package renderer;

import model.Line;
import model.Light;
import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;
import shaders.Shader;
import shaders.ShaderPhongAmbientDiffuse;
import textures.TextureMapper;
import transforms.*;
import utils.Math3DUtils;
import view.Panel;

import java.util.List;

/**
 * Converts 3D objects to 2D lines and triangles on screen.
 * The rendering pipeline performs the following steps:
 * <ol>
 *   <li>Takes all primitives from the object's index buffer (IB):
 *       <ul>
 *         <li>for lines: takes pairs of indices,</li>
 *         <li>for triangles: takes triples of indices,</li>
 *       </ul>
 *       which point to the vertex buffer (VB).</li>
 *   <li>For each vertex, performs the transformation pipeline:
 *       <ul>
 *         <li>model: model space -> world space,</li>
 *         <li>view: world space -> view space,</li>
 *         <li>projection: view space -> clip space.</li>
 *       </ul></li>
 *   <li>Performs clipping against the near plane in clip space:
 *       <ul>
 *         <li>for lines: draws the visible part, possibly calculating intersection with near plane,</li>
 *         <li>for triangles: depending on visible vertices, discards the primitive or creates one or two clipped triangles.</li>
 *       </ul></li>
 *   <li>Performs dehomogenization:
 *       <ul>
 *         <li>after projection, points are in homogeneous coordinates (x, y, z, w),</li>
 *         <li>before division, checks that w is not nearly zero (EPS_W) to avoid division by zero.</li>
 *       </ul></li>
 *   <li>Converts points from normalized coordinates (NDC) to window coordinates (screen space):
 *       <ul>
 *         <li>recalculates range <-1,1> to pixels according to current panel width and height,</li>
 *         <li>flips the Y axis (because in screen coordinates Y increases downward).</li>
 *       </ul></li>
 *   <li>Creates 2D line using lineRasterizer or triangle using triangleRasterizer.</li>
 * </ol>
 */
public class Renderer3D implements GPURenderer {

    /** Rendering mode for the renderer. */
    public enum RenderMode {
        /** Render only wireframe (lines). */
        WIREFRAME,

        /** Render filled triangles. */
        FILL
    }

    /** Rasterizer for line primitives. */
    private final LineRasterizer lineRasterizer;

    /** Panel for displaying the rendered output. */
    private final Panel panel;

    /** Model transformation matrix (model space -> world space). */
    private Mat4 model;

    /** View transformation matrix (world space -> view space). */
    private Mat4 view;

    /** Projection matrix (view space -> clip space). */
    private Mat4 projection;

    /** Rasterizer for triangle primitives. */
    private final TriangleRasterizer triangleRasterizer;

    /** Current rendering mode. */
    private RenderMode renderMode = RenderMode.FILL;

    /** Minimum w value to prevent division by zero during dehomogenization. */
    private static final double EPS_W = 1e-10;

    /** Clipper for near plane clipping. */
    private final Clipper clipper = new Clipper(EPS_W);

    /** Shader for lit rendering. */
    private Shader litShader =
            (a, b, c, wA, wB, wC) -> a.getCol().mul(wA)
                    .add(b.getCol().mul(wB))
                    .add(c.getCol().mul(wC));

    /** Shader for unlit rendering. */
    private final Shader unlitShader =
            (a, b, c, wA, wB, wC) -> a.getCol().mul(wA)
                    .add(b.getCol().mul(wB))
                    .add(c.getCol().mul(wC));

    /**
     * Creates a new 3D renderer.
     *
     * @param lineRasterizer the line rasterizer to use
     * @param triangleRasterizer the triangle rasterizer to use
     * @param panel the panel for displaying the output
     * @param view the view transformation matrix
     * @param projection the projection matrix
     */
    public Renderer3D(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer,
                      Panel panel, Mat4 view, Mat4 projection) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
        this.panel = panel;

        this.model = new Mat4Identity();

