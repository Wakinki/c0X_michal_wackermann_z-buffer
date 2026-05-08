package rasterize;

import raster.ZBuffer;
import transforms.Col;

/**
 * A line segment rasterizer that writes results through a Z-buffer.
 * During rasterization, it progressively interpolates points between the start and end points
 * and performs a z-test for each pixel.
 */
public class LineRasterizerZBuffer extends LineRasterizer {
    /** Z-buffer used for color writing and depth testing. */
    private ZBuffer zBuffer;

    /**
     * Creates a new line rasterizer with the specified Z-buffer.
     *
     * @param zBuffer the Z-buffer to use for depth testing and color writing
     */
    public LineRasterizerZBuffer(ZBuffer zBuffer) {
        super();
        this.zBuffer = zBuffer;
    }

    /**
     * Sets the Z-buffer for this rasterizer.
     *
     * @param zBuffer the Z-buffer to use
     */
    public void setZBuffer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
    }

    /**
     * Renders a line segment between two points using linear interpolation.
     * Each interpolated point is written through the Z-buffer, which simultaneously
     * handles visibility (z-test).
     *
     * @param x1 x-coordinate of the start point
     * @param y1 y-coordinate of the start point
     * @param z1 depth of the start point
     * @param x2 x-coordinate of the end point
     * @param y2 y-coordinate of the end point
     * @param z2 depth of the end point
     */
    @Override
    protected void drawLine(int x1, int y1, double z1, int x2, int y2, double z2) {
        if (zBuffer == null || color == null) return;

        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));

        Col col = new Col(color.getRGB());

        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;

            int x = (int) Math.round(x1 + t * dx);
            int y = (int) Math.round(y1 + t * dy);
            double z = (1.0 - t) * z1 + t * z2;

            zBuffer.setPixelWithZTest(x, y, z, col);
        }
    }
}