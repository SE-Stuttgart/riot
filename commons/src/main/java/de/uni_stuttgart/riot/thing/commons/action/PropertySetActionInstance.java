package de.uni_stuttgart.riot.thing.commons.action;

import de.uni_stuttgart.riot.thing.commons.Property;

public class PropertySetActionInstance<T> extends ActionInstance {

    private final Property<T> property;
    
    public PropertySetActionInstance(Property<T> property, PropertySetAction<T> instanceOf) {
        super(instanceOf);
        this.property = property;
    }

    public Property<T> getProperty() {
        return property;
    }
}
