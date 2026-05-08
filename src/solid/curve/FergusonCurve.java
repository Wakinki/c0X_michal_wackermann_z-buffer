package solid.curve;

import solid.Solid;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.List;

public class FergusonCurve extends Solid {
    private static final int DEFAULT_STEPS = 24;
    private Point3D p0, p1, p2, p3;
    private int steps;

    public FergusonCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3, int steps, Col color) {
        this.p0 = p0; this.p1 = p1; this.p2 = p2; this.p3 = p3;
        this.steps = steps;

        generateCurve();
        updateModel();
    }

    public FergusonCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3, Col color) {
        this(p0, p1, p2, p3, DEFAULT_STEPS, color);
    }

    public FergusonCurve(Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
        this(p0, p1, p2, p3, DEFAULT_STEPS, new Col(0x6677AB)); // default steps
    }

    private void generateCurve() {
        vb.clear();
        ib.clear();

        //Generování křivky podle Fergusona
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;

            double x = p0.getX()*(2*t*t*t - 3*t*t + 1) + p1.getX()*(t*t*t - 2*t*t + t)
                    + p2.getX()*(-2*t*t*t + 3*t*t) + p3.getX()*(t*t*t - t*t);
            double y = p0.getY()*(2*t*t*t - 3*t*t + 1) + p1.getY()*(t*t*t - 2*t*t + t)
                    + p2.getY()*(-2*t*t*t + 3*t*t) + p3.getY()*(t*t*t - t*t);
            double z = p0.getZ()*(2*t*t*t - 3*t*t + 1) + p1.getZ()*(t*t*t - 2*t*t + t)
                    + p2.getZ()*(-2*t*t*t + 3*t*t) + p3.getZ()*(t*t*t - t*t);
//            vb.add(new Point3D(x, y, z));
            if (i > 0) { ib.add(i-1); ib.add(i); }
        }
    }


    public Point3D getP0() { return p0; }
    public FergusonCurve withP0(Point3D p0) { this.p0 = p0; generateCurve(); updateModel(); return this; }

    public Point3D getP1() { return p1; }
    public FergusonCurve withP1(Point3D p1) { this.p1 = p1; generateCurve(); updateModel(); return this; }

    public Point3D getP2() { return p2; }
    public FergusonCurve withP2(Point3D p2) { this.p2 = p2; generateCurve(); updateModel(); return this; }

    public Point3D getP3() { return p3; }
    public FergusonCurve withP3(Point3D p3) { this.p3 = p3; generateCurve(); updateModel(); return this; }

    public int getSteps() { return steps; }
    public FergusonCurve withSteps(int steps) { this.steps = steps; generateCurve(); updateModel(); return this; }

    @Override
    public void updateModel() {
        super.updateModel();
        generateCurve(); // ensure curve points update
    }
}