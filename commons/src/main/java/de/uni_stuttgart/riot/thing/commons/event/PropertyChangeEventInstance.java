package de.uni_stuttgart.riot.thing.commons.event;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * TODO This class contains the new and old (?) property values.
 *
 * @param <T>
 */
public class PropertyChangeEventInstance<T> extends EventInstance {

    private final Property<T> newProperty;

    /**
     * Constructor.
     * 
     * @param newProperty
     *            the new property.
     */
    public PropertyChangeEventInstance(Property<T> newProperty) {
        this.newProperty = newProperty;
    }

    public Property<T> getNewProperty() {
        return newProperty;
    }
}