package de.uni_stuttgart.riot.thing.commons;

import de.uni_stuttgart.riot.thing.commons.event.Event;

public class RegisterRequest {
    
    private long registerOnThingId;
    private long thingId;
    private Event event;
    
    public RegisterRequest() {
    }
    
    public RegisterRequest(long thingid, long registerOnThingId, Event action) {
        this.event = action;
        this.thingId = thingid;
        this.registerOnThingId = registerOnThingId;
    }

    public long getThingId() {
        return thingId;
    }
    public void setThingId(long thingId) {
        this.thingId = thingId;
    }
    public Event getEvent() {
        return event;
    }
    public void setEvent(Event action) {
        this.event = action;
    }

    public long getRegisterOnThingId() {
        return registerOnThingId;
    }

    public void setRegisterOnThingId(long registerOnThingId) {
        this.registerOnThingId = registerOnThingId;
    }

}
