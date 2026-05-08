package model;

import solid.Solid;
import solid.Sphere;
import transforms.Col;
import transforms.Mat4Transl;
import transforms.Point3D;
import transforms.Vec3D;

/**
 * Represents a light source in the scene.
 * Stores the light's visual marker and its current color.
 */
public class Light {
    /** Visual representation of the light source (a sphere). */
    private final Sphere marker;

    /** Current color of the light source. */
    private Col color;

    /**
     * Creates a new light source with the specified parameters.
     *
     * @param position the position of the light in 3D space
     * @param color the color of the light
     * @param markerRadius the radius of the visual marker sphere
     * @param stacks the number of vertical divisions for the marker sphere
     * @param slices the number of horizontal divisions for the marker sphere
     */
    public Light(Vec3D position, Col color, double markerRadius, int stacks, int slices) {
        this.marker = new Sphere(markerRadius, stacks, slices, new Point3D(0, 0, 0), color);
        this.color = color;

        marker.setLightingEnabled(false);
        marker.setModel(new Mat4Transl(position.getX(), position.getY(), position.getZ()));
    }

    /**
     * Returns the visual marker of this light source.
     *
     * @return the sphere marker representing this light
     */
    public Solid getMarker() {
        return marker;
    }

    /**
     * Returns the position of this light source in world coordinates.
     *
     * @return the 3D position of the light
     */
    public Vec3D getPosition() {
        Point3D p = new Point3D(0, 0, 0).mul(marker.getModel());
        return new Vec3D(p);
    }

    /**
     * Returns the color of this light source.
     *
     * @return the light color
     */
    public Col getColor() {
        return color;
    }

    /**
     * Sets the position of this light source.
     *
     * @param position the new position in 3D space
     */
    public void setPosition(Vec3D position) {
        marker.setModel(new Mat4Transl(position.getX(), position.getY(), position.getZ()));
    }

    /**
     * Sets the position of this light source using individual coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public void setPosition(double x, double y, double z) {
        setPosition(new Vec3D(x, y, z));
    }

    /**
     * Sets the color of this light source.
     *
     * @param color the new light color
     */
    public void setColor(Col color) {
        this.color = color;
        marker.recolor(color);
    }
}