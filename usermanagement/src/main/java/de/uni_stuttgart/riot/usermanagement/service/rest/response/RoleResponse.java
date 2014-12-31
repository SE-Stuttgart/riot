package de.uni_stuttgart.riot.usermanagement.service.rest.response;

import java.util.Collection;
import java.util.stream.Collectors;

import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * Wrapper around a {@link Role}.<br>
 * FIXME These wrapper classes should not be necessary. Instead, use JAXB annotations to tell, which attributes are to be sent to the
 * client. CHECKSTYLE:OFF
 */
public class RoleResponse {

    private Role role;
    private Collection<PermissionResponse> permissions;

    /**
     * Default-Constructor for JAXB.
     */
    public RoleResponse() {
    }

    /**
     * Creates a wrapper around the role and retrieves its permissions.
     * 
     * @param role
     *            The role to be wrapped.
     */
    public RoleResponse(Role role) {
        this.role = role;
        try {
            this.permissions = UserManagementFacade.getInstance().getAllPermissionsOfRole(role.getId()).stream().map(PermissionResponse::new).collect(Collectors.toList());
        } catch (GetPermissionsFromRoleException e) {
            throw new RuntimeException(e);
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
