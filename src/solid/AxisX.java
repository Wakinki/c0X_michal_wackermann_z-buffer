package solid;

import objectdata.Vertex;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

public class AxisX extends Solid{
    public AxisX() {
        vb.add(new Vertex(0, 0, 0, new Col(0xff0000)));
        vb.add(new Vertex(10, 0, 0, new Col(0xff0000)));

        ib.add(0);
        ib.add(1);
    }

    @Override
    public void setModel(Mat4 model) {

    }
}

