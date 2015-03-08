package de.uni_stuttgart.riot.android;

/**
 * Generic interface for callback functions.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The type of the callback parameter.
 */
public interface Callback<T> {

    /**
     * The callback function.
     * 
     * @param t
     *            The callback parameter.
     */
    void callback(T t);

}
