package de.uni_stuttgart.riot.thing.commons.event;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * The Change Event of a {@link Property}.
 * 
 * @param <T>
 */
public class PropertyChangeEvent<T> extends Event<PropertyChangeEventInstance<T>> {

    @Override
    public PropertyChangeEventInstance<T> createInstance(Collection<Property> params) throws Exception {
        if (params.size() != 2) {
            throw new Exception("Only one parameter allowed");
        }
        PropertyChangeEventInstance<T> result = null;
        for (Property property : params) {
            result = new PropertyChangeEventInstance<T>(property);
        }
        return result;
    }

    @Override
    public boolean isTypeOf(EventInstance eventInstance) {
        return eventInstance instanceof PropertyChangeEventInstance;
    }

}
