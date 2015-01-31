package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

/**
 * TODO .
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
    public PropertySetAction(String propertyName, long thingId) {
        super(thingId);
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

    public PropertySetActionInstance<T> createInstance(Property<T> property){
        return new PropertySetActionInstance<T>(property, this);
    }

    @Override
    public String getFactoryString() {
        return "TODO";
    }

}
