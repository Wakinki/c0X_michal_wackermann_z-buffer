package objectdata;

/**
 * Represents an element that supports basic vector space operations.
 * Implementations must provide:
 * <ul>
 *   <li>scalar multiplication (mul),</li>
 *   <li>vector addition (add).</li>
 * </ul>
 *
 * @param <E> the type of the vectorizable element
 */
public interface Vectorizable<E> {
    /**
     * Multiplies this element by a scalar.
     *
     * @param d the scalar value
     * @return a new element representing the result of the multiplication
     */
    E mul(double d);

    /**
     * Adds another element to this element.
     *
     * @param v the element to add
     * @return a new element representing the sum
     */
    E add(E v);
}