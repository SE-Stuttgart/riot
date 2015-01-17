package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

public class RemoteThingAction extends Storable{
    
    public RemoteThingAction(long thingID, long actionID) {
        this.thingID =thingID;
        this.actionID = actionID;
    }
    
    public long getThingID() {
        return thingID;
    }

    public long getActionID() {
        return actionID;
    }

    private final long thingID;
    private final long actionID;

}
