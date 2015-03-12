package de.uni_stuttgart.riot.thing;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.references.Referenceable;

/**
 * Represents the current state of a thing, i.e., the values of its properties.
 * 
 * @author Philipp Keck
 */
public class ThingState {

    private final Map<String, Object> propertyValues;

    /**
     * Creates a new instance.
     * 
     * @param propertyValues
     *            The values of the thing's properties.
     */
    @JsonCreator
    public ThingState(@JsonProperty("propertyValues") Map<String, Object> propertyValues) {
        if (propertyValues == null) {
            throw new IllegalArgumentException("propertyValues must not be null!");
        }

        this.propertyValues = propertyValues;
    }

    /**
     * Creates a new empty instance.
     */
    public ThingState() {
        this(new HashMap<String, Object>());
    }

    /**
     * Gets the values.
     * 
     * @return The values of the thing's properties.
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT)
    public Map<String, Object> getPropertyValues() {
        return propertyValues;
    }

    /**
     * Gets a property value by its name.
     * 
     * @param propertyName
     *            The property name.
     * @return The property value. <tt>null</tt> if the property does not exist.
     */
    public Object get(String propertyName) {
        return propertyValues == null ? null : propertyValues.get(propertyName);
    }

    /**
     * Sets the given property.
     * 
     * @param propertyName
     *            The name of the property.
     * @param propertyValue
     *            The value of the property.
     */
    public void set(String propertyName, Object propertyValue) {
        propertyValues.put(propertyName, propertyValue);
    }

    /**
     * Determines if the state is empty.
     * 
     * @return True if there are no property values in this state.
     */
    @JsonIgnore
    public boolean isEmpty() {
        return propertyValues.isEmpty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((propertyValues == null) ? 0 : propertyValues.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ThingState other = (ThingState) obj;
        if (propertyValues == null) {
            if (other.propertyValues != null) {
                return false;
            }
        } else if (!propertyValues.equals(other.propertyValues)) {
            return false;
        }
        return true;
    }

    /**
     * Applies this state silently to the given thing. Nonexistent properties will be ignored.
     * 
     * @param thing
     *            The thing to apply the state to.
     */
    public void apply(Thing thing) {
        for (Map.Entry<String, Object> value : propertyValues.entrySet()) {
            Property<?> property = thing.getProperty(value.getKey());
            if (property != null) {
                silentSetThingProperty(property, value.getValue());
            }
        }
    }

    /**
     * Silently (without raising any events) sets the value of the given property. Use with care!
     * 
     * @param <V>
     *            The type of the property's value.
     * @param property
     *            The property to be set.
     * @param value
     *            The new value for the property.
     */
    public static <V> void silentSetThingProperty(Property<V> property, Object value) {
        property.setValueSilently(property.getValueType().cast(value));
    }

    /**
     * Silently (without raising any events) sets the value of the given property. Use with care!
     * 
     * @param <R>
     *            The type of the entities referenced by the property.
     * @param property
     *            The property to be set.
     * @param value
     *            The new referenced entity value for the property.
     */
    public static <R extends Referenceable<R>> void silentSetThingProperty(ReferenceProperty<R> property, Object value) {
        if (value == null) {
            property.setValueSilently(null);
        } else if (value instanceof Long) {
            property.setValueSilently((Long) value);
        } else if (property.getTargetType().isInstance(value)) {
            property.setValueSilently(((Referenceable<?>) value).getId());
        } else {
            throw new IllegalArgumentException("The type of the value " + value + " does not match the expected type " + property.getTargetType());
        }
    }

    /**
     * Silently (without raising any events) sets the value of the given property. Use with care!
     * 
     * @param <R>
     *            The type of the entities referenced by the property.
     * @param property
     *            The property to be set.
     * @param value
     *            The new referenced entity value for the property.
     */
    public static <R extends Referenceable<R>> void silentSetThingProperty(WritableReferenceProperty<R> property, Object value) {
        if (value == null) {
            property.setValueSilently(null);
        } else if (value instanceof Long) {
            property.setValueSilently((Long) value);
        } else if (property.getTargetType().isInstance(value)) {
            property.setValueSilently(((Referenceable<?>) value).getId());
        } else {
            throw new IllegalArgumentException("The type of the value " + value + " does not match the expected type " + property.getTargetType());
        }
    }

    /**
     * Constructs a state snapshot from the given thing.
     * 
     * @param thing
     *            The thing.
     * @return The state of the thing.
     */
    public static ThingState create(Thing thing) {
        Map<String, Object> propertyValues = new HashMap<String, Object>();
        for (Property<?> property : thing.properties.values()) {
            propertyValues.put(property.getName(), property.get());
        }
        return new ThingState(propertyValues);
    }

}
