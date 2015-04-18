package de.uni_stuttgart.riot.thing.rest;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * An extension of {@link ThingShare} where the user ID has been resolved to a full {@link User}.
 */
public class UserThingShare {

    /** The user. */
    private final User user;

    /** The permissions. */
    private final Set<ThingPermission> permissions;

    /**
     * Creates a new instance.
     * 
     * @param user
     *            The user.
     * @param permissions
     *            The permissions.
     */
    @JsonCreator
    public UserThingShare(@JsonProperty("user") User user, @JsonProperty("permissions") Set<ThingPermission> permissions) {
        this.permissions = permissions;
        this.user = user;
    }

    public Set<ThingPermission> getPermissions() {
        return permissions;
    }

    public User getUser() {
        return user;
    }

}
