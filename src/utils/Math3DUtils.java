package utils;

import transforms.Vec3D;

/**
 * Helper class for basic 3D space calculations.
 * In this project, it is primarily used for lighting calculations:
 * <ul>
 *   <li>normalizing normals,</li>
 *   <li>normalizing direction to light,</li>
 *   <li>calculating dot product for diffuse component.</li>
 * </ul>
 */
public final class Math3DUtils {

    /** Small epsilon value for floating-point comparisons to avoid division by zero. */
    private static final double EPS = 1e-10;

    /** Private constructor to prevent instantiation of this utility class. */
    private Math3DUtils() {
    }

    /**
     * Normalizes a 3D vector to unit length.
     * If the vector length is below EPS, returns a default vector (0, 0, 1).
     *
     * @param v the vector to normalize
     * @return the normalized vector, or (0, 0, 1) if the original vector is too small
     */
    public static Vec3D normalize(Vec3D v) {
        double len = Math.sqrt(
                v.getX() * v.getX()
                        + v.getY() * v.getY()
                        + v.getZ() * v.getZ()
        );

        if (len < EPS) {
            return new Vec3D(0, 0, 1);
        }

        return new Vec3D(
                v.getX() / len,
                v.getY() / len,
                v.getZ() / len
        );
    }

    /**
     * Computes the dot product of two 3D vectors.
     *
     * @param a the first vector
     * @param b the second vector
     * @return the dot product of a and b
     */
    public static double dot(Vec3D a, Vec3D b) {
        return a.getX() * b.getX()
                + a.getY() * b.getY()
                + a.getZ() * b.getZ();
    }
}