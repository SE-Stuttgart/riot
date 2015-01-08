package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;

/**
 * Wrapper around a {@link Role}.<br>
 * FIXME These wrapper classes should not be necessary. Instead, use JAXB
 * annotations to tell, which attributes are to be sent to the client.
 * CHECKSTYLE:OFF
 */
public class RoleResponse {

    private Role role;
    private Collection<PermissionResponse> permissions;

    public RoleResponse() {
    }

    public RoleResponse(Role role, Collection<PermissionResponse> permissions) {
        this.setRole(role);
        this.permissions = permissions;
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
