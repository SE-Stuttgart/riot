package de.uni_stuttgart.riot.thing.commons.event;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * The Change Event of a {@link Property}.
 * 
 * @param <T>
 */
public class PropertyChangeEvent<T> extends Event<PropertyChange<T>> {

    @Override
    public PropertyChange<T> createInstance(Collection<Property> params) throws Exception {
        if (params.size() != 2) {
            throw new Exception("Only one parameter allowed");
        }
        PropertyChange<T> result = null;
        for (Property property : params) {
            result = new PropertyChange<T>(property);
        }
        return result;
    }

}
