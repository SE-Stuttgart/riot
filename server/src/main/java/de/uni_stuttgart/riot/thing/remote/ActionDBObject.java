package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.action.Action;

public class ActionDBObject extends Storable{
    
    private final String factoryString;
    
    public ActionDBObject(String factoryString) {
        this.factoryString = factoryString;
    }

    public String getFactoryString() {
        return factoryString;
    }
    
    public Action getTheAction(){
        return null; // TODO Impl
    }

}
