package textures;

import transforms.Col;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Handles loading and sampling of textures using texture coordinates (u, v).
 * <ul>
 *   <li>each vertex carries texture coordinates,</li>
 *   <li>for each pixel, texel coordinates are determined by interpolation,</li>
 *   <li>the corresponding color is then selected from the texture.</li>
 * </ul>
 */
public class TextureMapper {

    /** The texture image. */
    private final BufferedImage image;

    /**
     * Loads a texture from resources.
     * Throws an exception if the file cannot be loaded.
     *
     * @param path the path to the texture resource
     * @throws RuntimeException if the texture is not found or cannot be read
     */
    public TextureMapper(String path) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Texture not found.");
            }
            image = ImageIO.read(is);
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException("Cannot load texture.");
        }
    }

    /**
     * Samples the texture at coordinates (u, v).
     * Procedure:
     * <ol>
     *   <li>u,v coordinates are wrapped to the interval [0,1),</li>
     *   <li>converted to texture pixel coordinates,</li>
     *   <li>the corresponding texel is read from the texture.</li>
     * </ol>
     *
     * @param u the u texture coordinate
     * @param v the v texture coordinate
     * @return the color at the sampled texture coordinates
     */
    public Col sample(double u, double v) {
        double uu = wrap(u);
        double vv = wrap(v);

        int x = (int) Math.round(uu * (image.getWidth() - 1));
        int y = (int) Math.round((1.0 - vv) * (image.getHeight() - 1));

        int rgb = image.getRGB(x, y);
        return new Col(rgb);
    }

    /**
     * Wraps a coordinate to the interval [0,1).
     * wrap(t) = t - floor(t)
     *
     * @param t the coordinate to wrap
     * @return the wrapped coordinate in [0,1)
     */
    private double wrap(double t) {
        return t - Math.floor(t);
    }
}