        this.view = view;
        this.projection = projection;
    }

    /**
     * Sets the light source for this renderer.
     *
     * @param light the light source to use for lighting calculations
     */
    public void setLight(Light light) {
        this.litShader = new ShaderPhongAmbientDiffuse(light, 0.2);
    }

    /**
     * Enables or disables lighting.
     *
     * @param enabled true to enable lighting, false to disable
     */
    public void setLightingEnabled(boolean enabled) {
        triangleRasterizer.setShader(enabled ? litShader : unlitShader);
    }

    @Override
    public void draw(List<Part> parts, List<Integer> ib, List<Vertex> vb) {
        drawParts(parts, ib, vb, false);
    }

    /**
     * Draws axes, ignoring the current render mode.
     *
     * @param parts the parts to draw
     * @param ib the index buffer
     * @param vb the vertex buffer
     */
    public void drawAxes(List<Part> parts, List<Integer> ib, List<Vertex> vb) {
        drawParts(parts, ib, vb, true);
    }

    /**
     * Processes individual parts of the object according to topology.
     * From the index buffer, takes:
     * <ul>
     *   <li>pairs of indices for lines,</li>
     *   <li>triples of indices for triangles,</li>
     * </ul>
     * which point to the vertex buffer.
     *
     * @param parts the parts to process
     * @param ib the index buffer
     * @param vb the vertex buffer
     * @param ignoreRenderMode if true, ignores the current render mode
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
     * Processes a part represented by a list of triangles.
     *
     * @param ib the index buffer
     * @param vb the vertex buffer
     * @param start the starting index in the index buffer
     * @param count the number of triangles
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
     * Processes a part represented by a list of line segments.
     *
     * @param ib the index buffer
     * @param vb the vertex buffer
     * @param start the starting index in the index buffer
     * @param count the number of line segments
     */
    private void drawLinePart(List<Integer> ib, List<Vertex> vb, int start, int count) {
        int end = start + count * 2;

        for (int i = start; i + 1 < end && i + 1 < ib.size(); i += 2) {
            int i1 = ib.get(i);
            int i2 = ib.get(i + 1);
            drawEdge(vb, i1, i2);
        }
    }

    /**
     * Draws edges with clipping against the near plane.
     * Procedure:
     * <ol>
     *   <li>transform vertices to clip space,</li>
     *   <li>clip against z before dehomogenization,</li>
     *   <li>dehomogenize,</li>
     *   <li>transform to window coordinates,</li>
     *   <li>rasterize the line segment.</li>
     * </ol>
     *
     * @param vb the vertex buffer
     * @param i1 index of the first vertex
     * @param i2 index of the second vertex
     */
    private void drawEdge(List<Vertex> vb, int i1, int i2) {
        Vertex v1 = vb.get(i1);
        Vertex v2 = vb.get(i2);

        // Model transformation (model) = model space -> world space
        // View transformation (view) = world space -> view space
        // Projection transformation = view space -> clip space
        Vertex a = transformToClip(v1);
        Vertex b = transformToClip(v2);

        // Clipping against near plane z = 0 before dehomogenization
        Vertex[] clipped = clipper.clipLineNear(a, b);
        if (clipped == null) return;

        a = clipped[0];
        b = clipped[1];

        // Dehomogenization - check for division by zero
        if (Math.abs(a.getW()) < EPS_W || Math.abs(b.getW()) < EPS_W) return;

        // Dehomogenization
        Point3D pointA = a.getPosition().mul(1 / a.getW());
        Point3D pointB = b.getPosition().mul(1 / b.getW());

        // Transformation to window coordinates = NDC -> screen space
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

    /**
     * Rasterizes an already clipped triangle.
     *
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     */
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

    /**
     * Draws a triangle with clipping.
     *
     * @param vb the vertex buffer
     * @param i1 index of the first vertex
     * @param i2 index of the second vertex
     * @param i3 index of the third vertex
     */
    private void drawTriangle(List<Vertex> vb, int i1, int i2, int i3) {
        Vertex v1 = vb.get(i1);
        Vertex v2 = vb.get(i2);
        Vertex v3 = vb.get(i3);

        Vertex aClip = transformToClip(v1);
        Vertex bClip = transformToClip(v2);
        Vertex cClip = transformToClip(v3);

        if (clipper.triangleFullyOutsideClip(aClip, bClip, cClip)) {
            return;
        }

        if (clipper.triangleFullyInsideClip(aClip, bClip, cClip)) {
            rasterizeClippedTriangle(aClip, bClip, cClip);
            return;
        }

        for (Vertex[] tri : clipper.clipTriangleNear(aClip, bClip, cClip)) {
            rasterizeClippedTriangle(tri[0], tri[1], tri[2]);
        }
    }

    /**
     * Transforms a vertex to clip space.
     * Applies model, view, and projection transformations.
     *
     * @param v the vertex to transform
     * @return the transformed vertex in clip space
     */
    private Vertex transformToClip(Vertex v) {
        Point3D worldPos = v.getPosition().mul(model);
        Point3D clipPos = worldPos.mul(view).mul(projection);
        Vec3D worldNormal = transformNormal(v.getNormal());

        return new Vertex(clipPos, v.getCol(), worldNormal, v.getU(), v.getV(), worldPos);
    }

    /**
     * Transforms a normal vector from model space to world space.
     *
     * @param normal the normal vector to transform
     * @return the transformed normal vector
     */
    private Vec3D transformNormal(Vec3D normal) {
        Point3D p0 = new Point3D(0, 0, 0).mul(model);
        Point3D p1 = new Point3D(normal.getX(), normal.getY(), normal.getZ()).mul(model);

        return Math3DUtils.normalize(new Vec3D(
                p1.getX() - p0.getX(),
                p1.getY() - p0.getY(),
                p1.getZ() - p0.getZ()
        ));
    }

    /**
     * Transforms points from NDC to screen space (window coordinates).
     *
     * @param p the point in NDC
     * @return the point in screen space
     */
    private Vec3D transformToWindow(Point3D p) {
        int width = panel.getRaster().getWidth(); // viewport width from panel
        int height = panel.getRaster().getHeight(); // viewport height from panel

        return new Vec3D(p)
                .mul(new Vec3D(1, -1, 1)) // flip Y axis
                .add(new Vec3D(1, 1, 0)) // shift from <-1,1> to <0,2>
                .mul(new Vec3D((width - 1) / 2.0, (height - 1) / 2.0, 1)); // convert to pixels
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

    /**
     * Toggles between wireframe and fill render modes.
     */
    public void toggleRenderMode() {
        if (renderMode == RenderMode.FILL) {
            renderMode = RenderMode.WIREFRAME;
        } else {
            renderMode = RenderMode.FILL;
        }
    }

    /**
     * Returns whether the renderer is in wireframe mode.
     *
     * @return true if in wireframe mode, false otherwise
     */
    public boolean isWireframeMode() {
        return renderMode == RenderMode.WIREFRAME;
    }

    /**
     * Sets the texture for triangle rasterization.
     *
     * @param texture the texture to use
     * @param enabled whether texture mapping should be enabled
     */
    public void setTexture(TextureMapper texture, boolean enabled) {
        triangleRasterizer.setTexture(texture);
        triangleRasterizer.setTextureEnabled(enabled);
    }
}