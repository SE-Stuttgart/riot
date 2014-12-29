package de.uni_stuttgart.riot.usermanagement.service.rest.response;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;

/**
 * Wrapper around {@link Permission} to only expose the desired fields over REST.
 * FIXME Do we need this class in the first place?
 */
public class PermissionResponse {

    private Permission permission;

    /**
     * Creates a new PermissionResponse.
     * 
     * @param permission
     *            The wrapped Permission.
     */
    public PermissionResponse(Permission permission) {
        this.permission = permission;
    }

    public Long getId() {
        return permission.getId();
    }

    public String getPermissionValue() {
        return permission.getPermissionValue();
    }

}
