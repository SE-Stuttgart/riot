package de.uni_stuttgart.riot.usermanagement.service.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.UserManagementFacade;

public class RoleResponse {

    private Role role;
    private Collection<PermissionResponse> permissions;

    public RoleResponse() {

    }

    public RoleResponse(Role role) throws UserManagementException {
        this.role = role;
        this.permissions = new ArrayList<PermissionResponse>();

        Collection<Permission> roles = UserManagementFacade.getInstance().getAllPermissionsOfRole(role.getId());
        for (Iterator<Permission> it = roles.iterator(); it.hasNext();) {
            this.permissions.add(new PermissionResponse(it.next()));
        }
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
