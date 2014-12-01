package de.uni_stuttgart.riot.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.data.sqlquerydao.SearchFields;
import de.uni_stuttgart.riot.data.sqlquerydao.SearchParameter;

public class TokenRole implements Storable {

    private final Long tokenRoleID;
    private final Long tokenID;
    private final Long roleID;

    public final static String SEARCH_PARAM_ROLEID = "roleID";
    public final static String SEARCH_PARAM_TOKENID = "tokenID";

    public TokenRole(Token token, Role role, Long tokenRoleID) {
        this.tokenID = token.getID();
        this.roleID = role.getID();
        this.tokenRoleID = tokenRoleID;
    }

    public TokenRole(Long tokenRoleID, Long tokenID, Long roleID) {
        this.tokenID = tokenID;
        this.roleID = roleID;
        this.tokenRoleID = tokenRoleID;
    }

    @Override
    public long getID() {
        return this.tokenRoleID;
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
        result = prime * result + ((tokenRoleID == null) ? 0 : tokenRoleID.hashCode());
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
        if (tokenRoleID == null) {
            if (other.tokenRoleID != null)
                return false;
        } else if (!tokenRoleID.equals(other.tokenRoleID))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TokenRole [tokenRoleID=");
        builder.append(tokenRoleID);
        builder.append(", tokenID=");
        builder.append(tokenID);
        builder.append(", roleID=");
        builder.append(roleID);
        builder.append("]");
        return builder.toString();
    }
}
