package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

/**
 * {@link UserPermission} represents the n:m relation between {@link User} and {@link Permission}. Thus this class only maps Users to
 * Permission not adding any information.
 * 
 * @author Niklas Schnabel
 *
 */
public class UserPermission extends Storable {

    /** The permission id. */
    private Long permissionID;
    
    /** The role id. */
    private Long userID;

    /**
     * Instantiates a new user permission.
     */
    public UserPermission() {
        permissionID = -1L;
        userID = -1L;
    }

    /**
     * Instantiates a new user permission.
     *
     * @param user
     *            the user
     * @param permission
     *            the permission
     */
    public UserPermission(User user, Permission permission) {
        super(-1L);
        this.userID = user.getId();
        this.permissionID = permission.getId();
    }

    /**
     * Instantiates a new user permission.
     *
     * @param userID
     *            the role id
     * @param permissionID
     *            the permission id
     */
    public UserPermission(Long userID, Long permissionID) {
        super(-1L);
        this.userID = userID;
        this.permissionID = permissionID;
    }

    /**
     * Instantiates a new user permission.
     *
     * @param userID
     *            the role id
     * @param permissionID
     *            the permission id
     * @param userPermissionID
     *            the role permission id
     */
    public UserPermission(Long userID, Long permissionID, Long userPermissionID) {
        super(userPermissionID);
        this.userID = userID;
        this.permissionID = permissionID;
    }

    /**
     * Gets the permission id.
     *
     * @return the permission id
     */
    public Long getPermissionID() {
        return permissionID;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public Long getUserID() {
        return userID;
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.commons.rest.data.Storable#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((permissionID == null) ? 0 : permissionID.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.commons.rest.data.Storable#equals(java.lang.Object)
     */
    @Override
    //CHECKSTYLE: OFF - Autgenerated method
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserPermission other = (UserPermission) obj;
        if (permissionID == null) {
            if (other.permissionID != null)
                return false;
        } else if (!permissionID.equals(other.permissionID))
            return false;
        if (userID == null) {
            if (other.userID != null)
                return false;
        } else if (!userID.equals(other.userID))
            return false;
        return true;
    }
}
