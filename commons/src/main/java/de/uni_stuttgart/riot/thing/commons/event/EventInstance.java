package de.uni_stuttgart.riot.thing.commons.event;


import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Instance;

/**
 * Instance of {@link Event}.
 */
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
