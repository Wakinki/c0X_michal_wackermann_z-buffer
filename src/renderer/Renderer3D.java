package renderer;

import model.Line;
import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;
import shaders.TextureMapper;
import transforms.*;
import utils.Math3DUtils;
import view.Panel;

import java.util.List;

/**
 * Třída Renderer3D převádí 3D objekty na 2D úsečky a trojúhelníky na obrazovce.
 * 1) Vezme všechna primitiva objektu z index bufferu (IB).
 *    - pro úsečky bere dvojice indexů,
 *    - pro trojúhelníky bere trojice indexů,
 *    které ukazují do vertex bufferu (VB).
 * 2) Pro každý vrchol provede transformační řetězec:
 *    - model (modelovací transformace): model space -> world space
 *    - view  (pohledová transformace): world space -> view space
 *    - proj  (projekce): view space -> clip space
 * 3) Provede ořezání podle near plane v clip prostoru:
 *    - u úsečky vykreslí viditelnou část, případně dopočítá průsečík s near plane,
 *    - u trojúhelníku podle počtu viditelných vrcholů primitivum zahodí,
 *      nebo vytvoří jeden či dva ořezané trojúhelníky.
 * 4) Provede dehomogenizaci:
 *    - po projekci jsou body v homogenních souřadnicích (x, y, z, w),
 *    - před dělením se kontroluje, že w není skoro nula (EPS_W), aby nevzniklo dělení nulou.
 * 5) Převede body z normalizovaných souřadnic (NDC) do souřadnic okna (screen space):
 *    - přepočítá rozsah <-1,1> na pixely podle aktuální šířky a výšky panelu,
 *    - otočí osu Y (protože v obrazovce roste Y dolů).
 * 6) Vytvoří 2D úsečku pomocí lineRasterizeru nebo trojúhelník pomocí triangleRasterizeru.
 */
public class Renderer3D implements GPURenderer {

    public enum RenderMode {WIREFRAME, FILL}

    private final LineRasterizer lineRasterizer;
    private final Panel panel;
    private Mat4 model, view, projection;
    private final TriangleRasterizer triangleRasterizer;

    private RenderMode renderMode = RenderMode.FILL;

    private static final double EPS_W = 1e-10;
    private final Clipper clipper = new Clipper(EPS_W);

    private shaders.Shader litShader =
            (a, b, c, wA, wB, wC) -> a.getCol().mul(wA)
                    .add(b.getCol().mul(wB))
                    .add(c.getCol().mul(wC));

    private final shaders.Shader unlitShader =
            (a, b, c, wA, wB, wC) -> a.getCol().mul(wA)
                    .add(b.getCol().mul(wB))
                    .add(c.getCol().mul(wC));

    public Renderer3D(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer,
                      Panel panel, Mat4 view, Mat4 projection) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.panel = panel;

        this.model = new Mat4Identity();

