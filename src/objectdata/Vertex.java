package objectdata;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

/**
 * Represents a single vertex of a 3D geometric body.
 * Stores:
 * <ul>
 *   <li>the vertex position in homogeneous coordinates,</li>
 *   <li>the vertex color,</li>
 *   <li>the normal for lighting calculations,</li>
 *   <li>the texture coordinates u and v.</li>
 * </ul>
 * The class also supports basic vector space operations (scalar multiplication and addition) to enable linear interpolation of vertices during rasterization.
 */
public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col col;
    private final Vec3D normal;
    private final double u;
    private final double v;
    private final Point3D worldPosition;

    public Vertex(double x, double y, double z, Col color) {
        this(new Point3D(x, y, z), color, new Vec3D(0, 0, 1), 0.0, 0.0, new Point3D(x, y, z));
    }

    public Vertex(double x, double y, double z, Col color, Vec3D normal, double u, double v) {
        this(new Point3D(x, y, z), color, normal, u, v, new Point3D(x, y, z));
    }

    public Vertex(Point3D position, Col col, Vec3D normal, double u, double v, Point3D worldPosition) {
        this.position = position;
        this.col = col;
        this.normal = normal;
        this.u = u;
        this.v = v;
        this.worldPosition = worldPosition;
    }

    public Vertex mul(double t) {
        return new Vertex(
                position.mul(t),
                col.mul(t),
                normal.mul(t),
                u * t,
                v * t,
                worldPosition.mul(t)
        );
    }

    public Vertex add(Vertex other) {
        return new Vertex(
                position.add(other.getPosition()),
                col.add(other.col),
                normal.add(other.normal),
                u + other.u,
                v + other.v,
                worldPosition.add(other.worldPosition)
        );
    }

    public Point3D getWorldPosition() {return worldPosition;}

    public Point3D getPosition() { return position; }
    public Col getCol() { return col; }
    public Vec3D getNormal() { return normal; }
    public double getU() { return u; }
    public double getV() { return v; }

    public double getX() { return position.getX(); }
    public double getY() { return position.getY(); }
    public double getZ() { return position.getZ(); }
    public double getW() { return position.getW(); }
}