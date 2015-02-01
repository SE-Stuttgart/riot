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
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.event.Event;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent;

public class MapTest {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        ObjectMapper mapper= new ObjectMapper();
        mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);

        PropertySetAction a = new PropertySetAction<String>("P-Name");
        PropertyChangeEvent e = new PropertyChangeEvent<String>("P-Name");

        ActionInstance aI = a.createInstance("NewValue",1);
        EventInstance eI = e.createInstance("NewValue",1);
        
        
       String action = mapper.writeValueAsString(a);
       String actionI = mapper.writeValueAsString(aI);
       String event = mapper.writeValueAsString(e);
       String eventI = mapper.writeValueAsString(eI);
       
       System.out.println(action);
       System.out.println(actionI);
       System.out.println(event);
       System.out.println(eventI);
       System.out.println(action.getClass().getCanonicalName());
       Class.forName(action.getClass().getCanonicalName());
    }
}
