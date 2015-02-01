package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Action object to be stored at the DB.
 *
 */
public class ActionDBObject extends Storable {

    private final String factoryString;
    private final long thingId;

    /**
     * Constructor.
     * 
     * @param factoryString
     *            .
     */
    public ActionDBObject(long thingId, String factoryString) {
        this.thingId = thingId;
        this.factoryString = factoryString;
    }

    public String getFactoryString() {
        return factoryString;
    }

    public long getThingId() {
        return thingId;
    }

}
