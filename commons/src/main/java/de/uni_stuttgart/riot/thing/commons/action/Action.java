package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

/**
 * This class represents an action to be executed by a {@link Thing}.
 *
 * @param <T>
 */
public abstract class Action<T extends ActionInstance> {

    private long thingId;
    
    /**
     * Constructor.
     */
    public Action() {
    }
    
    public Action(long thingId) {
        this.thingId = thingId;
    }

    public long getThingId() {
        return thingId;
    }

    public void setThingId(long thingId) {
        this.thingId = thingId;
    }

    public abstract String getFactoryString();
}
