package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * {@link TokenRole} represents the n:m relation between {@link Token} and {@link Role}.
 * Thus this class only maps Token to Role not adding any information.
 * @author Jonas Tangermann
 *
 */
public class TokenRole extends Storable {

    private Long tokenID;
    private Long roleID;

    public final static String SEARCH_PARAM_ROLEID = "roleID";
    public final static String SEARCH_PARAM_TOKENID = "tokenID";

    public TokenRole() {
    }
    
    public TokenRole(Token token, Role role, Long tokenRoleID) {
    	super(tokenRoleID);
    	this.tokenID = token.getId();
        this.roleID = role.getId();
    }

    public TokenRole(Long tokenRoleID, Long tokenID, Long roleID) {
    	super(tokenRoleID);
    	this.tokenID = tokenID;
        this.roleID = roleID;
    }

    public TokenRole(Long tokenID, Long roleID) {
    	super(-1L);
    	this.tokenID = tokenID;
        this.roleID = roleID;
    }

    public Long getRoleID() {
        return roleID;
    }

    public Long getTokenID() {
        return tokenID;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
		result = prime * result + ((tokenID == null) ? 0 : tokenID.hashCode());
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
		TokenRole other = (TokenRole) obj;
		if (roleID == null) {
			if (other.roleID != null)
				return false;
		} else if (!roleID.equals(other.roleID))
			return false;
		if (tokenID == null) {
			if (other.tokenID != null)
				return false;
		} else if (!tokenID.equals(other.tokenID))
			return false;
		return true;
	}
    
    

}
