package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.thing.commons.Thing;

public class RemoteThing extends Thing {

    private final long ownerID;
    
    public RemoteThing(String name, long ownerID) {
        super(name);
        this.ownerID = ownerID;
    }

    public long getOwnerID() {
        return ownerID;
    }

}
