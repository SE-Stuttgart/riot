package de.uni_stuttgart.riot.simulation_client;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyListener;

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
     * Reacts to changes in the JavaFX Property.
     */
    private final ChangeListener<T> changeListener;

    /**
     * Reacts to changes in the Thing property.
     */
    private final PropertyListener<T> thingListener;

    /**
     * Creates a new binding.
     * 
     * @param thingProperty
     *            The thing property to bind to.
     * @param behavior
     *            The thing behavior, which is used to change the property's values.
     */
    public ThingPropertyInternalBinding(Property<T> thingProperty, SimulatedThingBehavior behavior) {
        super(thingProperty.get());
        this.thingProperty = thingProperty;

        this.changeListener = (observable, oldValue, newValue) -> {
            synchronized (ThingPropertyInternalBinding.this) {
                if (updating) {
                    return;
                }
                updating = true;
                try {
                    behavior.changePropertyValue(thingProperty, convertToExactType(newValue));
                } finally {
                    updating = false;
                }
            }
        };

        this.thingListener = (event, eventInstance) -> {
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

        thingProperty.getChangeEvent().register(thingListener);
        super.addListener(changeListener);
    }

    /**
     * Converts the given <tt>value</tt> to the type <tt>T</tt>. This particularly means that if <tt>value</tt> is of a subtype of
     * <tt>T</tt>, it will be converted to a direct instance of <tt>T</tt>. This transformation is only performed for numbers, all other
     * instances will not be changed.
     * 
     * @param value
     *            The value to convert.
     * @return The converted value, if it was a number of a wrong type, or simply the given value.
     */
    @SuppressWarnings("unchecked")
    private T convertToExactType(T value) {
        Class<T> targetType = thingProperty.getValueType();
        if (value == null || targetType.isInstance(value)) {
            return value;
        } else if (value instanceof Number && Number.class.isAssignableFrom(targetType)) {
            if (targetType == Number.class) {
                return value;
            }

            Number number = (Number) value;
            if (targetType == Integer.class) {
                return (T) (Integer) number.intValue();
            } else if (targetType == Long.class) {
                return (T) (Long) number.longValue();
            } else if (targetType == Short.class) {
                return (T) (Short) number.shortValue();
            } else if (targetType == Float.class) {
                return (T) (Float) number.floatValue();
            } else if (targetType == Double.class) {
                return (T) (Double) number.doubleValue();
            } else if (targetType == Byte.class) {
                return (T) (Byte) number.byteValue();
            } else {
                throw new IllegalArgumentException("Cannot convert the value " + value + " from the UI to the unsupported number type " + targetType + " of the target property " + thingProperty);
            }
        } else {
            return value;
        }
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
