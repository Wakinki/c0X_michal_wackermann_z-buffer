package solid.curve;

import solid.Solid;
import transforms.Col;
import transforms.Point3D;

public class CoonsCurve extends Solid {
    private static final int DEFAULT_STEPS = 24;
    private static final Col DEFAULT_COLOR = new Col(0xffaa44);
    private Point3D p0, p1, p2, p3;
    private int steps;

    public CoonsCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3, int steps, Col color) {
        this.p0 = p0; this.p1 = p1; this.p2 = p2; this.p3 = p3;
        this.steps = steps;
        this.color = color;
        generateCurve();
        updateModel();
    }

    public CoonsCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3, int steps) {
        this(p0, p1, p2, p3, steps, DEFAULT_COLOR);
    }

    public CoonsCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3, Col color) {
        this(p0, p1, p2, p3, DEFAULT_STEPS, color);
    }

    public CoonsCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
        this(p0, p1, p2, p3, DEFAULT_STEPS, DEFAULT_COLOR);
    }

    private void generateCurve() {
        vb.clear();
        ib.clear();

        //Generování křivky podle Coonse
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double x = (1-t)*p0.getX() + t*p3.getX() + t*(1-t)*( (1-t)*(p1.getX()-p0.getX()) + t*(p2.getX()-p3.getX()) );
            double y = (1-t)*p0.getY() + t*p3.getY() + t*(1-t)*( (1-t)*(p1.getY()-p0.getY()) + t*(p2.getY()-p3.getY()) );
            double z = (1-t)*p0.getZ() + t*p3.getZ() + t*(1-t)*( (1-t)*(p1.getZ()-p0.getZ()) + t*(p2.getZ()-p3.getZ()) );
            vb.add(new Point3D(x, y, z));
            if (i > 0) { ib.add(i-1); ib.add(i); }
        }
    }

    public Point3D getP0() { return p0; }
    public CoonsCurve withP0(Point3D p0) { this.p0 = p0; generateCurve(); updateModel(); return this; }

    public Point3D getP1() { return p1; }
    public CoonsCurve withP1(Point3D p1) { this.p1 = p1; generateCurve(); updateModel(); return this; }

    public Point3D getP2() { return p2; }
    public CoonsCurve withP2(Point3D p2) { this.p2 = p2; generateCurve(); updateModel(); return this; }

    public Point3D getP3() { return p3; }
    public CoonsCurve withP3(Point3D p3) { this.p3 = p3; generateCurve(); updateModel(); return this; }

    public int getSteps() { return steps; }
    public CoonsCurve withSteps(int steps) { this.steps = steps; generateCurve(); updateModel(); return this; }

    @Override
    public void updateModel() {
        super.updateModel();
        generateCurve();
    }
}
