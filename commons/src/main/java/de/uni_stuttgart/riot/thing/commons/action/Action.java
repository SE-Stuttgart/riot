package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

public abstract class Action<T extends ActionInstance> extends Storable {
    
    private final transient Thing owner;
    private final String className; // FIXME changeName to factoryString
    
    public Action(Thing belongsTo) {
        this.owner = belongsTo;
        this.className = this.getClass().getSimpleName();
    }
    
    public abstract T createInstance(Collection<Property> params) throws Exception;


    public String getClassName() {
        return className;
    }

    public Thing getOwner() {
        return owner;
    }

}
