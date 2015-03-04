package de.uni_stuttgart.riot.simulation_client;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;

/**
 * A binding from Thing properties to JavaFX properties. Both properties will be mutually updated when they change. The Thing property will
 * be accessed from the inside, i.e. as an executing thing behavior.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The property value type.
 */
public class ThingPropertyInternalBinding<T> extends SimpleObjectProperty<T> {

    /**
     * True when an update of either property is in progress (to avoid loops).
     */
    private boolean updating;

    /**
     * The thing property.
     */
    private final Property<T> thingProperty;

    /**
     * The thing's behavior.
     */
    private final SimulatedThingBehavior behavior;

    /**
     * Reacts to changes in the JavaFX Property.
     */
    private final ChangeListener<T> changeListener = (observable, oldValue, newValue) -> {
        synchronized (ThingPropertyInternalBinding.this) {
            if (updating) {
                return;
            }
            updating = true;
            try {
                behavior.changePropertyValue(thingProperty, newValue);
            } finally {
                updating = false;
            }
        }
    };

    /**
     * Reacts to changes in the Thing property.
     */
    private final EventListener<PropertyChangeEvent.Instance<T>> thingListener = (event, eventInstance) -> {
        Platform.runLater(() -> {
            synchronized (ThingPropertyInternalBinding.this) {
                if (updating) {
                    return;
                }
                updating = true;
                try {
                    ThingPropertyInternalBinding.super.set(eventInstance.getNewValue());
                } finally {
                    updating = false;
                }
            }
        });
    };

    /**
     * Creates a new binding.
     * 
     * @param thingProperty
     *            The thing property to bind to.
     * @param behavior
     *            The thing behavior, which is used to change the property's values.
     */
    public ThingPropertyInternalBinding(Property<T> thingProperty, SimulatedThingBehavior behavior) {
        super(thingProperty.getValue());
        this.thingProperty = thingProperty;
        this.behavior = behavior;
        thingProperty.getChangeEvent().register(thingListener);
        super.addListener(changeListener);
    }

    /**
     * Destroys the binding.
     */
    public void unbind() {
        thingProperty.getChangeEvent().unregister(thingListener);
        super.removeListener(changeListener);
    }

    /**
     * Creates an instance. This helper method serves for nailing down the generic parameter <tt>T</tt>.
     * 
     * @param <T>
     *            The type of the property values.
     * @param thingProperty
     *            The property to wrap.
     * @param behavior
     *            The behavior to use for updating the property values in the thing.
     * @return The binding property.
     */
    public static <T> ThingPropertyInternalBinding<T> create(Property<T> thingProperty, SimulatedThingBehavior behavior) {
        return new ThingPropertyInternalBinding<T>(thingProperty, behavior);
    }

}
