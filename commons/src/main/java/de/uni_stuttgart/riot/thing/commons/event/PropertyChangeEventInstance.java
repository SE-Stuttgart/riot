package de.uni_stuttgart.riot.thing.commons.event;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * TODO This class contains the new and old (?) property values.
 *
 * @param <T>
 */
public class PropertyChangeEventInstance<T> extends EventInstance {

    private Property<T> newProperty;

    /**
     * Constructor.
     * 
     * @param newProperty
     *            the new property.
     * @param thingId
     *            the thing id
     * @param time
     *            creation time
     */
    public PropertyChangeEventInstance(Property<T> newProperty, long thingId, Timestamp time) {
        super(time, thingId);
        this.setNewProperty(newProperty);
    }

    /**
     * Default constructor.
     */
    public PropertyChangeEventInstance() {
    }

    public Property<T> getNewProperty() {
        return newProperty;
    }

    public void setNewProperty(Property<T> newProperty) {
        this.newProperty = newProperty;
    }

}
