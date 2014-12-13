package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * {@link TokenRole} represents the n:m relation between {@link Token} and {@link Role}.
 * Thus this class only maps Token to Role not adding any information.
 * @author Jonas Tangermann
 *
 */
public class TokenRole implements Storable {

    private Long id;
    private Long tokenID;
    private Long roleID;

    public final static String SEARCH_PARAM_ROLEID = "roleID";
    public final static String SEARCH_PARAM_TOKENID = "tokenID";

    public TokenRole() {
    }
    
    public TokenRole(Token token, Role role, Long tokenRoleID) {
        this.tokenID = token.getId();
        this.roleID = role.getId();
        this.id = tokenRoleID;
    }

    public TokenRole(Long tokenRoleID, Long tokenID, Long roleID) {
        this.tokenID = tokenID;
        this.roleID = roleID;
        this.id = tokenRoleID;
    }

    public TokenRole(Long tokenID, Long roleID) {
        this.tokenID = tokenID;
        this.roleID = roleID;
        this.id = -1L;
    }
    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> result = new LinkedList<SearchParameter>();
        result.add(new SearchParameter(SearchFields.ROLEID, this.getRoleID()));
        result.add(new SearchParameter(SearchFields.TOKENID, this.getTokenID()));
        return result;
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
        int result = 1;
        result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
        result = prime * result + ((tokenID == null) ? 0 : tokenID.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TokenRole))
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
        builder.append("TokenRole [tokenRoleID=");
        builder.append(id);
        builder.append(", tokenID=");
        builder.append(tokenID);
        builder.append(", roleID=");
        builder.append(roleID);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public void setId(long id) {
        this.id =id;
    }

}
