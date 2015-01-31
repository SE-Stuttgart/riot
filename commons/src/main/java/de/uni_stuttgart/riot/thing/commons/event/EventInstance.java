package de.uni_stuttgart.riot.thing.commons.event;

import java.sql.Timestamp;

/**
 * TODO .
 *
 */
public abstract class EventInstance {

    private Timestamp time;
    private int thingId;

    /**
     * Constructor.
     */
    public EventInstance() {
        this.setTime(new Timestamp(System.currentTimeMillis()));
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getThingId() {
        return thingId;
    }

    public void setThingId(int thingId) {
        this.thingId = thingId;
    }

}
