package de.uni_stuttgart.riot.thing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents the current state of a thing, i.e., the values of its properties.
 * 
 * @author Philipp Keck
 */
@JsonSerialize(using = ThingState.Serializer.class)
@JsonDeserialize(using = ThingState.Deserializer.class)
public class ThingState {

    private final Map<String, Object> propertyValues;

    /**
     * Creates a new instance.
     * 
     * @param propertyValues
     *            The values of the thing's properties.
     */
    public ThingState(Map<String, Object> propertyValues) {
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
        if (value == null || property.getValueType().isInstance(value)) {
            @SuppressWarnings("unchecked")
            V typedValue = (V) value;
            property.setValueSilently(typedValue);
        } else {
            throw new IllegalArgumentException("The type of the value " + value + " does not match the expected type " + property.getValueType());
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
            propertyValues.put(property.getName(), property.getValue());
        }
        return new ThingState(propertyValues);
    }

    /**
     * Jackson serializer for ThingStates. The result is an array of objects. Each object contains the name of the respective thing
     * property, its value type (which is not necessarily the type specified by the thing, but may be a subtype) and its value.
     */
    public static class Serializer extends JsonSerializer<ThingState> {

        @Override
        public void serialize(ThingState value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartArray();
            for (Map.Entry<String, Object> property : value.propertyValues.entrySet()) {
                jgen.writeStartObject();
                jgen.writeStringField("name", property.getKey());
                jgen.writeObjectField("valueType", property.getValue() == null ? null : property.getValue().getClass());
                jgen.writeObjectField("value", property.getValue());
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
        }

    }

    /**
     * Jackson deserializer for ThingStates. See {@link Serializer} for details about the format.
     */
    public static class Deserializer extends JsonDeserializer<ThingState> {

        @Override
        public ThingState deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);
            if (!node.isArray()) {
                throw new JsonMappingException("Expected an array!");
            }

            Iterator<JsonNode> propertyNodes = node.elements();
            Map<String, Object> propertyValues = new HashMap<String, Object>();
            while (propertyNodes.hasNext()) {
                JsonNode propertyNode = propertyNodes.next();
                if (!propertyNode.isObject()) {
                    throw new JsonMappingException("Expected an array of objects!");
                }

                String propertyName = propertyNode.get("name").asText();
                Object propertyValue = deserializeValueOfType(propertyNode.get("value"), propertyNode.get("valueType").asText(), codec);
                propertyValues.put(propertyName, propertyValue);
            }
            return new ThingState(propertyValues);
        }
    }

    /**
     * Parses a JSON node into an instance of the given type.
     * 
     * @param valueNode
     *            The JSON node to parse.
     * @param valueType
     *            The type (should be a fully qualified class name).
     * @param codec
     *            The Jackson codec used for deserializing the value.
     * @return THe parsed value (or one of many exceptions ...). Note that the binding to the generic parameter <tt>V</tt> is very weak
     *         here. This method might as well return something else. There is no check and there is no possibility to check (type erasure).
     */
    public static Object deserializeValueOfType(JsonNode valueNode, String valueType, ObjectCodec codec) {
        if (valueNode.isNull()) {
            return null;
        }

        try {
            return valueNode.traverse(codec).readValueAs(Class.forName(valueType));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unknown valueType " + valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing value of type " + valueType + ", content was: " + valueNode);
        } catch (IOException e) {
            throw new RuntimeException("Error reading from the JSON input stream!");
        }
    }

}
