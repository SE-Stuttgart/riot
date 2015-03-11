package de.uni_stuttgart.riot.db.thing;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;

/**
 * {@link ThingUser} represents the n:m multi-relation between {@link Thing} and {@link User}. If a relation entry is present, this means
 * that the user can access the given thing. The field {@link #permission} details the type of allowed access.
 * 
 * @author Philipp Keck
 *
 */
public class ThingUser extends Storable {

    /** The thing id. */
    private Long thingID;

    /** The user id. */
    private Long userID;

    /** The permission what the user can do with the thing. */
    private ThingPermission permission;

    /**
     * Instantiates a new ThingUser.
     */
    public ThingUser() {
        this(-1L, -1L, null);
    }

    /**
     * Instantiates a new ThingUser.
     *
     * @param thing
     *            the thing
     * @param user
     *            the user
     * @param permission
     *            the permission
     */
    public ThingUser(Thing thing, User user, ThingPermission permission) {
        this(thing.getId(), user.getId(), permission);
    }

    /**
     * Instantiates a new ThingUser.
     *
     * @param thingID
     *            the thing id
     * @param userID
     *            the user id
     * @param permission
     *            the permission
     */
    public ThingUser(Long thingID, Long userID, ThingPermission permission) {
        this(thingID, userID, permission, -1L);
    }

    /**
     * Instantiates a new role permission.
     *
     * @param thingID
     *            the thing id
     * @param userID
     *            the user id
     * @param permission
     *            the permission
     * @param thingUserID
     *            the storable id.
     */
    public ThingUser(Long thingID, Long userID, ThingPermission permission, Long thingUserID) {
        super(thingUserID);
        this.thingID = thingID;
        this.userID = userID;
        this.permission = permission;
    }

    /**
     * Gets the thing id.
     *
     * @return the thing id
     */
    public Long getThingID() {
        return thingID;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public Long getUserID() {
        return userID;
    }

    /**
     * Gets the permission.
     * 
     * @return The permission that the user has for the thing.
     */
    public ThingPermission getPermission() {
        return permission;
    }

    // CHECKSTYLE:OFF
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((permission == null) ? 0 : permission.hashCode());
        result = prime * result + ((thingID == null) ? 0 : thingID.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ThingUser other = (ThingUser) obj;
        if (permission != other.permission)
            return false;
        if (thingID == null) {
            if (other.thingID != null)
                return false;
        } else if (!thingID.equals(other.thingID))
            return false;
        if (userID == null) {
            if (other.userID != null)
                return false;
        } else if (!userID.equals(other.userID))
            return false;
        return true;
    }

}
