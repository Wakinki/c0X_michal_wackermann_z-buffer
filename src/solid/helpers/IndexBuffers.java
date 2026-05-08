package solid.helpers;

import objectdata.Part;
import objectdata.TopologyType;
import solid.Solid;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for building index buffers for solid objects.
 * Accumulates line and triangle indices separately, then flushes them to a Solid object
 * with appropriate Part descriptors for each topology type.
 */
public class IndexBuffers {
    /** List of indices for line segments (pairs of vertex indices). */
    private final List<Integer> lineIb = new ArrayList<>();

    /** List of indices for triangles (triples of vertex indices). */
    private final List<Integer> triIb = new ArrayList<>();

    /**
     * Adds a line segment defined by two vertex indices.
     *
     * @param a the first vertex index
     * @param b the second vertex index
     */
    public void addLine(int a, int b) {
        lineIb.add(a);
        lineIb.add(b);
    }

    /**
     * Adds a triangle defined by three vertex indices.
     *
     * @param a the first vertex index
     * @param b the second vertex index
     * @param c the third vertex index
     */
    public void addTri(int a, int b, int c) {
        triIb.add(a);
        triIb.add(b);
        triIb.add(c);
    }

    /**
     * Transfers accumulated indices to the specified Solid object.
     * Creates Part objects for both line and triangle topology types with appropriate
     * starting indices and counts.
     *
     * @param s the Solid to flush indices to
     */
    public void flushTo(Solid s) {
        int startLines = s.getIb().size();
        for (Integer idx : lineIb) {
            s.getIb().add(idx);
        }
        s.getParts().add(new Part(TopologyType.LINE, startLines, lineIb.size() / 2));

        int startTris = s.getIb().size();
        for (Integer idx : triIb) {
            s.getIb().add(idx);
        }
        s.getParts().add(new Part(TopologyType.TRIANGLE, startTris, triIb.size() / 3));
    }
}