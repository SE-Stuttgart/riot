package de.uni_stuttgart.riot.simulation_client;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.WritableProperty;

/**
 * A binding from Thing properties to JavaFX properties. Both properties will be mutually updated when they change. The Thing property will
 * be accessed from the outside, i.e. as a controlling client.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The property value type.
 */
public class ThingPropertyBinding<T> extends SimpleObjectProperty<T> {

    /**
     * True when an update of either property is in progress (to avoid loops).
     */
    private boolean updating;

    /**
     * The thing property.
     */
    private final WritableProperty<T> thingProperty;

    /**
     * Reacts to changes in the JavaFX Property.
     */
    private final ChangeListener<T> changeListener = (observable, oldValue, newValue) -> {
        synchronized (ThingPropertyBinding.this) {
            if (updating) {
                return;
            }
            updating = true;
            try {
                thingProperty.set(newValue);
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
            synchronized (ThingPropertyBinding.this) {
                if (updating) {
                    return;
                }
                updating = true;
                try {
                    ThingPropertyBinding.super.set(eventInstance.getNewValue());
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
     */
    public ThingPropertyBinding(WritableProperty<T> thingProperty) {
        super(thingProperty.getValue());
        this.thingProperty = thingProperty;
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

}
