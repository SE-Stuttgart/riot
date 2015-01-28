package de.uni_stuttgart.riot.thing.commons.action;

import java.sql.Timestamp;

/**
 * TODO .
 *
 */
public abstract class ActionInstance {

    private Timestamp time;
    private Action instanceOf;

    /**
     * Constructor.
     * 
     * @param instanceOf
     *            .
     */
    public ActionInstance(Action instanceOf) {
        this.setTime(new Timestamp(System.currentTimeMillis()));
        this.setInstanceOf(instanceOf);
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Action getInstanceOf() {
        return instanceOf;
    }

    public void setInstanceOf(Action instanceOf) {
        this.instanceOf = instanceOf;
    }

}
