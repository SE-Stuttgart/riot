package de.uni_stuttgart.riot.thing.commons.action;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.thing.commons.Instance;

/**
 * Instance of Action.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public abstract class ActionInstance extends Instance {

    /**
     * Instantiates a new action instance.
     *
     * @param time
     *            the time the action instance should be executed.
     * @param thingId
     *            the id of thing that should execute this action instance.
     */
    public ActionInstance(Timestamp time, long thingId) {
        super(time, thingId);
    }

    /**
     * Visitor pattern. calls the specific handle operation
     * @param actionInstanceVisitor Visitor.
     */
    public abstract void accept(ActionInstanceVisitor actionInstanceVisitor);

}
