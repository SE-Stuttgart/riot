package de.uni_stuttgart.riot.thing.commons.action;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * TODO .
 *
 * @param <T>
 */
public class PropertySetActionInstance<T> extends ActionInstance {

    private final Property<T> property;

    /**
     * Constructor.
     * 
     * @param property
     *            .
     * @param instanceOf
     *            .
     */
    public PropertySetActionInstance(Property<T> property, PropertySetAction<T> instanceOf) {
        super(instanceOf);
        this.property = property;
    }

    public Property<T> getProperty() {
        return property;
    }
    
}
