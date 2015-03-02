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
    @JsonDeserialize(using = InstanceDeserializer.class)
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
        Instance(long thingId, String name, Date time, V newValue) {
            super(thingId, name, time);
            this.newValue = newValue;
        }

        /**
         * Gets the new value.
         * 
         * @return The new value for the property.
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
     * A deserializier for PropertySetAction Instances that uses the type information provided by {@link #getOldValueType()} and
     * {@link #getNewValue()}.
     */
    public static class InstanceDeserializer extends JsonDeserializer<Instance<?>> {

        @Override
        public Instance<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);
            long thingId = node.get("thingId").asLong();
            String name = node.get("name").asText();
            Date time = new Date(node.get("time").asLong());
            Object newValue = ThingState.deserializeValueOfType(node.get("newValue"), node.get("newValueType").asText(), jp.getCodec());
            @SuppressWarnings({ "unchecked", "rawtypes" })
            Instance result = new Instance(thingId, name, time, newValue);
            return result;
        }

    }

}
