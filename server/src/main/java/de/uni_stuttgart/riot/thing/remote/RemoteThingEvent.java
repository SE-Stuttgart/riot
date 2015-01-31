package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

public class RemoteThingEvent extends Storable{
    
    public RemoteThingEvent(long thingID, long eventID) {
        this.thingID =thingID;
        this.eventID = eventID;
    }
    
    public long getThingID() {
        return thingID;
    }

    public long getEventID() {
        return eventID;
    }

    private final long thingID;
    private final long eventID;

}
