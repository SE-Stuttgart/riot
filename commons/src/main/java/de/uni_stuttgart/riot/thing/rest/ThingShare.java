package de.uni_stuttgart.riot.thing.rest;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A {@link ThingShare} is similar to a ThingUser from the DB layer, but it does not contain the thing (ID) itself, so that must be
 * referenced from outside. Thing shares are used as a RESTful resource accessible under <tt>/things/{id}/shares</tt>.
 */
public class ThingShare {

    /** The permissions. */
    private Set<ThingPermission> permissions;

    /** The user id. */
    private long userId;

    /**
     * Instantiates a new thing share.
     */
    public ThingShare() {
    }

    /**
     * Instantiates a new thing share.
     *
     * @param userId
     *            the user
     * @param permissions
     *            the permissions
     */
    public ThingShare(long userId, Set<ThingPermission> permissions) {
        this.userId = userId;
        this.permissions = permissions;
    }

    public Set<ThingPermission> getPermissions() {
        return permissions;
    }

    public long getUserId() {
        return userId;
    }

    public void setPermissions(Set<ThingPermission> permissions) {
        this.permissions = permissions;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Checks if the share is empty.
     * 
     * @return True if there are no permissions.
     */
    @JsonIgnore
    public boolean isEmpty() {
        return permissions == null || permissions.isEmpty();
    }

    /**
     * Check if this share contains the given permission.
     * 
     * @param permission
     *            The permission to check for.
     * @return True if the share contains the permission.
     */
    public boolean permits(ThingPermission permission) {
        return permissions != null && permissions.contains(permission);
    }

}