        this.view = view;
        this.projection = projection;
    }


    public void setLight(model.Light light) {
        this.litShader = new shaders.ShaderPhongAmbientDiffuse(light, 0.2);
    }

    public void setLightingEnabled(boolean enabled) {
        triangleRasterizer.setShader(enabled ? litShader : unlitShader);
    }

    @Override
    public void draw(List<Part> parts, List<Integer> ib, List<Vertex> vb) {
        drawParts(parts, ib, vb, false);
    }

    public void drawAxes(List<Part> parts, List<Integer> ib, List<Vertex> vb) {
        drawParts(parts, ib, vb, true);
    }

    /**
     * Zpracování jednotlivých částí objektu podle topologie.
     * Podle topologie se z ib vezmeme:
     * - dvojice indexů pro úsečky,
     * - trojice indexů pro trojúhelníky.
     */
    private void drawParts(List<Part> parts, List<Integer> ib, List<Vertex> vb, boolean ignoreRenderMode) {
        for (Part part : parts) {
            final TopologyType topologyType = part.getTopologyType();
            final int start = part.getIndex();
            final int count = part.getCount();

            if (topologyType == TopologyType.LINE) {
                if (ignoreRenderMode || renderMode == RenderMode.WIREFRAME) {
                    drawLinePart(ib, vb, start, count);
                }
            } else if (topologyType == TopologyType.TRIANGLE) {
                if (ignoreRenderMode || renderMode == RenderMode.FILL) {
                    drawTrianglePart(ib, vb, start, count);
                }
            }
        }
    }

    /**
     * Zpracování části reprezentované seznamem trojúhelníků.
     */
    private void drawTrianglePart(List<Integer> ib, List<Vertex> vb, int start, int count) {
        int end = start + count * 3;

        for (int i = start; i + 2 < end && i + 2 < ib.size(); i += 3) {
            int i1 = ib.get(i);
            int i2 = ib.get(i + 1);
            int i3 = ib.get(i + 2);
            drawTriangle(vb, i1, i2, i3);
        }
    }

    /**
     * Zpracování části reprezentované seznamem úseček.
     */
    private void drawLinePart(List<Integer> ib, List<Vertex> vb, int start, int count) {
        int end = start + count * 2;

        for (int i = start; i + 1 < end && i + 1 < ib.size(); i += 2) {
            int i1 = ib.get(i);
            int i2 = ib.get(i + 1);
            drawEdge(vb, i1, i2);
        }
    }

    /** Vykreslení hran objektů s ořezáním podle near plane.
     * Postup:
     * 1) transformace vrcholů do clip space,
     * 2) ořezání podle z před dehomogenizací,
     * 3) dehomogenizace,
     * 4) transformace do okna,
     * 5) rasterizace úsečky.
     */
    private void drawEdge(List<Vertex> vb, int i1, int i2) {
        Vertex v1 = vb.get(i1);
        Vertex v2 = vb.get(i2);

        // Modelovací transformace (model) = model space -> world space
        // Pohledová tranformace (view) = world space -> view space
        // Projekční tranformace = view space -> clip space
        Vertex a = transformToClip(v1);
        Vertex b = transformToClip(v2);

        // Ořezání podle near plane z = 0 před dehomogenizací
        Vertex[] clipped = clipper.clipLineNear(a, b);
        if (clipped == null) return;

        a = clipped[0];
        b = clipped[1];

        // dehomogenizace - kontrola dělení nulou
        if (Math.abs(a.getW()) < EPS_W || Math.abs(b.getW()) < EPS_W) return;

        // dehomogenizace
        Point3D pointA = a.getPosition().mul(1 / a.getW());
        Point3D pointB = b.getPosition().mul(1 / b.getW());

        // Transformace do okna obrazovky = NDC -> screen space
        Vec3D vecA = transformToWindow(pointA);
        Vec3D vecB = transformToWindow(pointB);

        int x1 = (int) Math.round(vecA.getX());
        int y1 = (int) Math.round(vecA.getY());
        int x2 = (int) Math.round(vecB.getX());
        int y2 = (int) Math.round(vecB.getY());

        double z1 = vecA.getZ();
        double z2 = vecB.getZ();

        Col c = a.getCol();
        int rgb = 0xffffff;
        if (c != null) {
            rgb = c.getRGB();
        }

        lineRasterizer.rasterize(new Line(x1, y1, z1, x2, y2, z2, rgb));
    }

    /** Rasterizace již ořezaného trojúhelníku. */
    private void rasterizeClippedTriangle(Vertex a, Vertex b, Vertex c) {
        if (Math.abs(a.getW()) < EPS_W || Math.abs(b.getW()) < EPS_W || Math.abs(c.getW()) < EPS_W) return;

        Point3D p1 = a.getPosition().mul(1.0 / a.getW());
        Point3D p2 = b.getPosition().mul(1.0 / b.getW());
        Point3D p3 = c.getPosition().mul(1.0 / c.getW());

        Vec3D s1 = transformToWindow(p1);
        Vec3D s2 = transformToWindow(p2);
        Vec3D s3 = transformToWindow(p3);

        Vertex va = new Vertex(
                new Point3D(s1.getX(), s1.getY(), s1.getZ()),
                a.getCol(), a.getNormal(), a.getU(), a.getV(), a.getWorldPosition()
        );
        Vertex vb = new Vertex(
                new Point3D(s2.getX(), s2.getY(), s2.getZ()),
                b.getCol(), b.getNormal(), b.getU(), b.getV(), b.getWorldPosition()
        );
        Vertex vc = new Vertex(
                new Point3D(s3.getX(), s3.getY(), s3.getZ()),
                c.getCol(), c.getNormal(), c.getU(), c.getV(), c.getWorldPosition()
        );

        triangleRasterizer.rasterize(va, vb, vc);
    }

    private void drawTriangle(List<Vertex> vb, int i1, int i2, int i3) {
        Vertex v1 = vb.get(i1);
        Vertex v2 = vb.get(i2);
        Vertex v3 = vb.get(i3);

        Vertex aClip = transformToClip(v1);
        Vertex bClip = transformToClip(v2);
        Vertex cClip = transformToClip(v3);

        if (clipper.triangleFullyOutsideClip(aClip, bClip, cClip)) {return;}

        if (clipper.triangleFullyInsideClip(aClip, bClip, cClip)) {
            rasterizeClippedTriangle(aClip, bClip, cClip);
            return;
        }

        for (Vertex[] tri : clipper.clipTriangleNear(aClip, bClip, cClip)) {
            rasterizeClippedTriangle(tri[0], tri[1], tri[2]);
        }
    }

    private Vertex transformToClip(Vertex v) {
        Point3D worldPos = v.getPosition().mul(model);
        Point3D clipPos = worldPos.mul(view).mul(projection);
        Vec3D worldNormal = transformNormal(v.getNormal());

        return new Vertex(clipPos, v.getCol(), worldNormal, v.getU(), v.getV(), worldPos);
    }

    private Vec3D transformNormal(Vec3D normal) {
        Point3D p0 = new Point3D(0, 0, 0).mul(model);
        Point3D p1 = new Point3D(normal.getX(), normal.getY(), normal.getZ()).mul(model);

        return Math3DUtils.normalize(new Vec3D(
                p1.getX() - p0.getX(),
                p1.getY() - p0.getY(),
                p1.getZ() - p0.getZ()
        ));
    }

    /** Transformace bodů do pixelu okna = NDC -> screen space */
    private Vec3D transformToWindow(Point3D p) {
        int width = panel.getRaster().getWidth(); // sirka viewportu z panelu
        int height = panel.getRaster().getHeight(); // vyska viewportu z panelu

        return new Vec3D(p)
                .mul(new Vec3D(1, -1, 1)) // otočí osu Y
                .add(new Vec3D(1, 1, 0)) // posune z <-1,1> do <0,2>
                .mul(new Vec3D((width - 1) / 2.0, (height - 1) / 2.0, 1)); // prepocet na pixely
    }

    @Override
    public void setModel(Mat4 model) {
        if (model == null) {
            this.model = new Mat4Identity();
        } else {
            this.model = model;
        }
    }

    @Override
    public void setView(Mat4 view) {
        if (view == null) {
            this.view = new Mat4Identity();
        } else {
            this.view = view;
        }
    }

    @Override
    public void setProjection(Mat4 projection) {
        if (projection == null) {
            this.projection = new Mat4Identity();
        } else {
            this.projection = projection;
        }
    }

    public void toggleRenderMode() {
        if (renderMode == RenderMode.FILL) {
            renderMode = RenderMode.WIREFRAME;
        } else {
            renderMode = RenderMode.FILL;
        }
    }

    public boolean isWireframeMode() {
        return renderMode == RenderMode.WIREFRAME;
    }

    public void setTexture(TextureMapper texture, boolean enabled) {
        triangleRasterizer.setTexture(texture);
        triangleRasterizer.setTextureEnabled(enabled);
    }
}

