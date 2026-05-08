package objectdata;

/**
 * Represents a single part of a geometric body in the part buffer.
 * Stores:
 * <ul>
 *   <li>the topology type,</li>
 *   <li>the starting index in the index buffer (IB),</li>
 *   <li>the number of entities contained in this part.</li>
 * </ul>
 * Used to describe edges or triangles to be drawn from the index buffer.
 *
 * @param topologyType the type of topology
 * @param index the starting index in the index buffer
 * @param count the number of entities in this part
 */
public record Part(TopologyType topologyType, int index, int count) {
    public TopologyType getTopologyType() { return topologyType; }
    public int getIndex() { return index; }
    public int getCount() { return count; }
}