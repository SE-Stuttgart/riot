package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.data.TableName;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;

/**
 * {@link TokenRole} represents the n:m relation between {@link Token} and {@link Role}. Thus this class only maps Token to Role not adding
 * any information.
 * 
 * @author Jonas Tangermann
 *
 */
@TableName("tokens_roles")
public class TokenRole extends Storable {

    /** The Constant SEARCH_PARAM_ROLEID. */
    public static final String SEARCH_PARAM_ROLEID = "roleID";
    
    /** The Constant SEARCH_PARAM_TOKENID. */
    public static final String SEARCH_PARAM_TOKENID = "tokenID";

    /** The token id. */
    private Long tokenID;
    
    /** The role id. */
    private Long roleID;

    /**
     * Instantiates a new token role.
     */
    public TokenRole() {
        tokenID = -1L;
        roleID = -1L;
    }

    /**
     * Instantiates a new token role.
     *
     * @param token
     *            the token
     * @param role
     *            the role
     * @param tokenRoleID
     *            the token role id
     */
    public TokenRole(Token token, Role role, Long tokenRoleID) {
        super(tokenRoleID);
        this.tokenID = token.getId();
        this.roleID = role.getId();
    }

    /**
     * Instantiates a new token role.
     *
     * @param tokenRoleID
     *            the token role id
     * @param tokenID
     *            the token id
     * @param roleID
     *            the role id
     */
    public TokenRole(Long tokenRoleID, Long tokenID, Long roleID) {
        super(tokenRoleID);
        this.tokenID = tokenID;
        this.roleID = roleID;
    }

    /**
     * Instantiates a new token role.
     *
     * @param tokenID
     *            the token id
     * @param roleID
     *            the role id
     */
    public TokenRole(Long tokenID, Long roleID) {
        super(-1L);
        this.tokenID = tokenID;
        this.roleID = roleID;
    }

    /**
     * Gets the role id.
     *
     * @return the role id
     */
    public Long getRoleID() {
        return roleID;
    }

    /**
     * Gets the token id.
     *
     * @return the token id
     */
    public Long getTokenID() {
        return tokenID;
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.commons.rest.data.Storable#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
        result = prime * result + ((tokenID == null) ? 0 : tokenID.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.commons.rest.data.Storable#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TokenRole other = (TokenRole) obj;
        if (roleID == null) {
            if (other.roleID != null) {
                return false;
            }
        } else if (!roleID.equals(other.roleID)) {
            return false;
        }
        if (tokenID == null) {
            if (other.tokenID != null) {
                return false;
            }
        } else if (!tokenID.equals(other.tokenID)) {
            return false;
        }
        return true;
    }

}
