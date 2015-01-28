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

    @Override
    public PropertySetActionInstance<T> createInstance(Collection<Property> params, Thing owner) throws Exception {
        if (params.size() != 1) {
            throw new Exception("Only one parameter allowed");
        }
        PropertySetActionInstance<T> result = null;
        for (Property property : params) {
            if (!owner.hasProperty(property) || !property.getName().equals(this.propertyName)) {
                throw new Exception("Thing does not have such a property");
            }
            result = new PropertySetActionInstance<T>(property, this);
        }
        return result;
    }

}
