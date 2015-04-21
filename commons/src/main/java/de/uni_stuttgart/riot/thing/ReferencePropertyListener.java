package de.uni_stuttgart.riot.thing;

import de.uni_stuttgart.riot.references.Referenceable;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent.Instance;

/**
 * Convenience base class for listeners to reference property, resolves the old and new value as references of the targeted
 * {@link ReferenceProperty}. Must only be used with {@link ReferenceProperty} or {@link WritableReferenceProperty}.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the referenced values.
 */
public abstract class ReferencePropertyListener<R extends Referenceable<? super R>> implements EventListener<PropertyChangeEvent.Instance<Long>> {

    private final Class<R> targetType;

    /**
     * Creates a new instance.
     * 
     * @param targetType
     *            The type of the referenced values.
     */
    public ReferencePropertyListener(Class<R> targetType) {
        this.targetType = targetType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onFired(Event<? extends Instance<Long>> event, Instance<Long> eventInstance) {
        ThingBehavior behavior = event.getThing().getBehavior();
        try {
            R oldValue = behavior.resolve(eventInstance.getOldValue(), targetType);
            R newValue = behavior.resolve(eventInstance.getNewValue(), targetType);
            PropertyChangeEvent<Long> propertyEvent = ((PropertyChangeEvent<Long>) ((Event<PropertyChangeEvent.Instance<Long>>) event));
            onChange(propertyEvent.getProperty(), oldValue, newValue);
        } catch (ResolveReferenceException e) {
            throw new RuntimeException(e);
        }
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
    public abstract void onChange(Property<Long> property, R oldValue, R newValue);

}
