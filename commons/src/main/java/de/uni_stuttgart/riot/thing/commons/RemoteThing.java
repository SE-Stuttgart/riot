package de.uni_stuttgart.riot.thing.commons;

/**
 * TODO .
 *
 */
public class RemoteThing extends Thing {

    /**
     * TODO .
     */
    private long ownerID;

    /**
     * Constructor.
     * 
     * @param name
     *            the name
     * @param ownerID
     *            the owner ID
     */
    public RemoteThing(String name, long ownerID) {
        super(name);
        this.ownerID = ownerID;
    }

    /**
     * Constructor.
     */
    public RemoteThing() {
    }

    public long getOwnerID() {
        return ownerID;
    }

}
