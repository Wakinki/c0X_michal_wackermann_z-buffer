package raster;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents a depth buffer (Z-buffer) for 3D scene visibility resolution.
 * Based on a regular grid, it stores a depth value for each pixel.
 * <p>
 * Used to solve visibility in 3D scenes:
 * during rasterization, for each pixel the new z value is compared with the value
 * stored in the buffer, and closer elements can overwrite more distant ones.
 * <p>
 * The buffer is initialized to 1 (farthest depth) when cleared.
 */
public class DepthBuffer implements Raster<Double> {

    /** 2D array storing depth values for each pixel. */
    private final double[][] buffer;

    /** Width of the depth buffer in pixels. */
    private final int width;

    /** Height of the depth buffer in pixels. */
    private final int height;

    /**
     * Creates a new depth buffer with the specified dimensions.
     *
     * @param width the width of the buffer
     * @param height the height of the buffer
     */
    public DepthBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        this.buffer = new double[width][height];
        clear();
    }

    @Override
    public void setElement(int x, int y, Double value) {
        if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
            buffer[x][y] = value;
        }
    }

    @Override
    public Optional<Double> getElement(int x, int y) {
        if (x >= 0 && y >= 0 && x < getWidth() && y < getHeight()) {
            return Optional.of(buffer[x][y]);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Clears the buffer by initializing all values to 1 (farthest depth).
     */
    @Override
    public void clear() {
        for (double[] row : buffer) {
            Arrays.fill(row, 1);
        }
    }
}