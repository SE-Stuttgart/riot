package de.uni_stuttgart.riot.thing;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * An Event fired when a {@link Property} changed.
 * 
 * @param <V>
 *            The type of the property's values.
 */
public class PropertyChangeEvent<V> extends Event<PropertyChangeEvent.Instance<V>> {

    private final transient Property<V> property;

    /**
     * Creates a new PropertyChangeEvent.
     * 
     * @param property
     *            The property that this event belongs to.
     */
    @SuppressWarnings("unchecked")
    PropertyChangeEvent(Property<V> property) {
        super(property.getThing(), property.getName() + "_change", (Class<Instance<V>>) (Class<?>) Instance.class);
        this.property = property;
    }

    /**
     * Gets the property.
     * 
     * @return The property that this event belongs to.
     */
    public Property<V> getProperty() {
        return property;
    }

    @Override
    protected void notifyListeners(Instance<V> eventInstance) {
        // Before notifying the listeners, we need save the value in the property.
        this.property.setValueSilently(eventInstance.getNewValue());
        super.notifyListeners(eventInstance);
    }

    /**
     * An instance of a PropertyChangeEvent.
     * 
     * @param <V>
     *            The type of the property's values.
     */
    public static class Instance<V> extends EventInstance {

        private final V oldValue;
        private final V newValue;

        /**
         * Creates a new PropertyChangeEvent Instance.
         * 
         * @param event
         *            The PropertyChangeEvent.
         * @param oldValue
         *            The old value of the property.
         * @param newValue
         *            The new value of the property.
         */
        public Instance(PropertyChangeEvent<V> event, V oldValue, V newValue) {
            super(event);
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * Creates a new instance.
         * 
         * @param thingId
         *            The ID of the thing that owns the event that this instance belongs to.
         * @param name
         *            The name of the event.
         * @param time
         *            The time when this event was fired.
         * @param oldValue
         *            The old value of the property.
         * @param newValue
         *            The new value of the property.
         */
        @JsonCreator
        public Instance(@JsonProperty("thingId") long thingId, @JsonProperty("name") String name, @JsonProperty("time") Date time, @JsonProperty("oldValue") V oldValue, @JsonProperty("newValue") V newValue) {
            super(thingId, name, time);
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * Gets the old value.
         * 
         * @return The old value of the property.
         */
        @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT)
        public V getOldValue() {
            return oldValue;
        }

        /**
         * Gets the new value.
         * 
         * @return The new value of the property.
         */
        @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT)
        public V getNewValue() {
            return newValue;
        }

    }

}
