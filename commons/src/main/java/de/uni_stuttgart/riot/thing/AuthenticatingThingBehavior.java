package de.uni_stuttgart.riot.thing;

import de.uni_stuttgart.riot.thing.rest.ThingPermission;

/**
 * This interface marks {@link ThingBehavior} implementations that can decide whether other users have access to their thing or not.
 * 
 * @author Philipp Keck
 */
public interface AuthenticatingThingBehavior {

    /**
     * Determines if the given user has the given permission on the thing managed by this behavior.
     * 
     * @param userId
     *            The user id.
     * @param permission
     *            The permission to check for.
     * @return True if the user has the permission (or the FULL permission), false otherwise.
     */
    boolean canAccess(Long userId, ThingPermission permission);

}
