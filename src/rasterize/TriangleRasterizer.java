package rasterize;

import objectdata.Vertex;
import raster.ZBuffer;
import shaders.Shader;
import textures.TextureMapper;
import transforms.Col;
import utils.ColorUtils;

/**
 * Triangle rasterizer for filled areas.
 * Handles part of the rendering pipeline:
 * <ol>
 *   <li>the triangle is divided horizontally after sorting vertices by y-coordinate,</li>
 *   <li>for each y, the edge points of the scanline are linearly interpolated,</li>
 *   <li>for each x on the scanline, z is determined by interpolation in the plane,</li>
 *   <li>Z-test is performed and the pixel is written to image and depth buffers if it passes.</li>
 * </ol>
 */
public class TriangleRasterizer {
    /** Z-buffer for depth testing and pixel writing. */
    private ZBuffer zBuffer;

    /** Texture mapper for texture sampling. */
    private TextureMapper texture;

    /** Whether texture mapping is enabled. */
    private boolean textureEnabled = false;

    /** Shader for computing pixel colors. Defaults to simple interpolation of vertex colors. */
    private Shader shader = (a, b, c, wA, wB, wC) -> a.getCol().mul(wA)
            .add(b.getCol().mul(wB))
            .add(c.getCol().mul(wC));

    /**
     * Creates a new triangle rasterizer with the specified Z-buffer.
     *
     * @param zBuffer the Z-buffer to use for depth testing
     */
    public TriangleRasterizer(ZBuffer zBuffer) {
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
     * Sets the shader for this rasterizer.
     * If null is provided, resets to the default shader (simple vertex color interpolation).
     *
     * @param shader the shader to use for color calculations, or null for default
     */
    public void setShader(Shader shader) {
        if (shader == null) {
            this.shader = (a, b, c, wA, wB, wC) -> a.getCol().mul(wA)
                    .add(b.getCol().mul(wB))
                    .add(c.getCol().mul(wC));
        } else {
            this.shader = shader;
        }
    }

    /**
     * Rasterizes a single triangle.
     * Procedure:
     * <ol>
     *   <li>sort vertices by y-coordinate,</li>
     *   <li>divide the triangle into upper and lower parts,</li>
     *   <li>interpolate along edges by y,</li>
     *   <li>interpolate along scanline by x,</li>
     *   <li>determine z and other attributes at each pixel,</li>
     *   <li>perform Z-test and write pixel if it passes.</li>
     * </ol>
     *
     * @param a the first vertex of the triangle
     * @param b the second vertex of the triangle
     * @param c the third vertex of the triangle
     */
    public void rasterize(Vertex a, Vertex b, Vertex c) {
        int width = zBuffer.getWidth();
        int height = zBuffer.getHeight();

        Vertex A = a;
        Vertex B = b;
        Vertex C = c;

        // Sort by Y to get Ay <= By <= Cy
        // This allows processing the triangle as two halves: A->B and B->C
        if (A.getY() > B.getY()) { Vertex tmp = A; A = B; B = tmp; }
        if (B.getY() > C.getY()) { Vertex tmp = B; B = C; C = tmp; }
        if (A.getY() > B.getY()) { Vertex tmp = A; A = B; B = tmp; }

        // Convert vertex coordinates to local variables
        // x,y are image coordinates after viewport transformation
        // z is depth for Z-buffer
        // u,v are texture coordinates
        int ax = (int) Math.round(A.getX());
        int ay = (int) Math.round(A.getY());
        double az = A.getZ();
        double au = A.getU();
        double av = A.getV();

        int bx = (int) Math.round(B.getX());
        int by = (int) Math.round(B.getY());
        double bz = B.getZ();
        double bu = B.getU();
        double bv = B.getV();

        int cx = (int) Math.round(C.getX());
        int cy = (int) Math.round(C.getY());
        double cz = C.getZ();
        double cu = C.getU();
        double cv = C.getV();

        // Part 1: A -> B
        // For y from Ay to By, linearly interpolate along edges
        if (by != ay) {
            // Clipping by y to screen window during rasterization
            int yStart = Math.max(ay, 0);
            int yEnd = Math.min(by, height - 1);

            for (int y = yStart; y <= yEnd; y++) {
                // Parameter tAB determines scanline position on edge AB
                // tAB = 0 => we are at A
                // tAB = 1 => we are at B
                double tAB = (y - ay) / (double) (by - ay);

                // Interpolation of coordinates and attributes on edge AB
                int xAB = (int) Math.round((1 - tAB) * ax + tAB * bx);
                double zAB = (1 - tAB) * az + tAB * bz;
                double uAB = (1 - tAB) * au + tAB * bu;
                double vAB = (1 - tAB) * av + tAB * bv;

                // Weights for shader
                // Values represent linear combination of vertices along edge AB
                double wAAB = 1.0 - tAB;
                double wBAB = tAB;
                double wCAB = 0.0;

                // Same for edge AC
                double tAC = (y - ay) / (double) (cy - ay);
                int xAC = (int) Math.round((1 - tAC) * ax + tAC * cx);
                double zAC = (1 - tAC) * az + tAC * cz;
                double uAC = (1 - tAC) * au + tAC * cu;
                double vAC = (1 - tAC) * av + tAC * cv;

                double wAAC = 1.0 - tAC;
                double wBAC = 0.0;
                double wCAC = tAC;

                if (xAB > xAC) {
                    int tx = xAB; xAB = xAC; xAC = tx;

                    double tz = zAB; zAB = zAC; zAC = tz;
                    double tu = uAB; uAB = uAC; uAC = tu;
                    double tv = vAB; vAB = vAC; vAC = tv;

                    double twA = wAAB; wAAB = wAAC; wAAC = twA;
                    double twB = wBAB; wBAB = wBAC; wBAC = twB;
                    double twC = wCAB; wCAB = wCAC; wCAC = twC;
                }

                // Clipping by x to screen window during rasterization
                int xStart = Math.max(xAB, 0);
                int xEnd = Math.min(xAC, width - 1);
                if (xStart > xEnd) continue;

                // Scanline has width of 1 pixel
                // To avoid division by zero, use average of both edges
                if (xAC == xAB) {
                    double wA = 0.5 * (wAAB + wAAC);
                    double wB = 0.5 * (wBAB + wBAC);
                    double wC = 0.5 * (wCAB + wCAC);

                    double z = 0.5 * (zAB + zAC);
                    double u = 0.5 * (uAB + uAC);
                    double v = 0.5 * (vAB + vAC);

                    Col baseColor = shader.getColor(A, B, C, wA, wB, wC);
                    Col color = sampleColor(baseColor, u, v);

                    // Z-test
                    zBuffer.setPixelWithZTest(xAB, y, z, color);
                    continue;
                }

                // Fill scanline between left and right intersection
                for (int x = xStart; x <= xEnd; x++) {
                    double t = (x - xAB) / (double) (xAC - xAB);

                    // Interpolation of depth z: for each x,y determine z by interpolation in the plane
                    double z = (1 - t) * zAB + t * zAC;

                    // Interpolation of texture coordinates
                    double u = (1 - t) * uAB + t * uAC;
                    double v = (1 - t) * vAB + t * vAC;

                    // Interpolation of weights for shader color calculation
                    double wA = (1 - t) * wAAB + t * wAAC;
                    double wB = (1 - t) * wBAB + t * wBAC;
                    double wC = (1 - t) * wCAB + t * wCAC;

                    // Shader determines base pixel color, or texel - texture
                    Col baseColor = shader.getColor(A, B, C, wA, wB, wC);
                    Col color = sampleColor(baseColor, u, v);
                    zBuffer.setPixelWithZTest(x, y, z, color);
                }
            }
        }

        // Part 2: B -> C
        // Process is analogous, only left/right edge is calculated between BC and AC
        if (cy != by) {
            int yStart = Math.max(by, 0);
            int yEnd = Math.min(cy, height - 1);

            for (int y = yStart; y <= yEnd; y++) {
                double tBC = (y - by) / (double) (cy - by);
                int xBC = (int) Math.round((1 - tBC) * bx + tBC * cx);
                double zBC = (1 - tBC) * bz + tBC * cz;
                double uBC = (1 - tBC) * bu + tBC * cu;
                double vBC = (1 - tBC) * bv + tBC * cv;

                double wABC = 0.0;
                double wBBC = 1.0 - tBC;
                double wCBC = tBC;

                double tAC = (cy == ay) ? 0.0 : (y - ay) / (double) (cy - ay);
                int xAC = (int) Math.round((1 - tAC) * ax + tAC * cx);
                double zAC = (1 - tAC) * az + tAC * cz;
                double uAC = (1 - tAC) * au + tAC * cu;
                double vAC = (1 - tAC) * av + tAC * cv;

                double wAAC = 1.0 - tAC;
                double wBAC = 0.0;
                double wCAC = tAC;

                if (xBC > xAC) {
                    int tx = xBC; xBC = xAC; xAC = tx;

                    double tz = zBC; zBC = zAC; zAC = tz;
                    double tu = uBC; uBC = uAC; uAC = tu;
                    double tv = vBC; vBC = vAC; vAC = tv;

                    double twA = wABC; wABC = wAAC; wAAC = twA;
                    double twB = wBBC; wBBC = wBAC; wBAC = twB;
                    double twC = wCBC; wCBC = wCAC; wCAC = twC;
                }

                int xStart = Math.max(xBC, 0);
                int xEnd = Math.min(xAC, width - 1);
                if (xStart > xEnd) continue;

                if (xAC == xBC) {
                    double wA = 0.5 * (wABC + wAAC);
                    double wB = 0.5 * (wBBC + wBAC);
                    double wC = 0.5 * (wCBC + wCAC);

                    double z = 0.5 * (zBC + zAC);
                    double u = 0.5 * (uBC + uAC);
                    double v = 0.5 * (vBC + vAC);

                    Col baseColor = shader.getColor(A, B, C, wA, wB, wC);
                    Col color = sampleColor(baseColor, u, v);

                    zBuffer.setPixelWithZTest(xBC, y, z, color);
                    continue;
                }

                for (int x = xStart; x <= xEnd; x++) {
                    double t = (x - xBC) / (double) (xAC - xBC);

                    double z = (1 - t) * zBC + t * zAC;
                    double u = (1 - t) * uBC + t * uAC;
                    double v = (1 - t) * vBC + t * vAC;

                    double wA = (1 - t) * wABC + t * wAAC;
                    double wB = (1 - t) * wBBC + t * wBAC;
                    double wC = (1 - t) * wCBC + t * wCAC;

                    Col baseColor = shader.getColor(A, B, C, wA, wB, wC);
                    Col color = sampleColor(baseColor, u, v);

                    zBuffer.setPixelWithZTest(x, y, z, color);
                }
            }
        }
    }

    /**
     * Computes the final pixel color.
     * If texture is enabled, the color from the shader is combined with the texture texel.
     *
     * @param baseColor the base color from the shader
     * @param u the u texture coordinate
     * @param v the v texture coordinate
     * @return the final pixel color
     */
    private Col sampleColor(Col baseColor, double u, double v) {
        if (textureEnabled && texture != null) {
            return ColorUtils.multiply(baseColor, texture.sample(u, v));
        }
        return baseColor;
    }

    /**
     * Sets the texture mapper for this rasterizer.
     *
     * @param texture the texture mapper to use
     */
    public void setTexture(TextureMapper texture) {
        this.texture = texture;
    }

    /**
     * Enables or disables texture mapping.
     *
     * @param textureEnabled true to enable texture mapping, false to disable
     */
    public void setTextureEnabled(boolean textureEnabled) {
        this.textureEnabled = textureEnabled;
    }
}