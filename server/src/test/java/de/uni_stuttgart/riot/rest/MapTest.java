package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;

public class MapTest {

    @Test
    public void test() throws IOException {
      
        RemoteThing test = new RemoteThing("name", 2);
        test.addAction(new PropertySetAction<String>("T",1));
        
        ObjectMapper mapper= new ObjectMapper();
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
        String mapString = mapper.writeValueAsString(test);
        System.out.println(mapString);
        RemoteThing thing = mapper.readValue(mapString, RemoteThing.class);
        
        int i = 0;
        int x = i;
    }
}
