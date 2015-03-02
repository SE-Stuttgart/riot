package de.uni_stuttgart.riot.thing;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
    @JsonDeserialize(using = InstanceDeserializer.class)
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
        public Instance(long thingId, String name, Date time, V oldValue, V newValue) {
            super(thingId, name, time);
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        /**
         * Gets the old value.
         * 
         * @return The old value of the property.
         */
        public V getOldValue() {
            return oldValue;
        }

        /**
         * Gets the actual type of the old value. This is needed by the {@link InstanceDeserializer} to reconstruct the value.
         * 
         * @return The type of {@link #getOldValue()}.
         */
        @JsonProperty("oldValueType")
        public Class<?> getOldValueType() {
            return oldValue == null ? null : oldValue.getClass();
        }

        /**
         * Gets the new value.
         * 
         * @return The new value of the property.
         */
        public V getNewValue() {
            return newValue;
        }

        /**
         * Gets the actual type of the new value. This is needed by the {@link InstanceDeserializer} to reconstruct the value.
         * 
         * @return The type of {@link #getNewValue()}.
         */
        @JsonProperty("newValueType")
        public Class<?> geNewValueType() {
            return newValue == null ? null : newValue.getClass();
        }

    }

    /**
     * A deserializier for PropertyChangeEvent Instances that uses the type information provided by {@link #getOldValueType()} and
     * {@link #getNewValue()}.
     */
    public static class InstanceDeserializer extends JsonDeserializer<Instance<?>> {

        @Override
        public Instance<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            long thingId = node.get("thingId").asLong();
            String name = node.get("name").asText();
            Date time = new Date(node.get("time").asLong());
            Object oldValue = ThingState.deserializeValueOfType(node.get("oldValue"), node.get("oldValueType").asText(), jp.getCodec());
            Object newValue = ThingState.deserializeValueOfType(node.get("newValue"), node.get("newValueType").asText(), jp.getCodec());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Instance result = new Instance(thingId, name, time, oldValue, newValue);
            return result;
        }

    }

}
