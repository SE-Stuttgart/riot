package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;

/**
 * {@link UserRole} represents the n:m relation between {@link UMUser} and {@link Role}. Thus this class only maps User to Role not adding
 * any information.
 * 
 * @author Jonas Tangermann
 *
 */
public class UserRole extends Storable {

    /** The user id. */
    private Long userID;
    
    /** The role id. */
    private Long roleID;

    /**
     * Instantiates a new user role.
     */
    public UserRole() {
    }

    /**
     * Instantiates a new user role.
     *
     * @param user
     *            the user
     * @param role
     *            the role
     */
    public UserRole(UMUser user, Role role) {
        super(-1L);
        this.userID = user.getId();
        this.roleID = role.getId();
    }

    /**
     * Instantiates a new user role.
     *
     * @param userID
     *            the user id
     * @param roleID
     *            the role id
     */
    public UserRole(Long userID, Long roleID) {
        super(-1L);
        this.userID = userID;
        this.roleID = roleID;
    }

    /**
     * Instantiates a new user role.
     *
     * @param userID
     *            the user id
     * @param roleID
     *            the role id
     * @param userRoleID
     *            the user role id
     */
    public UserRole(Long userID, Long roleID, Long userRoleID) {
        super(userRoleID);
        this.userID = userID;
        this.roleID = roleID;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public Long getUserID() {
        return userID;
    }

    /**
     * Gets the role id.
     *
     * @return the role id
     */
    public Long getRoleID() {
        return roleID;
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.commons.rest.data.Storable#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
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
        UserRole other = (UserRole) obj;
        if (roleID == null) {
            if (other.roleID != null) {
                return false;
            }
        } else if (!roleID.equals(other.roleID)) {
            return false;
        }
        if (userID == null) {
            if (other.userID != null) {
                return false;
            }
        } else if (!userID.equals(other.userID)) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UserRole [userID=" + userID + ", roleID=" + roleID + "]";
    }

}
