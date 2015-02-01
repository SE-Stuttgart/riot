package de.uni_stuttgart.riot.thing.commons.action;

import java.sql.Time;
import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * TODO .
 *
 * @param <T>
 */
public class PropertySetActionInstance<T> extends ActionInstance {

    private Property<T> property;

    /**
     * Constructor.
     * 
     * @param property
     *            .
     * @param instanceOf
     *            .
     */
    public PropertySetActionInstance(Property<T> property, long thingid, Timestamp time) {
        super(time, thingid);
        this.setProperty(property);
    }

    public PropertySetActionInstance() {
        super(new Timestamp(0),-1);
    }
    
    public Property<T> getProperty() {
        return property;
    }

    public void setProperty(Property<T> property) {
        this.property = property;
    }
}
