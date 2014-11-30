package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

public class RolePermission implements Storable{
	
	private final Long rolePermissionID;
	private final Long permissionID;
	private final Long roleID;
	
	public RolePermission(Role role, Permission permission, Long rolePermissionID) {
		this.roleID = role.getID();
		this.rolePermissionID = rolePermissionID;
		this.permissionID = permission.getID();
	}
	
	public RolePermission(Long roleID, Long permissionID, Long rolePermissionID) {
		this.roleID = roleID;
		this.rolePermissionID = rolePermissionID;
		this.permissionID = permissionID;
	}
	
	@Override
	public long getID() {
		return this.rolePermissionID;
	}

	@Override
	public Collection<String> getSearchParam() {
		return new LinkedList<String>();
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
		result = prime * result
				+ ((permissionID == null) ? 0 : permissionID.hashCode());
		result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
		result = prime
				* result
				+ ((rolePermissionID == null) ? 0 : rolePermissionID.hashCode());
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
		if (rolePermissionID == null) {
			if (other.rolePermissionID != null)
				return false;
		} else if (!rolePermissionID.equals(other.rolePermissionID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RolePermission [rolePermissionID=");
		builder.append(rolePermissionID);
		builder.append(", permissionID=");
		builder.append(permissionID);
		builder.append(", roleID=");
		builder.append(roleID);
		builder.append("]");
		return builder.toString();
	}
}
