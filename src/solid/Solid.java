package solid;

import transforms.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Solid {
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>();//dvojice
    protected Col color = new Col(0xffffff);
    protected Mat4 model = new Mat4Identity();

    protected Vec3D position = new Vec3D(0, 0, 0);
    protected Vec3D rotation = new Vec3D(0, 0, 0); // stupně
    protected Vec3D scale    = new Vec3D(1, 1, 1);

    protected boolean dirty = true;

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public Mat4 getModel() {
        if (dirty) {
            updateModel();
        }
        return model;
    }

    public void updateModel() {
        model = new Mat4Transl(position)
                .mul(new Mat4RotXYZ(
                        Math.toRadians(rotation.getX()),
                        Math.toRadians(rotation.getY()),
                        Math.toRadians(rotation.getZ())
                ))
                .mul(new Mat4Scale(scale));
        dirty = false;
    }

    public List<Point3D> getVb() {
        return vb;
    }

    public Col getColor() {
        return color;
    }

    public void setColor(Col color) {
        this.color = color;
    }

    public List<Integer> getIb() {
        return ib;
    }

    public Vec3D getPosition() {
        return position;
    }

    public void setPosition(Vec3D position) {
        this.position = position;
    }

    public Vec3D getRotation() {
        return rotation;
    }

    public void setRotation(Vec3D rotation) {
        this.rotation = rotation;
    }

    public Vec3D getScale() {
        return scale;
    }

    public void setScale(Vec3D scale) {
        this.scale = scale;
    }
}
