package de.uni_stuttgart.riot.thing.action;

import java.sql.Timestamp;

public abstract class ExecutedAction {
    
    private Timestamp time;
    private Action instanceOf;
    
    public ExecutedAction(Action instanceOf) {
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
