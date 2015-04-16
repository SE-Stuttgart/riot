package de.uni_stuttgart.riot.android.ui;

import de.uni_stuttgart.riot.android.Callback;
import android.view.View;

/**
 * A PropertyView is a special kind of {@link View} that displays the value of a simple of complex value type <tt>V</tt> and is able to let
 * the user change the value and report the changes to a listener.
 * 
 * @author Philipp Keck
 * 
 * @param <V>
 *            The type of the view's values.
 */
public interface PropertyView<V> {

    /**
     * Checks if the view is enabled, that is, if the user can enter new values or not.
     * 
     * @return True if enabled.
     */
    boolean isEnabled();

    /**
     * Sets the view to enabled or disabled. The user can enter new values if and only if the view is enabled.
     * 
     * @param enabled
     *            True if the view should be enabled.
     */
    void setEnabled(boolean enabled);

    /**
     * Gets the current value of the view.
     * 
     * @return The current value.
     */
    V getValue();

    /**
     * Sets a new value to the view. In most cases (but this is not guaranteed), this will call the listener, see
     * {@link #setListener(Callback)}.
     * 
     * @param value
     *            The new value.
     */
    void setValue(V value);

    /**
     * Sets a listener for the view that will be notified when its value changes (either by the user or by {@link #setValue(Object)}).
     * 
     * @param listener
     *            The new listener.
     */
    void setListener(Callback<V> listener);

    /**
     * Returns the view (usually just <tt>this</tt>).
     * 
     * @return The view.
     */
    View toView();

}
