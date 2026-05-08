package solid;

import transforms.Col;
import transforms.Point3D;

public class Cylinder extends Solid {

    private static final int DEFAULT_SEGMENTS = 16;

    public Cylinder() {
        this(0.5, 1.0, DEFAULT_SEGMENTS, new Point3D(), new Col(0x00ffff));
    }

    public Cylinder(double radius, double height) {
        this(radius, height, DEFAULT_SEGMENTS, new Point3D(), new Col(0x00ffff));
    }

    public Cylinder(double radius, double height, Col col) {
        this(radius, height, DEFAULT_SEGMENTS, new Point3D(), col);
    }

    public Cylinder(double radius, double height, Point3D center) {
        this(radius, height, DEFAULT_SEGMENTS, center, new Col(0x00ffff));
    }

    public Cylinder(double radius, double height, Point3D center, Col col) {
        this(radius, height, DEFAULT_SEGMENTS, center, col);
    }

    public Cylinder(double radius, double height, int segments) {
        this(radius, height, segments, new Point3D(), new Col(0x00ffff));
    }

    public Cylinder(double radius, double height,
                    int segments, Point3D center, Col col) {
        buildCylinder(radius, height, segments, center);
        color = new Col(col);
    }

    private void buildCylinder(double r, double h, int n, Point3D c) {
        double halfH = h / 2.0;

        int baseIndex = vb.size();

        //Vrcholy
        for (int i = 0; i < n; i++) {
            double a = 2 * Math.PI * i / n;
            double x = Math.cos(a) * r;
            double y = Math.sin(a) * r;

            //spodní kruh
            vb.add(new Point3D(
                    c.getX() + x,
                    c.getY() + y,
                    c.getZ() - halfH
            ));

            //horní kruh
            vb.add(new Point3D(
                    c.getX() + x,
                    c.getY() + y,
                    c.getZ() + halfH
            ));
        }

        //Hrany
        for (int i = 0; i < n; i++) {
            int i0 = baseIndex + i * 2;
            int i1 = baseIndex + ((i + 1) % n) * 2;

            //spodní kruh
            ib.add(i0);
            ib.add(i1);

            //horní kruh
            ib.add(i0 + 1);
            ib.add(i1 + 1);

            //svislá hrana
            ib.add(i0);
            ib.add(i0 + 1);
        }
    }
}