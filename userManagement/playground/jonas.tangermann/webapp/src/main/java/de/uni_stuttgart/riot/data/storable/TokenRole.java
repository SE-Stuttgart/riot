package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

public class TokenRole implements Storable{

	private final Long tokenRoleID;
	private final Long tokenID;
	private final Long roleID;
	
	public TokenRole(Token token, Role role, Long tokenRoleID) {
		this.tokenID = token.getID();
		this.roleID = role.getID();
		this.tokenRoleID = tokenRoleID;
	}
	
	public TokenRole(Long tokenID, Long roleID, Long tokenRoleID) {
		this.tokenID = tokenID;
		this.roleID = roleID;
		this.tokenRoleID = tokenRoleID;
	}
	
	@Override
	public long getID() {
		return this.tokenRoleID;
	}

	@Override
	public Collection<String> getSearchParam() {
		return new LinkedList<String>();
	}

	public Long getRoleID() {
		return roleID;
	}

	public Long getTokenID() {
		return tokenID;
	}
}
