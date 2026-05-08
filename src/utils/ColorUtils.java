package utils;

import transforms.Col;

/**
 * Helper class for color operations.
 * Used when calculating the final pixel color:
 * <ul>
 *   <li>combining lighting color components,</li>
 *   <li>multiplying surface color by light color,</li>
 *   <li>combining base color and texture.</li>
 * </ul>
 */
public final class ColorUtils {

    /** Private constructor to prevent instantiation of this utility class. */
    private ColorUtils() {
    }

    /**
     * Multiplies two colors component-wise.
     * Each RGB component is multiplied and divided by 255 to maintain the 0-255 range.
     *
     * @param a the first color
     * @param b the second color
     * @return a new color with component-wise multiplication of a and b
     */
    public static Col multiply(Col a, Col b) {
        int rgbA = a.getRGB();
        int rgbB = b.getRGB();

        int r = (((rgbA >> 16) & 0xff) * ((rgbB >> 16) & 0xff)) / 255;
        int g = (((rgbA >> 8) & 0xff) * ((rgbB >> 8) & 0xff)) / 255;
        int bl = ((rgbA & 0xff) * (rgbB & 0xff)) / 255;

        return new Col((r << 16) | (g << 8) | bl);
    }

    /**
     * Scales a color by a scalar factor.
     * Each RGB component is multiplied by the factor and clamped to the 0-255 range.
     *
     * @param c the color to scale
     * @param k the scaling factor
     * @return a new color with each component scaled by k
     */
    public static Col scale(Col c, double k) {
        int rgb = c.getRGB();
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;

        r = clamp((int) Math.round(r * k));
        g = clamp((int) Math.round(g * k));
        b = clamp((int) Math.round(b * k));

        return new Col((r << 16) | (g << 8) | b);
    }

    /**
     * Adds two colors component-wise.
     * Each RGB component is added and clamped to the 0-255 range.
     *
     * @param a the first color
     * @param b the second color
     * @return a new color with component-wise addition of a and b
     */
    public static Col add(Col a, Col b) {
        int rgbA = a.getRGB();
        int rgbB = b.getRGB();

        int r = clamp(((rgbA >> 16) & 0xff) + ((rgbB >> 16) & 0xff));
        int g = clamp(((rgbA >> 8) & 0xff) + ((rgbB >> 8) & 0xff));
        int bl = clamp((rgbA & 0xff) + (rgbB & 0xff));

        return new Col((r << 16) | (g << 8) | bl);
    }

    /**
     * Clamps an integer value to the range [0, 255].
     *
     * @param v the value to clamp
     * @return the clamped value between 0 and 255
     */
    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}