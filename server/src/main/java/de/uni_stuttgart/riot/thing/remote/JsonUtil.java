package de.uni_stuttgart.riot.thing.remote;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

public class JsonUtil {
    
    private static JsonUtil instance;
    private final ObjectMapper mapper;
    
    public static JsonUtil getJsonUtil(){
        if(instance == null) instance = new JsonUtil();
        return instance;
    }
    
    private JsonUtil() {
        this.mapper = new ObjectMapper();
        this.mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
    }
    
    public String toJson(Object o) throws JsonProcessingException{
        return this.mapper.writeValueAsString(o);
    }
    
    public <T> T toObject(String json, Class<T> c) throws JsonParseException, JsonMappingException, IOException{
        return this.mapper.readValue(json, c);
    }
    
}
