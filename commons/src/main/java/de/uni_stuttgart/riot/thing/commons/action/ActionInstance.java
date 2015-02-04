package de.uni_stuttgart.riot.thing.commons.action;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Instance;

/**
 * TODO .
 *
 */
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

    public abstract void accept(ActionInstanceVisitor actionInstanceVisitor);

}
