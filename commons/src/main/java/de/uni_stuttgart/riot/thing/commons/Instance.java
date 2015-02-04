package de.uni_stuttgart.riot.thing.commons;

import java.sql.Timestamp;

/**
 * The Base Class for Instances at the Things-Model.
 */
public abstract class Instance {

    /** The creation time of this instance. */
    private Timestamp time;

    /** The thing id to which this instance is related. */
    private long thingId;

    /**
     * Constructor.
     *
     * @param time
     *            the creation time of this instance
     * @param thingId
     *            the thing id to which this instance is related
     */
    public Instance(Timestamp time, long thingId) {
        this.thingId = thingId;
        this.time = time;
    }

    /**
     * Constructor.
     */
    public Instance() {
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public long getThingId() {
        return thingId;
    }

    public void setThingId(long thingId) {
        this.thingId = thingId;
    }
}
