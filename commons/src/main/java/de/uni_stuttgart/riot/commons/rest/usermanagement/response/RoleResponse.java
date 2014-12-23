package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;

public class RoleResponse {

    private Role role;
    private Collection<PermissionResponse> permissions;

    public RoleResponse() {

    }

    public RoleResponse(Role role, Collection<PermissionResponse> rolePermis) {
        this.setRole(role);
        this.permissions = rolePermis;
    }

    public Collection<PermissionResponse> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<PermissionResponse> permissions) {
        this.permissions = permissions;
    }

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "RoleResponse [role=" + role + ", permissions=" + permissions
				+ "]";
	}

	
}
