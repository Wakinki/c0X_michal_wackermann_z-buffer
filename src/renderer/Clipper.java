package renderer;

import objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles various types of clipping operations for the rendering pipeline.
 * Provides methods to test vertices and triangles against the view volume
 * and to clip geometry against the near plane (z = 0).
 */
final class Clipper {

    /** Minimum w value to prevent division by zero during perspective division. */
    private final double epsW;

    /**
     * Creates a new Clipper with the specified epsilon for w-value checking.
     *
     * @param epsW the minimum w value threshold
     */
    Clipper(double epsW) {
        this.epsW = epsW;
    }

    /**
     * Tests whether a vertex lies inside the clip volume in homogeneous coordinates.
     * The vertex must satisfy:
     * <ul>
     *   <li>-w <= x <= w</li>
     *   <li>-w <= y <= w</li>
     *   <li>0 <= z <= w</li>
     *   <li>w > 0</li>
     * </ul>
     * This test determines if the vertex can be safely processed after dehomogenization.
     *
     * @param v the vertex to test
     * @return true if the vertex is inside the clip volume, false otherwise
     */
    boolean insideClipVolume(Vertex v) {
        Point3D p = v.getPosition();

        double x = p.getX();
        double y = p.getY();
        double z = p.getZ();
        double w = p.getW();

        return w > epsW
                && x >= -w && x <= w
                && y >= -w && y <= w
                && z >= 0.0 && z <= w;
    }

    /**
     * Tests whether an entire triangle lies inside the clip volume.
     *
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     * @return true if all three vertices are inside the clip volume, false otherwise
     */
    boolean triangleFullyInsideClip(Vertex a, Vertex b, Vertex c) {
        return insideClipVolume(a) && insideClipVolume(b) && insideClipVolume(c);
    }

    /**
     * Fast clipping test to determine if an entire triangle lies outside the clip volume.
     * Checks if all vertices are:
     * <ul>
     *   <li>x < -w or x > w,</li>
     *   <li>y < -w or y > w,</li>
     *   <li>z < 0 or z > w,</li>
     *   <li>or w <= epsW.</li>
     * </ul>
     *
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     * @return true if the entire triangle is outside the clip volume, false otherwise
     */
    boolean triangleFullyOutsideClip(Vertex a, Vertex b, Vertex c) {
        Point3D pa = a.getPosition();
        Point3D pb = b.getPosition();
        Point3D pc = c.getPosition();

        if (pa.getX() < -pa.getW() && pb.getX() < -pb.getW() && pc.getX() < -pc.getW()) return true;
        if (pa.getX() >  pa.getW() && pb.getX() >  pb.getW() && pc.getX() >  pc.getW()) return true;
        if (pa.getY() < -pa.getW() && pb.getY() < -pb.getW() && pc.getY() < -pc.getW()) return true;
        if (pa.getY() >  pa.getW() && pb.getY() >  pb.getW() && pc.getY() >  pb.getW()) return true;

        if (pa.getZ() < 0.0 && pb.getZ() < 0.0 && pc.getZ() < 0.0) return true;

        if (pa.getZ() > pa.getW() && pb.getZ() > pb.getW() && pc.getZ() > pc.getW()) return true;

        return pa.getW() <= epsW && pb.getW() <= epsW && pc.getW() <= epsW;
    }

    /**
     * Tests whether a vertex lies inside relative to the near plane.
     * Clips based on z before dehomogenization to remove points behind the observer.
     *
     * @param v the vertex to test
     * @return true if the vertex is inside (w > epsW and z >= 0), false otherwise
     */
    boolean insideNear(Vertex v) {
        return v.getW() > epsW && v.getZ() >= 0.0;
    }

