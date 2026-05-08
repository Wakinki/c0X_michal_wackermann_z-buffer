package solid;

import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

public class AxisZ extends Solid{
    public AxisZ() {
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 0, 10));

        ib.add(0);
        ib.add(1);

        color = new Col(0x0000ff);
    }

    @Override
    public void setModel(Mat4 model) {

    }
}
