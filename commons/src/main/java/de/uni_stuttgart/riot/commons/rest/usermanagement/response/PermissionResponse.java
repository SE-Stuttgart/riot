package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;


public class PermissionResponse {

    private Permission permission;
    
    public PermissionResponse() {

    }

    public PermissionResponse(Permission permission) {
        this.setPermission(permission);
    }

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return "PermissionResponse [permission=" + permission + "]";
	}
	
	
}