    /**
     * Calculates the intersection point of edge AB with the plane z = 0.
     * Performs linear interpolation of all vertex attributes (position, color, normal,
     * texture coordinates, and world position) at the intersection point.
     *
     * @param a the first endpoint of the edge
     * @param b the second endpoint of the edge
     * @return a new vertex at the intersection with z = 0
     */
    Vertex intersectNear(Vertex a, Vertex b) {
        double za = a.getZ();
        double zb = b.getZ();

        // Intersection parameter with plane z = 0
        double t = -za / (zb - za);

        // Linear interpolation of homogeneous position
        Point3D p = a.getPosition().mul(1.0 - t).add(b.getPosition().mul(t));

        // Linear interpolation of color
        Col c = a.getCol().mul(1.0 - t).add(b.getCol().mul(t));

        // Linear interpolation of normal
        Vec3D n = a.getNormal().mul(1.0 - t).add(b.getNormal().mul(t));

        // Linear interpolation of texture coordinates
        double u = a.getU() * (1.0 - t) + b.getU() * t;
        double v = a.getV() * (1.0 - t) + b.getV() * t;

        // Linear interpolation of world position
        Point3D worldPos = a.getWorldPosition().mul(1.0 - t).add(b.getWorldPosition().mul(t));

        return new Vertex(p, c, n, u, v, worldPos);
    }

    /**
     * Clips a line segment against the plane z = 0.
     *
     * @param a the first endpoint of the line segment
     * @param b the second endpoint of the line segment
     * @return array of vertices representing the clipped line segment, or null if completely outside
     */
    Vertex[] clipLineNear(Vertex a, Vertex b) {
        boolean aInside = insideNear(a);
        boolean bInside = insideNear(b);

        // Entire segment is outside the visible area
        if (!aInside && !bInside) return null;

        Vertex originalA = a;
        Vertex originalB = b;

        // If A is outside, replace with intersection with plane z = 0
        if (!aInside) a = intersectNear(originalA, originalB);

        // If B is outside, replace with intersection with plane z = 0
        if (!bInside) b = intersectNear(originalA, originalB);

        return new Vertex[]{a, b};
    }

    /**
     * Clips a triangle against the plane z = 0.
     * Handles all cases:
     * <ul>
     *   <li>0 vertices inside: returns empty list (nothing to draw)</li>
     *   <li>1 vertex inside: returns 1 triangle</li>
     *   <li>2 vertices inside: returns 2 triangles (split quadrilateral)</li>
     *   <li>3 vertices inside: returns original triangle</li>
     * </ul>
     *
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     * @return list of vertex arrays, each representing a triangle after clipping
     */
    List<Vertex[]> clipTriangleNear(Vertex a, Vertex b, Vertex c) {
        List<Vertex[]> result = new ArrayList<>();

        boolean aInside = insideNear(a);
        boolean bInside = insideNear(b);
        boolean cInside = insideNear(c);

        int insideCount = 0;
        if (aInside) insideCount++;
        if (bInside) insideCount++;
        if (cInside) insideCount++;

        // Nothing is inside => nothing to draw
        if (insideCount == 0) {
            return result;
        }

        // Entire triangle is inside => unchanged
        if (insideCount == 3) {
            result.add(new Vertex[]{a, b, c});
            return result;
        }

        // One vertex is inside
        // After clipping, one smaller triangle is created
        if (insideCount == 1) {
            Vertex inside;
            Vertex outside1;
            Vertex outside2;

            if (aInside) {
                inside = a;
                outside1 = b;
                outside2 = c;
            } else if (bInside) {
                inside = b;
                outside1 = a;
                outside2 = c;
            } else {
                inside = c;
                outside1 = a;
                outside2 = b;
            }

            // New vertices are intersections of edges with plane z = 0
            Vertex i1 = intersectNear(inside, outside1);
            Vertex i2 = intersectNear(inside, outside2);

            result.add(new Vertex[]{inside, i1, i2});
            return result;
        }

        // Two vertices are inside
        // After clipping, a quadrilateral is created, which is split into two triangles
        Vertex inside1;
        Vertex inside2;
        Vertex outside;

        if (!aInside) {
            outside = a;
            inside1 = b;
            inside2 = c;
        } else if (!bInside) {
            outside = b;
            inside1 = a;
            inside2 = c;
        } else {
            outside = c;
            inside1 = a;
            inside2 = b;
        }

        Vertex i1 = intersectNear(inside1, outside);
        Vertex i2 = intersectNear(inside2, outside);

        result.add(new Vertex[]{inside1, inside2, i1});
        result.add(new Vertex[]{inside2, i2, i1});

        return result;
    }
}