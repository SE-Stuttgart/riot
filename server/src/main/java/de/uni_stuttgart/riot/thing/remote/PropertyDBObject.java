package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

public class PropertyDBObject extends Storable {
    
    private final String name;
    private final String val;
    private final String valType;
    private final long thingID;


    public PropertyDBObject(String name, String value, String valTye, long thingID) {
        this.name = name;
        this.valType = valTye;
        this.val = value;
        this.thingID = thingID;
    }

    public String getName() {
        return name;
    }


    public String getVal() {
        return val;
    }


    public String getValType() {
        return valType;
    }

    public long getThingID() {
        return thingID;
    }
    
}
