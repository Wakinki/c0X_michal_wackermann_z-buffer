package solid;

import objectdata.Vertex;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

public class AxisY extends Solid{
    public AxisY() {
        vb.add(new Vertex(0, 0, 0, new Col(0x00ff00)));
        vb.add(new Vertex(0, 10, 0, new Col(0x00ff00)));

        ib.add(0);
        ib.add(1);
    }

    @Override
    public void setModel(Mat4 model) {

    }
}

