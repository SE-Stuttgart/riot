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
}
