package solid;

import objectdata.Part;
import objectdata.Vertex;
import textures.TextureMapper;
import transforms.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class representing a 3D solid object.
 * Manages:
 * <ul>
 *   <li>vertex buffer (vb) - stores object vertices,</li>
 *   <li>index buffer (ib) - stores vertex indices for rendering,</li>
 *   <li>parts - describes topology segments of the object,</li>
 *   <li>model transformation (position, rotation, scale),</li>
 *   <li>texture mapping,</li>
 *   <li>lighting settings.</li>
 * </ul>
 */
public abstract class Solid {
    /** Vertex buffer storing the object's vertices. */
    protected List<Vertex> vb = new ArrayList<>();

    /** Index buffer storing vertex indices (pairs). */
    protected List<Integer> ib = new ArrayList<>();

    /** List of parts describing the object's topology. */
    protected final List<Part> parts = new ArrayList<>();

    /** Model transformation matrix. */
    protected Mat4 model = new Mat4Identity();

    /** Position of the object in 3D space. */
    protected Vec3D position = new Vec3D(0, 0, 0);

    /** Rotation of the object in degrees (x, y, z). */
    protected Vec3D rotation = new Vec3D(0, 0, 0);

    /** Scale of the object (x, y, z). */
    protected Vec3D scale = new Vec3D(1, 1, 1);

    /** Texture mapper for this object. */
    protected TextureMapper texture;

    /** Whether texture mapping is enabled. */
    protected boolean textureEnabled = false;

    /** Whether lighting is enabled. */
    protected boolean lightingEnabled = true;

    /** Flag indicating if the model matrix needs to be recalculated. */
    protected boolean dirty = true;

    /**
     * Returns whether lighting is enabled for this object.
     *
     * @return true if lighting is enabled, false otherwise
     */
    public boolean isLightingEnabled() {
        return lightingEnabled;
    }

    /**
     * Enables or disables lighting for this object.
     *
     * @param lightingEnabled true to enable lighting, false to disable
     */
    public void setLightingEnabled(boolean lightingEnabled) {
        this.lightingEnabled = lightingEnabled;
    }

    /**
     * Sets the model transformation matrix.
     *
     * @param model the model matrix to set
     */
    public void setModel(Mat4 model) {
        this.model = model;
    }

    /**
     * Sets the model transformation matrix and returns the changed object reference.
     *
     * @param model the model matrix to set
     * @return Solid the updated object
     */
    public Solid withModel(Mat4 model) {
        this.model = model;
        return this;
    }

    /**
     * Returns the model transformation matrix.
     * Recalculates the matrix if it is dirty.
     *
     * @return the current model matrix
     */
    public Mat4 getModel() {
        if (dirty) {
            updateModel();
        }
        return model;
    }

    /**
     * Updates the model matrix based on position, rotation, and scale.
     */
    public void updateModel() {
        model = new Mat4Transl(position)
                .mul(new Mat4RotXYZ(
                        Math.toRadians(rotation.getX()),
                        Math.toRadians(rotation.getY()),
                        Math.toRadians(rotation.getZ())
                ))
                .mul(new Mat4Scale(scale));
        dirty = false;
    }

    public List<Vertex> getVb() {
        return vb;
    }

    public List<Integer> getIb() {
        return ib;
    }

    public List<Part> getParts() {
        return parts;
    }

    public TextureMapper getTexture() {
        return texture;
    }

    /**
     * Sets the texture for this object.
     *
     * @param texture the texture mapper to use
     */
    public void setTexture(TextureMapper texture) {
        this.texture = texture;
    }

    /**
     * Sets the texture for this object and returns the object
     *
     * @param texture the texture mapper to use
     * @return Solid object with newly set texture
     */
    public Solid withTexture(TextureMapper texture) {
        this.texture = texture;
        return this;
    }

    /**
     * Returns whether texture mapping is enabled.
     *
     * @return true if texture mapping is enabled, false otherwise
     */
    public boolean isTextureEnabled() {
        return textureEnabled;
    }

    /**
     * Enables or disables texture mapping.
     *
     * @param textureEnabled true to enable texture mapping, false to disable
     */
    public void setTextureEnabled(boolean textureEnabled) {
        this.textureEnabled = textureEnabled;
    }

    /**
     * Toggles texture mapping on/off.
     */
    public void toggleTexture() {
        textureEnabled = !textureEnabled;
    }

    public Vec3D getPosition() {
        return position;
    }

    /**
     * Sets the position of the object.
     *
     * @param position the new position
     */
    public void setPosition(Vec3D position) {
        this.position = position;
        dirty = true;
    }

    public Vec3D getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation of the object in degrees.
     *
     * @param rotation the new rotation (x, y, z) in degrees
     */
    public void setRotation(Vec3D rotation) {
        this.rotation = rotation;
        dirty = true;
    }

    public Vec3D getScale() {
        return scale;
    }

    /**
     * Sets the scale of the object.
     *
     * @param scale the new scale (x, y, z)
     */
    public void setScale(Vec3D scale) {
        this.scale = scale;
        dirty = true;
    }

    /**
     * Recolors all vertices of the object to the specified color.
     *
     * @param color the new color for all vertices
     */
    public void recolor(Col color) {
        for (int i = 0; i < vb.size(); i++) {
            Vertex v = vb.get(i);

            vb.set(i, new Vertex(
                    v.getPosition(),
                    color,
                    v.getNormal(),
                    v.getU(),
                    v.getV(),
                    v.getWorldPosition()
            ));
        }
    }

    /**
     * Helper method for calculating the center of the object in local coordinates.
     * The center is the center of the axis-aligned bounding box.
     *
     * @return the center point in local coordinates
     */
    public Vec3D getLocalCenter() {
        if (vb.isEmpty()) return new Vec3D(0, 0, 0);

        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;

        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for (Vertex p : vb) {
            double x = p.getX();
            double y = p.getY();
            double z = p.getZ();

            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;

            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }

        return new Vec3D(
                (minX + maxX) * 0.5,
                (minY + maxY) * 0.5,
                (minZ + maxZ) * 0.5
        );
    }
}