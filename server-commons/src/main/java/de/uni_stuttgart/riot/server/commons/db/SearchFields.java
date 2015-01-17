package de.uni_stuttgart.riot.server.commons.db;

import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

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
    /**
     * Search field for: {@link Token#getTokenValue()}.
     */
    REFRESHTOKEN("refreshtokenValue"),
    /**
     * Search field for: {@link Token#getRefreshtokenValue()}.
     */
    TOKENVALID("valid"),
    /**
     * Search field for: UMUser.getLoginAttempCount.
     */
    LOGINATTEMPTCOUNT("loginAttemptCount"),
    /**
     * Search field for:.
     */
    TABLEPK("id"),
    /**
     * Search field for: {@link ConfigurationStorable#getConfigKey()}.
     * */
    CONFIGKEY("configKey"),

    /**
     * Search field for:.
     */
    NAME("name"), 
    /**
     * Search field for:.
     */
    THINGID("thingID"),
    
        /**
     * Search field for:.
     */
    OWNERID("ownerID");
    
    private final String text;

    private SearchFields(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
