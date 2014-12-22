package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;

public class RoleResponse {

    private Role role;
    private Collection<PermissionResponse> permissions;

    public RoleResponse() {

    }

    public RoleResponse(Role role, Collection<PermissionResponse> rolePermis) {
        this.role = role;
        this.permissions = rolePermis;
    }

    public Long getId() {
        return role.getId();
    }

    public void setId(Long id) {
        role.setId(id);
    }

    public String getRoleName() {
        return role.getRoleName();
    }

    public void setRoleName(String roleName) {
        role.setRoleName(roleName);
    }

    public Collection<PermissionResponse> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<PermissionResponse> permissions) {
        this.permissions = permissions;
    }

}
