package de.uni_stuttgart.riot.thing;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Action for setting a property.
 * 
 * @param <V>
 *            The type of the property's values.
 */
public class PropertySetAction<V> extends Action<PropertySetAction.Instance<V>> {

    private final transient Property<V> property;

    /**
     * Creates a new PropertySetAction.
     * 
     * @param property
     *            The property that this action belongs to.
     */
    @SuppressWarnings("unchecked")
    PropertySetAction(Property<V> property) {
        super(property.getThing(), property.getName() + "_set", (Class<Instance<V>>) (Class<?>) Instance.class);
        this.property = property;
    }

    /**
     * Gets the property.
     * 
     * @return The property that this action belongs to.
     */
    public Property<V> getProperty() {
        return property;
    }

    /**
     * Shorthand for {@link #fire(ActionInstance)}. Please read the notes there about the actual effects of calling this method.
     * 
     * @param newValue
     *            The new value for the property.
     */
    public void fire(V newValue) {
        fire(new Instance<V>(this, newValue));
    }

    /**
     * An instance of a PropertySetAction.
     * 
     * @param <V>
     *            The type of the property's values.
     */
    public static class Instance<V> extends ActionInstance {

        private final V newValue;

        /**
         * Creates a new PropertySetAction Instance.
         * 
         * @param action
         *            The PropertySetAction.
         * @param newValue
         *            The new value for the property.
         */
        public Instance(PropertySetAction<V> action, V newValue) {
            super(action);
            this.newValue = newValue;
        }

        /**
         * Creates a new instance.
         * 
         * @param thingId
         *            The ID of the thing that owns the action that this instance belongs to.
         * @param name
         *            The name of the action.
         * @param time
         *            The time when this action was fired.
         * @param newValue
         *            The new value for the property.
         */
        @JsonCreator
        Instance(@JsonProperty("thingId") long thingId, @JsonProperty("name") String name, @JsonProperty("time") Date time, @JsonProperty("newValue") V newValue) {
            super(thingId, name, time);
            this.newValue = newValue;
        }

        /**
         * Gets the new value.
         * 
         * @return The new value for the property.
         */
        @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT)
        public V getNewValue() {
            return newValue;
        }

    }

}
