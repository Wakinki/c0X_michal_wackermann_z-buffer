package solid;

import objectdata.Vertex;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

public class AxisZ extends Solid{
    public AxisZ() {
        vb.add(new Vertex(0, 0, 0,new Col(0x0000ff)));
        vb.add(new Vertex(0, 0, 10,new Col(0x0000ff)));

        ib.add(0);
        ib.add(1);
    }

    @Override
    public void setModel(Mat4 model) {

    }
}
