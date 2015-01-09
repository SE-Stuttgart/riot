package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;

/**
 * {@link RolePermission} represents the n:m relation between {@link Role} and {@link Permission}. Thus this class only maps Roles to
 * Permission not adding any information.
 * 
 * @author Jonas Tangermann
 *
 */
public class RolePermission extends Storable {

    private Long permissionID;
    private Long roleID;

    public RolePermission() {
    }

    public RolePermission(Role role, Permission permission) {
        super(-1L);
        this.roleID = role.getId();
        this.permissionID = permission.getId();
    }

    public RolePermission(Long roleID, Long permissionID) {
        super(roleID);
        this.roleID = roleID;
        this.permissionID = permissionID;
    }

    public RolePermission(Long roleID, Long permissionID, Long rolePermissionID) {
        super(rolePermissionID);
        this.roleID = roleID;
        this.permissionID = permissionID;
    }

    public Long getRoleID() {
        return roleID;
    }

    public Long getPermissionID() {
        return permissionID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((permissionID == null) ? 0 : permissionID.hashCode());
        result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RolePermission other = (RolePermission) obj;
        if (permissionID == null) {
            if (other.permissionID != null)
                return false;
        } else if (!permissionID.equals(other.permissionID))
            return false;
        if (roleID == null) {
            if (other.roleID != null)
                return false;
        } else if (!roleID.equals(other.roleID))
            return false;
        return true;
    }

}
