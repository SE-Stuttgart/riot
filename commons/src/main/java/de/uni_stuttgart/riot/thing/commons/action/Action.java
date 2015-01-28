package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

/**
 * This class represents an action to be executed by a {@link Thing}.
 *
 * @param <T>
 */
public abstract class Action<T extends ActionInstance> {

    /**
     * Constructor.
     */
    public Action() {
    }

    /**
     * Creates the instance.
     * 
     * @param params
     *            collection of properties.
     * @param owner
     *            the owner {@link Thing}.
     * @return the created instance.
     * @throws Exception .
     */
    public abstract T createInstance(Collection<Property> params, Thing owner) throws Exception;

}
