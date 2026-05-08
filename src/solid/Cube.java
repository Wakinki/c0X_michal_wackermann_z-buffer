package solid;

import objectdata.Vertex;
import solid.helpers.IndexBuffers;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.Arrays;

/**
 * Represents a 3D cube solid.
 * The cube is centered at the specified position and can be configured with:
 * <ul>
 *   <li>size (edge length),</li>
 *   <li>center position,</li>
 *   <li>color.</li>
 * </ul>
 * Each face has appropriate normals for lighting and texture coordinates for mapping.
 */
public class Cube extends Solid {

    /**
     * Creates a cube with default size (1), center at origin, and green color.
     */
    public Cube() {
        this(1, new Point3D(), new Col(0x00ff00));
    }

    /**
     * Creates a cube with the specified size, center at origin, and green color.
     *
     * @param size the edge length of the cube
     */
    public Cube(double size) {
        this(size, new Point3D(), new Col(0x00ff00));
    }

    /**
     * Creates a cube with the specified size, center at origin, and color.
     *
     * @param size the edge length of the cube
     * @param col the color of the cube
     */
    public Cube(double size, Col col) {
        this(size, new Point3D(), col);
    }

    /**
     * Creates a cube with the specified size, center position, and green color.
     *
     * @param size the edge length of the cube
     * @param center the center position of the cube
     */
    public Cube(double size, Point3D center) {
        this(size, center, new Col(0x00ff00));
    }

    /**
     * Creates a cube with the specified size, center position, and color.
     *
     * @param size the edge length of the cube
     * @param center the center position of the cube
     * @param col the color of the cube
     */
    public Cube(double size, Point3D center, Col col) {
        buildCube(center.getX(), center.getY(), center.getZ(), size, col);
    }

    /**
     * Builds the cube geometry with the specified parameters.
     * Creates 6 faces (back, front, bottom, top, left, right), each with:
     * <ul>
     *   <li>4 vertices with appropriate positions,</li>
     *   <li>normals pointing outward,</li>
     *   <li>texture coordinates.</li>
     * </ul>
     *
     * @param cx the x-coordinate of the cube center
     * @param cy the y-coordinate of the cube center
     * @param cz the z-coordinate of the cube center
     * @param size the edge length of the cube
     * @param col the color of the cube
     */
    private void buildCube(double cx, double cy, double cz, double size, Col col) {
        double h = size * 0.5;
        IndexBuffers buffers = new IndexBuffers();

        // Back face: z = cz - h, normal (0, 0, -1)
        addFace(buffers, new Vertex[]{
                new Vertex(cx - h, cy - h, cz - h, col, new Vec3D(0, 0, -1), 0, 1),
                new Vertex(cx + h, cy - h, cz - h, col, new Vec3D(0, 0, -1), 1, 1),
                new Vertex(cx + h, cy + h, cz - h, col, new Vec3D(0, 0, -1), 1, 0),
                new Vertex(cx - h, cy + h, cz - h, col, new Vec3D(0, 0, -1), 0, 0)
        }, 0, 1, 2, 0, 2, 3);

        // Front face: z = cz + h, normal (0, 0, 1)
        addFace(buffers, new Vertex[]{
                new Vertex(cx - h, cy - h, cz + h, col, new Vec3D(0, 0, 1), 0, 1),
                new Vertex(cx + h, cy - h, cz + h, col, new Vec3D(0, 0, 1), 1, 1),
                new Vertex(cx + h, cy + h, cz + h, col, new Vec3D(0, 0, 1), 1, 0),
                new Vertex(cx - h, cy + h, cz + h, col, new Vec3D(0, 0, 1), 0, 0)
        }, 0, 2, 1, 0, 3, 2);

        // Bottom face: y = cy - h, normal (0, -1, 0)
        addFace(buffers, new Vertex[]{
                new Vertex(cx - h, cy - h, cz - h, col, new Vec3D(0, -1, 0), 0, 1),
                new Vertex(cx + h, cy - h, cz - h, col, new Vec3D(0, -1, 0), 1, 1),
                new Vertex(cx + h, cy - h, cz + h, col, new Vec3D(0, -1, 0), 1, 0),
                new Vertex(cx - h, cy - h, cz + h, col, new Vec3D(0, -1, 0), 0, 0)
        }, 0, 2, 1, 0, 3, 2);

        // Top face: y = cy + h, normal (0, 1, 0)
        addFace(buffers, new Vertex[]{
                new Vertex(cx - h, cy + h, cz - h, col, new Vec3D(0, 1, 0), 0, 1),
                new Vertex(cx + h, cy + h, cz - h, col, new Vec3D(0, 1, 0), 1, 1),
                new Vertex(cx + h, cy + h, cz + h, col, new Vec3D(0, 1, 0), 1, 0),
                new Vertex(cx - h, cy + h, cz + h, col, new Vec3D(0, 1, 0), 0, 0)
        }, 0, 1, 2, 0, 2, 3);

        // Left face: x = cx - h, normal (-1, 0, 0)
        addFace(buffers, new Vertex[]{
                new Vertex(cx - h, cy - h, cz - h, col, new Vec3D(-1, 0, 0), 0, 1),
                new Vertex(cx - h, cy + h, cz - h, col, new Vec3D(-1, 0, 0), 1, 1),
                new Vertex(cx - h, cy + h, cz + h, col, new Vec3D(-1, 0, 0), 1, 0),
                new Vertex(cx - h, cy - h, cz + h, col, new Vec3D(-1, 0, 0), 0, 0)
        }, 0, 1, 2, 0, 2, 3);

        // Right face: x = cx + h, normal (1, 0, 0)
        addFace(buffers, new Vertex[]{
                new Vertex(cx + h, cy - h, cz - h, col, new Vec3D(1, 0, 0), 0, 1),
                new Vertex(cx + h, cy + h, cz - h, col, new Vec3D(1, 0, 0), 1, 1),
                new Vertex(cx + h, cy + h, cz + h, col, new Vec3D(1, 0, 0), 1, 0),
                new Vertex(cx + h, cy - h, cz + h, col, new Vec3D(1, 0, 0), 0, 0)
        }, 0, 2, 1, 0, 3, 2);

        buffers.flushTo(this);
    }

    /**
     * Adds a face to the solid's buffers.
     * Adds 4 vertices for the face edges and 2 triangles for the face surface.
     *
     * @param buffers the index buffers to add to
     * @param verts the 4 vertices of the face
     * @param tris the vertex indices for the two triangles (6 indices)
     */
    private void addFace(IndexBuffers buffers, Vertex[] verts, int... tris) {
        int startVertex = vb.size();

        vb.addAll(Arrays.asList(verts));

        buffers.addLine(startVertex, startVertex + 1);
        buffers.addLine(startVertex + 1, startVertex + 2);
        buffers.addLine(startVertex + 2, startVertex + 3);
        buffers.addLine(startVertex + 3, startVertex);

        buffers.addTri(startVertex + tris[0], startVertex + tris[1], startVertex + tris[2]);
        buffers.addTri(startVertex + tris[3], startVertex + tris[4], startVertex + tris[5]);
    }
}