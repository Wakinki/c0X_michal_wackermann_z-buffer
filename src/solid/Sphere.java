package solid;

import objectdata.Vertex;
import solid.helpers.IndexBuffers;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

/**
 * Represents a 3D sphere solid.
 * The sphere is defined by:
 * <ul>
 *   <li>radius,</li>
 *   <li>number of stacks (vertical divisions),</li>
 *   <li>number of slices (horizontal divisions),</li>
 *   <li>center position,</li>
 *   <li>color.</li>
 * </ul>
 * Vertices are generated using spherical coordinates with proper normals and texture coordinates.
 */
public class Sphere extends Solid {

    /** Default number of vertical divisions (parallels). */
    private static final int DEFAULT_STACKS = 16;

    /** Default number of horizontal divisions (meridians). */
    private static final int DEFAULT_SLICES = 16;

    /**
     * Creates a sphere with default radius (0.5), stacks (16), slices (16),
     * center at origin, and white color.
     */
    public Sphere() {
        this(0.5, DEFAULT_STACKS, DEFAULT_SLICES, new Point3D(), new Col(0xffffff));
    }

    /**
     * Creates a sphere with the specified radius, default stacks (16), slices (16),
     * center at origin, and white color.
     *
     * @param radius the radius of the sphere
     */
    public Sphere(double radius) {
        this(radius, DEFAULT_STACKS, DEFAULT_SLICES, new Point3D(), new Col(0xffffff));
    }

    /**
     * Creates a sphere with the specified radius and color, default stacks (16), slices (16),
     * and center at origin.
     *
     * @param radius the radius of the sphere
     * @param col the color of the sphere
     */
    public Sphere(double radius, Col col) {
        this(radius, DEFAULT_STACKS, DEFAULT_SLICES, new Point3D(), col);
    }

    /**
     * Creates a sphere with the specified radius and center, default stacks (16), slices (16),
     * and white color.
     *
     * @param radius the radius of the sphere
     * @param center the center position of the sphere
     */
    public Sphere(double radius, Point3D center) {
        this(radius, DEFAULT_STACKS, DEFAULT_SLICES, center, new Col(0xffffff));
    }

    /**
     * Creates a sphere with the specified radius, center, and color, default stacks (16) and slices (16).
     *
     * @param radius the radius of the sphere
     * @param center the center position of the sphere
     * @param col the color of the sphere
     */
    public Sphere(double radius, Point3D center, Col col) {
        this(radius, DEFAULT_STACKS, DEFAULT_SLICES, center, col);
    }

    /**
     * Creates a sphere with the specified radius, stacks, and slices,
     * center at origin, and white color.
     *
     * @param radius the radius of the sphere
     * @param stacks the number of vertical divisions (parallels)
     * @param slices the number of horizontal divisions (meridians)
     */
    public Sphere(double radius, int stacks, int slices) {
        this(radius, stacks, slices, new Point3D(), new Col(0xffffff));
    }

    /**
     * Creates a sphere with the specified parameters.
     *
     * @param radius the radius of the sphere
     * @param stacks the number of vertical divisions (parallels)
     * @param slices the number of horizontal divisions (meridians)
     * @param center the center position of the sphere
     * @param col the color of the sphere
     */
    public Sphere(double radius, int stacks, int slices, Point3D center, Col col) {
        buildSphere(center.getX(), center.getY(), center.getZ(), radius, stacks, slices, col);
    }

    /**
     * Builds the sphere geometry with the specified parameters.
     * Generates vertices using spherical coordinates:
     * <ul>
     *   <li>phi (latitude) ranges from -π/2 to π/2,</li>
     *   <li>theta (longitude) ranges from 0 to 2π,</li>
     *   <li>each vertex has a normal pointing outward from the center,</li>
     *   <li>texture coordinates are mapped accordingly.</li>
     * </ul>
     * Creates lines for wireframe rendering and triangles for solid rendering.
     *
     * @param cx the x-coordinate of the sphere center
     * @param cy the y-coordinate of the sphere center
     * @param cz the z-coordinate of the sphere center
     * @param radius the radius of the sphere
     * @param stacks the number of vertical divisions (parallels)
     * @param slices the number of horizontal divisions (meridians)
     * @param col the color of the sphere
     */
    private void buildSphere(double cx, double cy, double cz, double radius, int stacks, int slices, Col col) {
        IndexBuffers buffers = new IndexBuffers();
        int vertsPerRow = slices + 1;

        for (int i = 0; i <= stacks; i++) {
            double v = i / (double) stacks;
            double phi = Math.PI * (v - 0.5);
            double cosPhi = Math.cos(phi);
            double sinPhi = Math.sin(phi);

            for (int j = 0; j <= slices; j++) {
                double u = j / (double) slices;
                double theta = 2.0 * Math.PI * u;
                double cosTheta = Math.cos(theta);
                double sinTheta = Math.sin(theta);

                double nx = cosPhi * cosTheta;
                double ny = cosPhi * sinTheta;
                double nz = sinPhi;

                double x = cx + radius * nx;
                double y = cy + radius * ny;
                double z = cz + radius * nz;

                vb.add(new Vertex(x, y, z, col, new Vec3D(nx, ny, nz), u, 1.0 - v));
            }
        }

        for (int i = 0; i <= stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int a = i * vertsPerRow + j;
                buffers.addLine(a, a + 1);
            }
        }

        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j <= slices; j++) {
                int a = i * vertsPerRow + j;
                buffers.addLine(a, (i + 1) * vertsPerRow + j);
            }
        }

        for (int i = 0; i < stacks; i++) {
            for (int j = 0; j < slices; j++) {
                int a = i * vertsPerRow + j;
                int b = a + 1;
                int c = (i + 1) * vertsPerRow + j;
                int d = c + 1;

                buffers.addTri(a, b, c);
                buffers.addTri(b, d, c);
            }
        }

        buffers.flushTo(this);
    }
}