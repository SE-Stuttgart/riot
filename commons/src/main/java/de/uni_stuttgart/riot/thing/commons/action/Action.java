package de.uni_stuttgart.riot.thing.commons.action;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.thing.commons.Thing;

/**
 * This class represents an action that can be executed by a {@link Thing}.
 *
 * @param <T>
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public abstract class Action<T extends ActionInstance> {

}
