package de.uni_stuttgart.riot.thing;

import de.uni_stuttgart.riot.thing.PropertyChangeEvent.Instance;

/**
 * Convenience base class for {@link PropertyListener}, can be registered with {@link Property#register(PropertyListener)}, delivers the
 * changed property and old and new value.
 * 
 * @author Philipp Keck
 *
 * @param <V>
 *            The type of the property's values.
 */
public abstract class BasePropertyListener<V> implements PropertyListener<V> {

    @SuppressWarnings("unchecked")
    @Override
    public final void onFired(Event<? extends Instance<? extends V>> event, Instance<? extends V> eventInstance) {
        onChange(((PropertyChangeEvent<? extends V>) event).getProperty(), eventInstance.getOldValue(), eventInstance.getNewValue());
    }

    /**
     * This method will be fired whenever one of the properties that the listener is registered to has changed (unless it was a silent
     * change).
     * 
     * @param property
     *            The property that changed.
     * @param oldValue
     *            Its old value.
     * @param newValue
     *            Its new value.
     */
    public abstract void onChange(Property<? extends V> property, V oldValue, V newValue);

}
