package de.uni_stuttgart.riot.simulation_client;

import javafx.application.Platform;
import javafx.beans.value.ObservableValueBase;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;

/**
 * A binding from Thing properties to JavaFX observables. The observable will be updated when the Thing property changes, so this is
 * readonly in the direction from the thing to JavaFX.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The property value type.
 */
public class ThingPropertyObservable<T> extends ObservableValueBase<T> {

    /**
     * The thing property.
     */
    private final Property<T> thingProperty;

    /**
     * Reacts to changes in the Thing property.
     */
    private final EventListener<PropertyChangeEvent.Instance<T>> thingListener;

    /**
     * Creates a new binding.
     * 
     * @param thingProperty
     *            The thing property to bind to.
     */
    public ThingPropertyObservable(Property<T> thingProperty) {
        this.thingProperty = thingProperty;

        this.thingListener = (event, eventInstance) -> {
            Platform.runLater(ThingPropertyObservable.this::fireValueChangedEvent);
        };

        thingProperty.getChangeEvent().register(thingListener);
    }

    /**
     * Destroys the binding.
     */
    public void unbind() {
        thingProperty.getChangeEvent().unregister(thingListener);
    }

    @Override
    public T getValue() {
        return thingProperty.getValue();
    }

}
