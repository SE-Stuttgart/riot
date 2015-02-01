package de.uni_stuttgart.riot.thing.commons;

import java.sql.Timestamp;

public abstract class Instance {

    private Timestamp time;
    private long thingId;
    
    public Instance(Timestamp time, long thingId) {
        this.thingId = thingId;
        this.time = time;
    }
    
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public long getThingId() {
        return thingId;
    }

    public void setThingId(long thingId) {
        this.thingId = thingId;
    }
    
}
