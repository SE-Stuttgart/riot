package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

public class PropertySetAction<T> extends Action<PropertySetActionInstance<T>> {

    private String propertyName;
    
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public PropertySetAction(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public PropertySetAction() {
    }

    @Override
    public PropertySetActionInstance<T> createInstance(Collection<Property> params, Thing owner) throws Exception {
        if(params.size() != 1) throw new Exception("Only one parameter allowed");
        PropertySetActionInstance<T> result = null;
        for (Property property : params) {
            if(!owner.hasProperty(property) || !property.getName().equals(this.propertyName)) 
                throw new Exception("Thing does not have such a property");
            result = new PropertySetActionInstance<T>(property, this);
        }
        return result;    
    }
    
}
