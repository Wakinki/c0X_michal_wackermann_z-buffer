package solid;

import model.Light;
import textures.TextureMapper;
import transforms.*;

import java.util.ArrayList;

/**
 * Represents the content of the entire 3D scene:
 * <ul>
 *   <li>currently used projection - perspective and orthographic,</li>
 *   <li>displayed solids in the scene,</li>
 *   <li>coordinate axes,</li>
 *   <li>default matrices for resetting transformations,</li>
 *   <li>light source,</li>
 *   <li>texture initialization.</li>
 * </ul>
 */
public class Scene {

    /** Current projection matrix (perspective or orthographic). */
    private Mat4 proj;

    /** Perspective projection matrix. */
    private Mat4 projPersp;

    /** Orthographic projection matrix. */
    private Mat4 projOrtho;

    /** Whether perspective projection is currently active. */
    private boolean usePersp = true;

    /** Field of view for perspective projection (in radians). */
    private static final double FOV = Math.toRadians(90);

    /** Distance to the near clipping plane of the view volume. */
    private static final double NEAR = 0.1;

    /** Distance to the far clipping plane of the view volume. */
    private static final double FAR = 100.0;

    /** Height of the orthographic view volume. */
    private static final double ORTHO_H = 10.0;

    /** List of solids in the scene. */
    private final ArrayList<Solid> solids;

    /** List of coordinate axis solids. */
    private final ArrayList<Solid> axes;

    /** Base model matrices for resetting transformations. */
    private final Mat4[] baseModel;

    /** The light source for the scene. */
    private final Light light;

    /**
     * Creates a new 3D scene with the specified viewport dimensions.
     * Initializes default solids (cube, cylinder, sphere), coordinate axes, and a light source.
     *
     * @param viewportWidth the width of the viewport
     * @param viewportHeight the height of the viewport
     */
    public Scene(int viewportWidth, int viewportHeight) {
        rebuildProjections(viewportWidth, viewportHeight);
        proj = projPersp;

        solids = new ArrayList<>();
        axes = new ArrayList<Solid>();

        Mat4 cubeM = new Mat4Transl(-0.4, 0.0, 0.0);
        Mat4 cylinderM = new Mat4Transl(-0.15, 0.3, 0.0);
        Mat4 sphereM = new Mat4Transl(-0.2, 0.0, 0.0);

        solids.add(
                new Cube(3, new Col(0xff4444))
                        .withTexture(new TextureMapper(""))
                        .withModel(cubeM)
        );
        solids.add(
                new Cylinder(4.5, 10, new Point3D(0, 1, 1, 2.5), new Col(0xff0044))
                        .withTexture(new TextureMapper(""))
                        .withModel(cylinderM)
        );
        solids.add(
                new Sphere(2.0, 10, 10, new Point3D(2, 2, 2), new Col(0xff4400))
                        .withTexture(new TextureMapper(""))
                        .withModel(sphereM)
        );

        Col lightDiffuse = new Col(0xffffaa);
        light = new Light(new Vec3D(3.5, -1.0, 2.0), lightDiffuse, 0.25, 8, 8);

        axes.add(new AxisX());
        axes.add(new AxisY());
        axes.add(new AxisZ());

        for (Solid s : solids) {
            s.setTextureEnabled(false);
        }

        light.getMarker().setTextureEnabled(false);

        Mat4 lightM = light.getMarker().getModel();

        baseModel = new Mat4[]{cubeM, cylinderM, sphereM, lightM};
    }

    /**
     * Rebuilds projection matrices based on the new viewport dimensions.
     *
     * @param width the new viewport width
     * @param height the new viewport height
     */
    public void rebuildProjections(int width, int height) {
        if (width <= 0 || height <= 0) return;

        double k = height / (double) width;

        projPersp = new Mat4PerspRH(FOV, k, NEAR, FAR);

        double orthoW = ORTHO_H / k;
        projOrtho = new Mat4OrthoRH(orthoW, ORTHO_H, NEAR, FAR);

        if (usePersp) {
            proj = projPersp;
        } else {
            proj = projOrtho;
        }
    }

    /**
     * Sets the projection type.
     *
     * @param perspective true for perspective projection, false for orthographic
     */
    public void setProjection(boolean perspective) {
        usePersp = perspective;
        if (usePersp) {
            proj = projPersp;
        } else {
            proj = projOrtho;
        }
    }

    /**
     * Returns the current projection matrix.
     *
     * @return the current projection matrix
     */
    public Mat4 getProj() {
        return proj;
    }

    /**
     * Returns whether perspective projection is currently active.
     *
     * @return true if using perspective projection, false if using orthographic
     */
    public boolean isPerspective() {
        return usePersp;
    }

    /**
     * Returns all solids that can be manipulated (scene solids + light marker).
     *
     * @return array of manipulable solids
     */
    public Solid[] getManipulableSolids() {
        Solid[] result = new Solid[solids.size() + 1];
        solids.toArray(result);
        result[solids.size()] = light.getMarker();
        return result;
    }

    /**
     * Returns the coordinate axis solids.
     *
     * @return array of axis solids
     */
    public Solid[] getAxes() {
        return axes.toArray(new Solid[0]);
    }

    /**
     * Returns the base model matrices for resetting transformations.
     *
     * @return array of base model matrices
     */
    public Mat4[] getBaseModels() {
        return baseModel;
    }

    /**
     * Returns the light source for the scene.
     *
     * @return the light source
     */
    public Light getLight() {
        return light;
    }
}