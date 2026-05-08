package controller;

import solid.Solid;
import transforms.*;

/**
 * Handles manipulation of selected 3D objects in the scene.
 * Capabilities:
 * <ul>
 *   <li>maintains a list of controllable objects and tracks which is currently active,</li>
 *   <li>allows transforming the active object and resetting it to its default state,</li>
 *   <li>applies transformations using matrices from the transforms library: translation, X/Y/Z rotation, and scale.</li>
 * </ul>
 */
public class TransformController {

    /** Array of solids that can be controlled. */
    private final Solid[] solids;

    /** Array of base model matrices for resetting transformations. */
    private final Mat4[] baseModels;

    /** Index of the currently active solid. */
    private int activeIndex;

    /**
     * Creates a new transform controller.
     *
     * @param solids the array of controllable solids
     * @param baseModels the array of base model matrices for each solid
     * @param initialIndex the initial active solid index
     */
    public TransformController(Solid[] solids, Mat4[] baseModels, int initialIndex) {
        this.solids = solids;
        this.baseModels = baseModels;
        this.activeIndex = Math.max(0, Math.min(initialIndex, solids.length - 1));
    }

    /**
     * Returns the currently active solid.
     *
     * @return the active solid
     */
    public Solid getActive() {
        return solids[activeIndex];
    }

    /**
     * Sets the active solid index.
     *
     * @param idx the index of the solid to activate
     */
    public void setActiveIndex(int idx) {
        if (idx < 0 || idx >= solids.length) return;
        activeIndex = idx;
    }

    /**
     * Resets the active object to its default state.
     */
    public void resetActive() {
        getActive().setModel(baseModels[activeIndex]);
    }

    /**
     * Applies an arbitrary transformation to the active object.
     *
     * @param t the transformation matrix to apply
     */
    public void apply(Mat4 t) {
        Solid s = getActive();
        s.setModel(s.getModel().mul(t));
    }

    /**
     * Translates the active object by the specified amounts.
     *
     * @param dx translation in x-direction
     * @param dy translation in y-direction
     * @param dz translation in z-direction
     */
    public void translate(double dx, double dy, double dz) {
        apply(new Mat4Transl(dx, dy, dz));
    }

    /**
     * Translates the active object along the X axis.
     *
     * @param dx translation in x-direction
     */
    public void translateX(double dx) {
        this.translate(dx,0,0);
    }

    /**
     * Translates the active object along the Y axis.
     *
     * @param dy translation in y-direction
     */
    public void translateY(double dy) {
        this.translate(0,dy,0);
    }

    /**
     * Translates the active object along the Z axis.
     *
     * @param dz translation in z-direction
     */
    public void translateZ(double dz) {
        this.translate(0,0,dz);
    }

    /**
     * Rotates the active object around the X axis.
     *
     * @param radians the rotation angle in radians
     */
    public void rotateX(double radians) {
        rotateAroundCenter(new Mat4RotX(radians));
    }

    /**
     * Rotates the active object around the Y axis.
     *
     * @param radians the rotation angle in radians
     */
    public void rotateY(double radians) {
        rotateAroundCenter(new Mat4RotY(radians));
    }

    /**
     * Rotates the active object around the Z axis.
     *
     * @param radians the rotation angle in radians
     */
    public void rotateZ(double radians) {
        rotateAroundCenter(new Mat4RotZ(radians));
    }

    /**
     * Scales the active object uniformly.
     *
     * @param s the uniform scale factor
     */
    public void scale(double s) {
        scaleAroundCenter(s);
    }

    /**
     * Scales the active object non-uniformly.
     *
     * @param sx scale factor in x-direction
     * @param sy scale factor in y-direction
     * @param sz scale factor in z-direction
     */
    public void scale(double sx, double sy, double sz) {
        Vec3D c = getActiveCenterWorld();

        Mat4 pivotScale = new Mat4Transl(-c.getX(), -c.getY(), -c.getZ())
                .mul(new Mat4Scale(1+sx, 1+sy, 1+sz))
                .mul(new Mat4Transl(c.getX(), c.getY(), c.getZ()));

        apply(pivotScale);
    }

    /**
     * Applies a scale transformation around the object's center.
     *
     * @param s the uniform scale factor
     */
    private void scaleAroundCenter(double s) {
        scale(s,s,s);
    }

    /**
     * Scales the active object along the X axis.
     *
     * @param s scale factor in x-direction
     */
    public void scaleX(double s) {
        this.scale(s, 0, 0);
    }

    /**
     * Scales the active object along the Y axis.
     *
     * @param s scale factor in y-direction
     */
    public void scaleY(double s) {
        this.scale(0, s,0);
    }

    /**
     * Scales the active object along the Z axis.
     *
     * @param s scale factor in z-direction
     */
    public void scaleZ(double s){
        this.scale(0,0, s);
    }

    /**
     * Helper method to compute the center of the active object in world coordinates.
     *
     * @return the center position of the active object in world space
     */
    private Vec3D getActiveCenterWorld() {
        Solid s = getActive();
        Vec3D cLocal = s.getLocalCenter();
        Point3D cWorld = new Point3D(cLocal.getX(), cLocal.getY(), cLocal.getZ()).mul(s.getModel());
        return new Vec3D(cWorld);
    }

    /**
     * Applies a rotation around the object's center.
     *
     * @param rot the rotation matrix to apply
     */
    private void rotateAroundCenter(Mat4 rot) {
        Vec3D c = getActiveCenterWorld();

        Mat4 pivotRot = new Mat4Transl(-c.getX(), -c.getY(), -c.getZ())
                .mul(rot)
                .mul(new Mat4Transl(c.getX(), c.getY(), c.getZ()));

        apply(pivotRot);
    }


}