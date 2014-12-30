package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;


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
        this.setPermission(permission);
    }
    
    public PermissionResponse() {
	}

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}


}
