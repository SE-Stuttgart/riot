package de.uni_stuttgart.riot.thing.commons.event;


import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.thing.commons.Instance;

/**
 * Instance of {@link Event}.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public abstract class EventInstance extends Instance {

    /**
     * Constructor.
     * @param time .
     * @param thingId .
     */
    public EventInstance(Timestamp time, long thingId) {
        super(time, thingId);
    }
    
    /**
     * Default Constructor.
     */
    public EventInstance() {
    }

}
