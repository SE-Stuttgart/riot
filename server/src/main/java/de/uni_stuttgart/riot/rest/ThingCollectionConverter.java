package de.uni_stuttgart.riot.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingState;

/**
 * Converts {@link Thing}s on the REST interface.
 * 
 * @author Philipp Keck
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ThingCollectionConverter implements MessageBodyWriter<Collection<Thing>>, MessageBodyReader<Collection<Thing>> {

    private static final JsonFactory JSON_FACTORY = RiotApplication.produceObjectMapper().getFactory();

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        boolean result = Collection.class.isAssignableFrom(type) &&
                (((ParameterizedType)genericType).getActualTypeArguments()[0]).equals(Thing.class) &&
                mediaType.equals(MediaType.APPLICATION_JSON_TYPE);
        return result;
      }

    @Override
    public Collection<Thing> readFrom(Class<Collection<Thing>> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        boolean result = Collection.class.isAssignableFrom(type) &&
                (((ParameterizedType)genericType).getActualTypeArguments()[0]).equals(Thing.class) &&
                mediaType.equals(MediaType.APPLICATION_JSON_TYPE);
        return result;
    }

    @Override
    public void writeTo(Collection<Thing> things, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        try (JsonGenerator generator = JSON_FACTORY.createGenerator(entityStream)) {
            generator.writeStartArray();
            Iterator<Thing> i = things.iterator();
            while (i.hasNext()) {
                Thing t = i.next();
                ThingState state = ThingState.create(t);
                generator.writeStartObject();
                generator.writeStringField("type", t.getClass().getName());
                generator.writeNumberField("id", t.getId());
                generator.writeStringField("name", t.getName());

                for (Map.Entry<String, Object> propertyValue : state.getPropertyValues().entrySet()) {
                    generator.writeObjectField(propertyValue.getKey(), propertyValue.getValue());
                }
                generator.writeEndObject();
            } 
            generator.writeEndArray();
        }
    }

    @Override
    public long getSize(Collection<Thing> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * Finds an annotation by type in the Collection. Note: This method could be used in the future to distinguish the JSON format for different
     * use cases with annotations on the Jersey endpoint methods.
     * 
     * @param annotations
     *            The annotations.
     * @param annotationType
     *            The type to look for.
     * @return The annotation of the given type or <tt>null</tt> if none was present.
     */
    @SuppressWarnings("unused")
    private static <A extends Annotation> A findAnnotation(Annotation[] annotations, Class<A> annotationType) {
        if (annotations == null || annotations.length == 0) {
            return null;
        }

        for (Annotation annotation : annotations) {
            if (annotationType.isInstance(annotation)) {
                return annotationType.cast(annotation);
            }
        }
        return null;
    }

}
