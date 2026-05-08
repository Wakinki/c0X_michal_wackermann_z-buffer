package raster;

import java.util.Optional;

/**
 * Generic interface for a 2D raster/grid data structure.
 * Provides methods for accessing and modifying elements at specific coordinates,
 * querying dimensions, and clearing the raster.
 *
 * @param <T> the type of elements stored in the raster
 */
public interface Raster<T> {

    /**
     * Sets the element at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param value the value to set
     */
    void setElement(int x, int y, T value);

    /**
     * Gets the element at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return an Optional containing the element, or empty if coordinates are out of bounds
     */
    Optional<T> getElement(int x, int y);

    /**
     * Returns the width of the raster.
     *
     * @return the width in pixels
     */
    int getWidth();

    /**
     * Returns the height of the raster.
     *
     * @return the height in pixels
     */
    int getHeight();

    /**
     * Clears all elements in the raster.
     */
    void clear();
}