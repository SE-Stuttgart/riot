package de.uni_stuttgart.riot.thing.commons.event;


import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Instance;

/**
 * TODO .
 *
 */
public abstract class EventInstance extends Instance {

    public EventInstance(Timestamp time, long thingId) {
        super(time, thingId);
    }
    
    public EventInstance() {
    }

}
