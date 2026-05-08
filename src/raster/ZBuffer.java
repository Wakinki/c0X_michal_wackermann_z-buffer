package raster;

import transforms.Col;

import java.util.Optional;

/**
 * Combines an image buffer and a depth buffer.
 * <ul>
 *   <li>image buffer - stores the final color of each pixel,</li>
 *   <li>depth buffer - stores the depth (z) value for each pixel.</li>
 * </ul>
 * <p>
 * Used to perform z-test during rasterization:
 * for each pixel, compares the new z value with the value stored in the depth buffer
 * and writes the color and new depth only if the rendered element is closer to the observer.
 * <p>
 * Implements the basic principle of the Z-buffer algorithm, where more distant pixels
 * are not drawn over closer ones.
 */
public class ZBuffer {
    /** Buffer for storing the final color of each pixel. */
    private final Raster<Col> imageBuffer;

    /** Buffer for storing the depth (z) value of each pixel. */
    private final Raster<Double> depthBuffer;

    /**
     * Creates a new ZBuffer with the specified image buffer.
     * Creates a depth buffer with the same dimensions as the image buffer.
     *
     * @param imageBuffer the image buffer to use for color storage
     */
    public ZBuffer(Raster<Col> imageBuffer) {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    /**
     * Sets a pixel with z-test.
     * The pixel is drawn only if:
     * <ul>
     *   <li>z >= 0 (in front of the camera), and</li>
     *   <li>z is less than the current depth buffer value (closer to the observer).</li>
     * </ul>
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @param z the depth value to test
     * @param color the color to set if the z-test passes
     */
    public void setPixelWithZTest(int x, int y, double z, Col color) {
        // Load value from depth buffer
        Optional<Double> zbOpt = depthBuffer.getElement(x, y);
        // Compare value with incoming z
        // Clipping combined with z-test:
        // if (z >= 0 && z < ZB[x][y]) { ... }
        if (zbOpt.isEmpty()) {
            return; // outside raster -> do nothing
        }
        double zb = zbOpt.get();

        // According to condition:
        // 1. do nothing
        // 2. color pixel, update depth buffer
        // Comparison with depth buffer - compare with stored value and update if needed
        if (z >= 0.0 && z < zb) {         // z-test
            imageBuffer.setElement(x, y, color); // color pixel
            depthBuffer.setElement(x, y, z);     // update depth
        }
    }

    /**
     * Returns the width of the buffer.
     *
     * @return the width in pixels
     */
    public int getWidth() {
        return imageBuffer.getWidth();
    }

    /**
     * Returns the height of the buffer.
     *
     * @return the height in pixels
     */
    public int getHeight() {
        return imageBuffer.getHeight();
    }

    /**
     * Clears the depth buffer by initializing all values to 1 (farthest depth).
     */
    public void clear() {
        depthBuffer.clear();
    }
}