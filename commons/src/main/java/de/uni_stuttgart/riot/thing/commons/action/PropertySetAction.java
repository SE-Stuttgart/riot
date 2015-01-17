package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

public class PropertySetAction<T> extends Action<PropertySetActionInstance<T>> {

    private final transient String propertyName;
    
    public PropertySetAction(String propertyName, Thing belongsTo) {
        super(belongsTo);
        this.propertyName = propertyName;
    }

    @Override
    public PropertySetActionInstance<T> createInstance(Collection<Property> params) throws Exception {
        if(params.size() != 1) throw new Exception("Only one parameter allowed");
        PropertySetActionInstance<T> result = null;
        for (Property property : params) {
            if(!this.getOwner().hasProperty(property) || !property.getName().equals(this.propertyName)) 
                throw new Exception("Thing does not have such a property");
            result = new PropertySetActionInstance<T>(property, this);
        }
        return result;
    }
    
}
