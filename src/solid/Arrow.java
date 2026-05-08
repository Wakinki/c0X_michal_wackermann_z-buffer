package solid;

import transforms.Col;
import transforms.Point3D;

public class Arrow extends Solid {
    public Arrow() {
        // Naplnit VB
        vb.add(new Point3D(0,0,0));
        vb.add(new Point3D(0.8,0,0));
        vb.add(new Point3D(0.8,0,-0.2));
        vb.add(new Point3D(1,0,0));
        vb.add(new Point3D(0.8,0,0.2));

        // Naplnit IB
        ib.add(0);
        ib.add(1);

        ib.add(2);
        ib.add(3);

        ib.add(4);
        ib.add(2);

        ib.add(3);
        ib.add(4);
        color = new Col(0xffff00);
    }
}
