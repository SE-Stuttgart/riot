package de.uni_stuttgart.riot.thing.remote;

import com.fasterxml.jackson.databind.util.ClassUtil;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

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

    public Property getTheProperty(Thing owner){
        switch (this.getValType()) { // TODO add types
        case "String":
            return new Property<String>(this.getName(), this.getVal());
        default:
            return new Property<String>(this.getName(), this.getVal());
        }
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
