package de.uni_stuttgart.riot.thing.commons.event;

import java.sql.Timestamp;

public abstract class EventInstance {
    
    private Timestamp time;
    
    public EventInstance() {
        this.setTime(new Timestamp(System.currentTimeMillis()));
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

}
