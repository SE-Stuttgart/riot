package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

/**
 * Enum for all searchable fields.
 * 
 * @author Jonas Tangermann
 *
 */
public enum SearchFields {
    /**
     * Search field for: {@link UserRole#getUserID()}.
     */
    USERID("USERID"),
    /**
     * Search field for: {@link UserRole#getRoleID()}.
     */
    ROLEID("roleID"),
    /**
     * Search field for: {@link Permission#getPermissionValue()}.
     */
    PERMISSIONVALUE("permissionValue"),
    /**
     * Search field for: {@link Role#getRoleName()}.
     */
    ROLENAME("rolename"),
    /**
     * Search field for: {@link RolePermission#getPermissionID()}.
     */
    PERMISSIONID("permissionID"),
    /**
     * Search field for: {@link Token#getTokenValue()}.
     */
    TOKENVALUE("tokenValue"),
    /**
     * Search field for: {@link Token#getIssueTime()()}.
     */
    ISSUETIME("issueTime"),
    /**
     * Search field for: {@link Token#getExpirationTime()}.
     */
    EXPIRATIONTIME("expirationTime"),
    /**
     * Search field for: {@link User#getUsername()}.
     */
    USERNAME("username"),
    /**
     * Search field for: {@link TokenRole#getTokenID()}.
     */
    TOKENID("tokenID"),

    REFRESHTOKEN("refreshtokenValue"),

    TOKENVALID("valid"),

    LOGINATTEMPTCOUNT("loginAttemptCount");

    private final String text;

    private SearchFields(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
