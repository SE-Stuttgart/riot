package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.action.Action;

/**
 * Action object to be stored at the DB.
 *
 */
public class ActionDBObject extends Storable {

    /**
     * Factory string that is used to read/write the {@link Action} object from/to the datasource.
     * Currently json serialization is used.  
     */
    private final String factoryString;
    
    /**
     * Id of the thing this action relates to.
     */
    private final long thingId;

    /**
     * Constructor for {@link ActionDBObject}.
     * @param factoryString 
     *              json representation of the {@link Action} that is stored through this DB Object.
     * @param thingId 
     *              id of the thing this action relates to.
     */
    public ActionDBObject(long thingId, String factoryString) {
        this.thingId = thingId;
        this.factoryString = factoryString;
    }

    /**
     * Getter for {@link #factoryString}.
     * @return the factory string in json representation.
     */
    public String getFactoryString() {
        return factoryString;
    }

    /**
     * Getter for {@link #thingId}.
     * @return the thing id.
     */
    public long getThingId() {
        return thingId;
    }

}
