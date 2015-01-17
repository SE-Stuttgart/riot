package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

public abstract class Action<T extends ActionInstance> {
    
    private final transient Thing owner;
    
    public Action(Thing belongsTo) {
        this.owner = belongsTo;
    }
    
    public abstract T createInstance(Collection<Property> params) throws Exception;

    public Thing getOwner() {
        return owner;
    }

}
