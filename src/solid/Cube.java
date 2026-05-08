package solid;

import transforms.Col;
import transforms.Point3D;



public class Cube extends Solid {

    public Cube() {
        this(1,new Point3D(), new Col(0x00ff00));
    }

    public Cube (double size){
        this(size, new Point3D(),new Col(0x00ff00));
    }

    public Cube (double size, Col col){
        this(size, new Point3D(),col);
    }

    public Cube (double size, Point3D center){
        this(size, center,new Col(0x00ff00));
    }

    public Cube(double size, Point3D center,Col col) {
        buildCube(size, center);
        color = new Col(col);
    }

    private void buildCube(double size, Point3D c) {
        double s = size / 2.0;

        // 1) Vrcholy
        for (int x = -1; x <= 1; x += 2) {
            for (int y = -1; y <= 1; y += 2) {
                for (int z = -1; z <= 1; z += 2) {
                    vb.add(new Point3D(
                            c.getX() + x * s,
                            c.getY() + y * s,
                            c.getZ() + z * s
                    ));
                }
            }
        }

        // 2) Hrany
        int vCount = vb.size();

        for (int i = 0; i < vCount; i++) {
            for (int j = i + 1; j < vCount; j++) {

                Point3D a = vb.get(i);
                Point3D b = vb.get(j);

                int diff = 0;
                if (a.getX() != b.getX()) diff++;
                if (a.getY() != b.getY()) diff++;
                if (a.getZ() != b.getZ()) diff++;

                if (diff == 1) {
                    ib.add(i);
                    ib.add(j);
                }
            }
        }
    }
}