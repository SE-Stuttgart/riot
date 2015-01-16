package de.uni_stuttgart.riot.thing.commons.event;

import de.uni_stuttgart.riot.thing.commons.Property;

public class PropertyChange<T> extends EventInstance {
    
    private final Property<T> newProperty;
    
    public PropertyChange(Property<T> newProperty) {
        this.newProperty = newProperty;
    }

    public Property<T> getNewProperty() {
        return newProperty;
    }
}