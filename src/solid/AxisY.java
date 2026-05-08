package solid;

import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

public class AxisY extends Solid{
    public AxisY() {
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(0, 10, 0));

        ib.add(0);
        ib.add(1);

        color = new Col(0x00ff00);
    }

    @Override
    public void setModel(Mat4 model) {

    }
}

