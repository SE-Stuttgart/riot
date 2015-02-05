package de.uni_stuttgart.riot.thing.commons.action;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * Action for setting a property.
 * 
 * @param <T>
 */
public class PropertySetAction<T> extends Action<PropertySetActionInstance<T>> {

    private String propertyName;

    /**
     * Constructor.
     * 
     * @param propertyName
     *            .
     */
    public PropertySetAction(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Constructor.
     */
    public PropertySetAction() {
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Creates a instance of this action that could be submitted to the server.
     * 
     * @param newValue
     *            new value for the property.
     * @param thingId
     *            thing that should change its property.
     * @return the instance.
     */
    public PropertySetActionInstance<T> createInstance(T newValue, long thingId) {
        return new PropertySetActionInstance<T>(new Property<T>(propertyName, newValue), thingId, new Timestamp(System.currentTimeMillis()));
    }

}
