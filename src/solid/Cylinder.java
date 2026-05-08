package solid;


import objectdata.Vertex;
import solid.helpers.IndexBuffers;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

/**
 * Represents a 3D cylinder solid.
 * The cylinder is defined by:
 * <ul>
 *   <li>radius of the base,</li>
 *   <li>height,</li>
 *   <li>number of segments around the circumference (default: 16),</li>
 *   <li>center position,</li>
 *   <li>color.</li>
 * </ul>
 * The cylinder consists of a side surface and two circular disks (top and bottom).
 */
public class Cylinder extends Solid {

    /** Default number of segments around the cylinder's circumference. */
    private static final int DEFAULT_SEGMENTS = 16;

    /**
     * Creates a cylinder with default radius (0.5), height (1.0), segments (16),
     * center at origin, and cyan color.
     */
    public Cylinder() {
        this(0.5, 1.0, DEFAULT_SEGMENTS, new Point3D(), new Col(0x00ffff));
    }

    /**
     * Creates a cylinder with the specified radius and height, default segments (16),
     * center at origin, and cyan color.
     *
     * @param radius the radius of the cylinder's base
     * @param height the height of the cylinder
     */
    public Cylinder(double radius, double height) {
        this(radius, height, DEFAULT_SEGMENTS, new Point3D(), new Col(0x00ffff));
    }

    /**
     * Creates a cylinder with the specified radius, height, and color, default segments (16),
     * and center at origin.
     *
     * @param radius the radius of the cylinder's base
     * @param height the height of the cylinder
     * @param col the color of the cylinder
     */
    public Cylinder(double radius, double height, Col col) {
        this(radius, height, DEFAULT_SEGMENTS, new Point3D(), col);
    }

    /**
     * Creates a cylinder with the specified radius, height, and center, default segments (16),
     * and cyan color.
     *
     * @param radius the radius of the cylinder's base
     * @param height the height of the cylinder
     * @param center the center position of the cylinder
     */
    public Cylinder(double radius, double height, Point3D center) {
        this(radius, height, DEFAULT_SEGMENTS, center, new Col(0x00ffff));
    }

    /**
     * Creates a cylinder with the specified radius, height, and center, default segments (16).
     *
     * @param radius the radius of the cylinder's base
     * @param height the height of the cylinder
     * @param center the center position of the cylinder
     * @param col the color of the cylinder
     */
    public Cylinder(double radius, double height, Point3D center, Col col) {
        this(radius, height, DEFAULT_SEGMENTS, center, col);
    }

    /**
     * Creates a cylinder with the specified radius, height, and number of segments,
     * center at origin, and cyan color.
     *
     * @param radius the radius of the cylinder's base
     * @param height the height of the cylinder
     * @param segments the number of segments around the circumference
     */
    public Cylinder(double radius, double height, int segments) {
        this(radius, height, segments, new Point3D(), new Col(0x00ffff));
    }

    /**
     * Creates a cylinder with the specified parameters.
     *
     * @param radius the radius of the cylinder's base
     * @param height the height of the cylinder
     * @param segments the number of segments around the circumference
     * @param center the center position of the cylinder
     * @param col the color of the cylinder
     */
    public Cylinder(double radius, double height, int segments, Point3D center, Col col) {
        buildCylinder(center.getX(), center.getY(), center.getZ(), radius, height, segments, col);
    }

    /**
     * Builds the cylinder geometry with the specified parameters.
     * Creates:
     * <ul>
     *   <li>the side surface with the specified number of segments,</li>
     *   <li>bottom disk with normal pointing downward,</li>
     *   <li>top disk with normal pointing upward.</li>
     * </ul>
     *
     * @param cx the x-coordinate of the cylinder center
     * @param cy the y-coordinate of the cylinder center
     * @param cz the z-coordinate of the cylinder center
     * @param radius the radius of the cylinder's base
     * @param height the height of the cylinder
     * @param segments the number of segments around the circumference
     * @param col the color of the cylinder
     */
    private void buildCylinder(double cx, double cy, double cz, double radius, double height, int segments, Col col) {
        double hz = height * 0.5;
        IndexBuffers buffers = new IndexBuffers();
        int sideStart = vb.size();

        for (int i = 0; i <= segments; i++) {
            double u = i / (double) segments;
            double a = 2.0 * Math.PI * u;
            double ca = Math.cos(a);
            double sa = Math.sin(a);

            Vec3D n = new Vec3D(ca, sa, 0);

            vb.add(new Vertex(cx + radius * ca, cy + radius * sa, cz - hz, col, n, u, 1.0));
            vb.add(new Vertex(cx + radius * ca, cy + radius * sa, cz + hz, col, n, u, 0.0));
        }

        for (int i = 0; i < segments; i++) {
            int b1 = sideStart + i * 2;
            int t1 = b1 + 1;
            int b2 = sideStart + (i + 1) * 2;
            int t2 = b2 + 1;

            buffers.addLine(b1, t1);
            buffers.addLine(b1, b2);
            buffers.addLine(t1, t2);

            buffers.addTri(b1, b2, t1);
            buffers.addTri(b2, t2, t1);
        }

        addDisk(buffers, cx, cy, cz - hz, radius, segments, col, new Vec3D(0, 0, -1), true);
        addDisk(buffers, cx, cy, cz + hz, radius, segments, col, new Vec3D(0, 0, 1), false);

        buffers.flushTo(this);
    }

    /**
     * Adds a circular disk to the cylinder.
     *
     * @param buffers the index buffers to add to
     * @param cx the x-coordinate of the disk center
     * @param cy the y-coordinate of the disk center
     * @param cz the z-coordinate of the disk center
     * @param radius the radius of the disk
     * @param segments the number of segments around the disk
     * @param col the color of the disk
     * @param normal the normal vector for the disk (points outward)
     * @param reverse if true, reverses the winding order of triangles (for bottom disk)
     */
    private void addDisk(IndexBuffers buffers, double cx, double cy, double cz, double radius, int segments, Col col, Vec3D normal, boolean reverse) {
        int center = vb.size();
        vb.add(new Vertex(cx, cy, cz, col, normal, 0.5, 0.5));

        int start = vb.size();
        for (int i = 0; i < segments; i++) {
            double u = i / (double) segments;
            double a = 2.0 * Math.PI * u;
            double ca = Math.cos(a);
            double sa = Math.sin(a);

            vb.add(new Vertex(
                    cx + radius * ca,
                    cy + radius * sa,
                    cz,
                    col,
                    normal,
                    0.5 + 0.5 * ca,
                    0.5 - 0.5 * sa
            ));
        }

        for (int i = 0; i < segments; i++) {
            int ni = (i + 1) % segments;
            int a = start + i;
            int b = start + ni;

            buffers.addLine(a, b);

            if (reverse) {
                buffers.addTri(center, b, a);
            } else {
                buffers.addTri(center, a, b);
            }
        }
    }
}