package solid;

import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;

public class AxisX extends Solid{
    public AxisX() {
        vb.add(new Point3D(0, 0, 0));
        vb.add(new Point3D(10, 0, 0));

        ib.add(0);
        ib.add(1);

        color = new Col(0xff0000);
    }

    @Override
    public void setModel(Mat4 model) {

    }
}

