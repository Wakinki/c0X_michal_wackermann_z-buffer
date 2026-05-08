package solid;

import objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;

public class Arrow extends Solid {
    public Arrow() {
        // Naplnit VB
        vb.add(new Vertex(0,0,0, new Col(0xffff00)));
        vb.add(new Vertex(0.8,0,0, new Col(0xffff00)));
        vb.add(new Vertex(0.8,0,-0.2, new Col(0xffff00)));
        vb.add(new Vertex(1,0,0, new Col(0xffff00)));
        vb.add(new Vertex(0.8,0,0.2, new Col(0xffff00)));

        // Naplnit IB
        ib.add(0);
        ib.add(1);

        ib.add(2);
        ib.add(3);

        ib.add(4);
        ib.add(2);

        ib.add(3);
        ib.add(4);

    }
}
