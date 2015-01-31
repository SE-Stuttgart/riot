package de.uni_stuttgart.riot.thing.commons;

/**
 * Logical representation of a Thing on the server side. It represents one {@link ClientThing}.
 *
 */
public class RemoteThing extends Thing {

    /**
     * Id of the user that owns this thing.
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

    /**
     * gets the id of user that owns this thing.
     * 
     * @return owner id
     */
    public long getOwnerID() {
        return ownerID;
    }

    /**
     * sets the owner id.
     * 
     * @param ownerID
     *            if of user that owns this Thing
     */
    public void setOwnerID(long ownerID) {
        this.ownerID = ownerID;
    }

}
