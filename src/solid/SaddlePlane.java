package solid;

import transforms.Col;
import transforms.Point3D;

public class SaddlePlane extends Solid {

    private static final int DEFAULT_STEPS = 20;

    public SaddlePlane() {
        this(1.0, 1.0, 1.0, DEFAULT_STEPS, new Point3D(), new Col(0xff00ff));
    }

    public SaddlePlane(double sizeX, double sizeY, double height) {
        this(sizeX, sizeY, height, DEFAULT_STEPS, new Point3D(), new Col(0xff00ff));
    }

    public SaddlePlane(double sizeX, double sizeY, double height, Col col) {
        this(sizeX, sizeY, height, DEFAULT_STEPS, new Point3D(), col);
    }

    public SaddlePlane(double sizeX, double sizeY, double height, Point3D center) {
        this(sizeX, sizeY, height, DEFAULT_STEPS, center, new Col(0xff00ff));
    }

    public SaddlePlane(double sizeX, double sizeY, double height,
                       int steps, Point3D center, Col col) {
        buildSaddle(sizeX, sizeY, height, steps, center);
        color = new Col(col);
    }

    private void buildSaddle(double sx, double sy, double h,
                             int n, Point3D c) {

        int cols = n + 1;
        int rows = n + 1;

        //Vrcholy
        for (int iy = 0; iy <= n; iy++) {
            double y = lerp(-sy, sy, (double) iy / n);

            for (int ix = 0; ix <= n; ix++) {
                double x = lerp(-sx, sx, (double) ix / n);

                //sedlová funkce
                double z = h * ((x * x) / (sx * sx) - (y * y) / (sy * sy));

                vb.add(new Point3D(
                        c.getX() + x,
                        c.getY() + y,
                        c.getZ() + z
                ));
            }
        }

        //Hrany
        for (int iy = 0; iy < rows; iy++) {
            for (int ix = 0; ix < cols; ix++) {

                int i = iy * cols + ix;

                //vodorovné spojení
                if (ix + 1 < cols) {
                    ib.add(i);
                    ib.add(i + 1);
                }

                //svislé spojení
                if (iy + 1 < rows) {
                    ib.add(i);
                    ib.add(i + cols);
                }
            }
        }
    }

    //lineární interpolace
    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
}