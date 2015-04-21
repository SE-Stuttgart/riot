package de.uni_stuttgart.riot.thing;

/**
 * This is just a convenience wrapper for {@link EventListener} to avoid that clients need to reference the
 * <tt>PropertyChangeEvent.Instance</tt> directly.
 * 
 * @author Philipp Keck
 *
 * @param <V>
 *            The type of the property.
 */
public interface PropertyListener<V> extends EventListener<PropertyChangeEvent.Instance<? extends V>> {

}
