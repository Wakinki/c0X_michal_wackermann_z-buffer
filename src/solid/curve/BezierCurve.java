package solid.curve;

import solid.Solid;
import transforms.Col;
import transforms.Point3D;

public class BezierCurve extends Solid {
    private static final int DEFAULT_STEPS = 24;
    private Point3D p0, p1, p2, p3;
    private int steps;

    public BezierCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3, int steps, Col color) {
        this.p0 = p0; this.p1 = p1; this.p2 = p2; this.p3 = p3;
        this.steps = steps;
        this.color = color;
        generateCurve();
        updateModel();
    }

    public BezierCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
        this(p0, p1, p2, p3, DEFAULT_STEPS, new Col(0x00ffaa));
    }

    public BezierCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3, Col color) {
        this(p0, p1, p2, p3, DEFAULT_STEPS, color);
    }

    private void generateCurve() {
        vb.clear();
        ib.clear();
        //Generování křivky podle Bezier
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double u = 1 - t;
            double x = u*u*u*p0.getX() + 3*u*u*t*p1.getX() + 3*u*t*t*p2.getX() + t*t*t*p3.getX();
            double y = u*u*u*p0.getY() + 3*u*u*t*p1.getY() + 3*u*t*t*p2.getY() + t*t*t*p3.getY();
            double z = u*u*u*p0.getZ() + 3*u*u*t*p1.getZ() + 3*u*t*t*p2.getZ() + t*t*t*p3.getZ();
            vb.add(new Point3D(x, y, z));
            if (i > 0) { ib.add(i-1); ib.add(i); }
        }
    }


    public Point3D getP0() { return p0; }
    public BezierCurve withP0(Point3D p0) { this.p0 = p0; generateCurve(); updateModel(); return this; }

    public Point3D getP1() { return p1; }
    public BezierCurve withP1(Point3D p1) { this.p1 = p1; generateCurve(); updateModel(); return this; }

    public Point3D getP2() { return p2; }
    public BezierCurve withP2(Point3D p2) { this.p2 = p2; generateCurve(); updateModel(); return this; }

    public Point3D getP3() { return p3; }
    public BezierCurve withP3(Point3D p3) { this.p3 = p3; generateCurve(); updateModel(); return this; }

    public int getSteps() { return steps; }
    public BezierCurve withSteps(int steps) { this.steps = steps; generateCurve(); updateModel(); return this; }

    @Override
    public void updateModel() {
        super.updateModel();
        generateCurve();
    }
}
