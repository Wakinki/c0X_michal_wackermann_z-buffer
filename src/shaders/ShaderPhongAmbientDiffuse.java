package shaders;

import model.Light;
import objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;
import utils.ColorUtils;
import utils.Math3DUtils;

/**
 * Phong shader implementing ambient and diffuse lighting in the scene.
 * The pixel color is determined by a combination of:
 * <ul>
 *   <li>base surface color,</li>
 *   <li>normal direction at the point,</li>
 *   <li>direction to the light source,</li>
 *   <li>light color.</li>
 * </ul>
 */
public class ShaderPhongAmbientDiffuse implements Shader {

    /** The light source for this shader. */
    private final Light light;

    /** The strength of the ambient lighting component. */
    private final double ambientStrength;

    /**
     * Creates a new Phong shader with ambient and diffuse components.
     *
     * @param light the light source to use for lighting calculations
     * @param ambientStrength the strength of the ambient lighting component (0-1)
     */
    public ShaderPhongAmbientDiffuse(Light light, double ambientStrength) {
        this.light = light;
        this.ambientStrength = ambientStrength;
    }

    /**
     * Computes the pixel color using attribute interpolation and lighting calculations.
     * <p>
     * The calculation includes:
     * <ul>
     *   <li>Ambient component: base color scaled by ambientStrength,</li>
     *   <li>Diffuse component: base color scaled by diffuse intensity (Lambert's cosine law)
     *       and multiplied by light color.</li>
     * </ul>
     *
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     * @param wA the barycentric weight for vertex a
     * @param wB the barycentric weight for vertex b
     * @param wC the barycentric weight for vertex c
     * @return the computed color for the pixel
     */
    @Override
    public Col getColor(Vertex a, Vertex b, Vertex c, double wA, double wB, double wC) {
        Col baseColor = a.getCol().mul(wA)
                .add(b.getCol().mul(wB))
                .add(c.getCol().mul(wC));

        Point3D worldPos = a.getWorldPosition().mul(wA)
                .add(b.getWorldPosition().mul(wB))
                .add(c.getWorldPosition().mul(wC));

        Vec3D normal = Math3DUtils.normalize(
                a.getNormal().mul(wA)
                        .add(b.getNormal().mul(wB))
                        .add(c.getNormal().mul(wC))
        );

        Vec3D lightDir = Math3DUtils.normalize(new Vec3D(
                light.getPosition().getX() - worldPos.getX(),
                light.getPosition().getY() - worldPos.getY(),
                light.getPosition().getZ() - worldPos.getZ()
        ));

        // Diffuse component according to Lambert's cosine law
        double diffuse = Math.max(0.0, Math3DUtils.dot(normal, lightDir));

        // Ambient component: base color weakened by ambientStrength constant
        Col ambientPart = ColorUtils.scale(baseColor, ambientStrength);

        // Diffuse component: base color strengthened by diffuse intensity and multiplied by light color
        Col diffusePart = ColorUtils.multiply(
                ColorUtils.scale(baseColor, diffuse),
                light.getColor()
        );

        // Final color is the sum of ambient and diffuse components
        return ColorUtils.add(ambientPart, diffusePart);
    }
}