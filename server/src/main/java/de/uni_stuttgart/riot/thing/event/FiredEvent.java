package de.uni_stuttgart.riot.thing.event;

import java.sql.Timestamp;

public abstract class FiredEvent {
    
    private Timestamp time;
    
    public FiredEvent() {
        this.setTime(new Timestamp(System.currentTimeMillis()));
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

}
