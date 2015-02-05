package de.uni_stuttgart.riot.thing.remote;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

/**
 *  JsonUtil for serialization of objects to json. 
 */
public class JsonUtil {

    private static JsonUtil instance;
    private final ObjectMapper mapper;

    private JsonUtil() {
        this.mapper = new ObjectMapper();
        this.mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
    }
    
    /**
     * Getter for a {@link JsonUtil} instance.
     * @return 
     *       instance of {@link JsonUtil}.
     */
    public static JsonUtil getJsonUtil() {
        if (instance == null) {
            instance = new JsonUtil();
        }
        return instance;
    }

    /**
     * Transforms a given Object into its json representation.
     * @param o 
     *      object to be serialized.
     * @return
     *      json representation of o
     * @throws JsonProcessingException
     *      Exception during serialization to json.
     */
    public String toJson(Object o) throws JsonProcessingException {
        return this.mapper.writeValueAsString(o);
    }

    /**
     * Construction of a object the given class type <b>c</b> from json representation <b>json</b>.
     * @param json
     *      json representation of an object of type c
     * @param c
     *      type information
     * @param <T> type of object to be constructed.
     * @return
     *      the constructed object of type c
     * @throws JsonParseException
     *      Exception during serialization to json.
     * @throws JsonMappingException
     *      Exception during serialization to json.
     * @throws IOException
     *      Exception during serialization to json.
     */
    public <T> T toObject(String json, Class<T> c) throws JsonParseException, JsonMappingException, IOException {
        return this.mapper.readValue(json, c);
    }

}
