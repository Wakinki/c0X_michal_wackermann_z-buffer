package renderer;

import model.Line;
import rasterize.LineRasterizer;
import solid.Solid;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.List;

public class Renderer {

    private LineRasterizer lineRasterizer;
    private int width, height;
    private boolean isPerspectiveProjection = true;
    private Mat4 view, proj;

    public Renderer(LineRasterizer lineRasterizer, int width, int height) {
        this.lineRasterizer = lineRasterizer;
        this.width = width;
        this.height = height;
    }

    public void render(Solid solid) {
        // for cyklus, který projde ib a pro každý index načte vertex
        for (int i = 0; i < solid.getIb().size(); i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D pointA = solid.getVb().get(indexA);
            Point3D pointB = solid.getVb().get(indexB);

            // Model transformace z model space do world space
            pointA = pointA.mul(solid.getModel());
            pointB = pointB.mul(solid.getModel());
            // View transformace z world space do view space
            pointA = pointA.mul(view);
            pointB = pointB.mul(view);
            // Projections transformace z view space clip space
            pointA = pointA.mul(proj);
            pointB = pointB.mul(proj);

            // TODO: ořezání
            if(pointA.getW() <= 0 ||pointB.getW() <= 0)
                return;
            // TODO: dehomogenizace

            if(isPerspectiveProjection){
                pointA = new Point3D(
                        pointA.getX() / pointA.getW(),
                        pointA.getY() / pointA.getW(),
                        pointA.getZ() / pointA.getW()
                );

                pointB = new Point3D(
                        pointB.getX() / pointB.getW(),
                        pointB.getY() / pointB.getW(),
                        pointB.getZ() / pointB.getW()
                );
            }



            // TODO: transformace do okna obrazovky
            Vec3D pointAInWindow = transformToWindow(new Vec3D(pointA));
            Vec3D pointBInWindow = transformToWindow(new Vec3D(pointB));

            // rasterizace
            Line line = new Line(
                    (int) Math.round(pointAInWindow.getX()), (int) Math.round(pointAInWindow.getY()),
                    (int) Math.round(pointBInWindow.getX()), (int) Math.round(pointBInWindow.getY()),
                   0xffffff
            );
            lineRasterizer.setColor(solid.getColor().getRGB());
            lineRasterizer.rasterize(line);
        }
    }

    private Vec3D transformToWindow(Vec3D p) {
        return p.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((width - 1) / 2.f, (height - 1) / 2.f, 1));
    }

    public void renderSolids(List<Solid> solids) {
        // TODO: pro každý solid -> zavolám render
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }

    public boolean isPerspectiveProjection() {
        return isPerspectiveProjection;
    }

    public void setPerspectiveProjection(boolean perspectiveProjection) {
        isPerspectiveProjection = perspectiveProjection;
    }
}
