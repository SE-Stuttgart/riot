package de.uni_stuttgart.riot.thing.commons.action;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.Thing;

public abstract class Action<T extends ActionInstance> {

    public Action() {
    }

    public abstract T createInstance(Collection<Property> params, Thing owner) throws Exception;

}
