package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.event.Event;

/**
 * Event object to be stored at the DB.
 *
 */
public class EventDBObject extends Storable {


    /**
     * Factory string that is used to read/write the {@link Event} object from/to the datasource.
     * Currently json serialization is used.  
     */
    private final String factoryString;
    
    /**
     * Id of the thing this action relates to.
     */
    private final long thingId;

    /**
     * Constructor for {@link EventDBObject}.
     * @param factoryString
     *          the factory string.
     * @param thingId
     *          the thing id.
     */
    public EventDBObject(long thingId, String factoryString) {
        this.factoryString = factoryString;
        this.thingId = thingId;
    }

    public String getFactoryString() {
        return factoryString;
    }

    public long getThingId() {
        return thingId;
    }

}
