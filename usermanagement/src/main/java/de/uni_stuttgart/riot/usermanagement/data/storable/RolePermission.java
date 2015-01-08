<<<<<<< HEAD
package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Storable;

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
		result = prime * result
				+ ((permissionID == null) ? 0 : permissionID.hashCode());
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
=======
package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.model.Storable;

/**
 * {@link RolePermission} represents the n:m relation between {@link Role} and {@link Permission}. Thus this class only maps Roles to
 * Permission not adding any information.
 * 
 * @author Jonas Tangermann
 *
 */
public class RolePermission implements Storable {

    private Long id;
    private Long permissionID;
    private Long roleID;

    public RolePermission() {
    }

    public RolePermission(Role role, Permission permission) {
        this.roleID = role.getId();
        this.id = -1L;
        this.permissionID = permission.getId();
    }

    public RolePermission(Long roleID, Long permissionID) {
        this.roleID = roleID;
        this.id = -1L;
        this.permissionID = permissionID;
    }

    public RolePermission(Long roleID, Long permissionID, Long rolePermissionID) {
        this.roleID = roleID;
        this.id = rolePermissionID;
        this.permissionID = permissionID;
    }

    @Override
    public long getId() {
        return this.id;
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
        int result = 1;
        result = prime * result + ((permissionID == null) ? 0 : permissionID.hashCode());
        result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof RolePermission))
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RolePermission [rolePermissionID=");
        builder.append(id);
        builder.append(", permissionID=");
        builder.append(permissionID);
        builder.append(", roleID=");
        builder.append(roleID);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

}
>>>>>>> TEMP-140: checkstyle fixes
