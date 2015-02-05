package de.uni_stuttgart.riot.thing.remote;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * DB representation of a {@link Property}.
 *
 */
public class PropertyDBObject extends Storable {
    
    private final String name;
    private final String val;
    private final String valType;
    private final long thingID;

    /**
     * Constructor for {@link PropertyDBObject}.
     * @param name
     *      Name of the {@link Property} -> {@link Property#getName()}
     * @param value
     *      Value of the {@link Property} -> {@link Property#getValue()}
     * @param valTye
     *      Type of value of the {@link Property}
     * @param thingID
     *      thing id this property belongs to.
     */
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
