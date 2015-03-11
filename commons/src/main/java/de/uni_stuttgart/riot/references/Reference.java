package de.uni_stuttgart.riot.references;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A reference to an entity of type <tt>T</tt>.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the referenced entity.
 */
@JsonSerialize(using = Reference.ReferenceSerializer.class)
@JsonDeserialize(using = Reference.ReferenceDeserializer.class)
public interface Reference<R> {

    /**
     * Gets the ID of the target entity.
     * 
     * @return The ID. A value of <tt>null</tt> indicates that no target entity is set.
     */
    Long getId();

    /**
     * A serializer for references that serializes a reference as a long value (JSON number), which puts a <tt>null</tt> value if the
     * reference is empty.
     */
    public static class ReferenceSerializer extends JsonSerializer<Reference<?>> {
        @Override
        public void serialize(Reference<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            if (value == null || value.getId() == null) {
                jgen.writeNull();
            } else {
                jgen.writeNumber(value.getId());
            }
        }
    }

    /**
     * A deserializer for references, the counterpart for {@link ReferenceSerializer}.
     */
    public static class ReferenceDeserializer extends JsonDeserializer<Reference<?>> {
        @Override
        public Reference<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
                return StaticReference.NULL_REFERENCE;
            } else if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
                return StaticReference.create(jp.getLongValue());
            } else {
                throw new JsonMappingException("Expected an integer value instead of " + jp.getCurrentToken());
            }
        }
    }

}
