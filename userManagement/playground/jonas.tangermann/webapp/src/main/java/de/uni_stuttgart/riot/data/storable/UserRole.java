package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

public class UserRole implements Storable {

	private final Long userRoleID;
	private final Long userID;
	private final Long roleID;
	
	public UserRole(User user, Role role, Long userRoleID) {
		this.userID = user.getID();
		this.roleID = role.getID();
		this.userRoleID = userRoleID;
	}
	
	public UserRole(Long userID, Long roleID, Long userRoleID) {
		this.userID = userID;
		this.roleID = roleID;
		this.userRoleID = userRoleID;
	}
	
	@Override
	public long getID() {
		return this.userRoleID;
	}

	@Override
	public Collection<String> getSearchParam() {
		return new LinkedList<String>();
	}

	public Long getUserID() {
		return userID;
	}

	public Long getRoleID() {
		return roleID;
	}

}
