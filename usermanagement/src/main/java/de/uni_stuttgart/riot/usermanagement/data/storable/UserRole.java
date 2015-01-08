<<<<<<< HEAD
package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Storable;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * {@link UserRole} represents the n:m relation between {@link UMUser} and {@link Role}. Thus this class only maps User to Role not adding any
 * information.
 * 
 * @author Jonas Tangermann
 *
 */
public class UserRole extends Storable {

    private Long userID;
    private Long roleID;

    public UserRole() {
    }

    public UserRole(UMUser user, Role role) {
        super(-1L);
    	this.userID = user.getId();
        this.roleID = role.getId();
    }

    public UserRole(Long userID, Long roleID) {
        super(-1L);
    	this.userID = userID;
        this.roleID = roleID;
    }

    public UserRole(Long userID, Long roleID, Long userRoleID) {
        super(userRoleID);
    	this.userID = userID;
        this.roleID = roleID;
    }

    public Long getUserID() {
        return userID;
    }

    public Long getRoleID() {
        return roleID;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
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
		UserRole other = (UserRole) obj;
		if (roleID == null) {
			if (other.roleID != null)
				return false;
		} else if (!roleID.equals(other.roleID))
			return false;
		if (userID == null) {
			if (other.userID != null)
				return false;
		} else if (!userID.equals(other.userID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserRole [userID=" + userID + ", roleID=" + roleID + "]";
	}


}
=======
package de.uni_stuttgart.riot.usermanagement.data.storable;

import de.uni_stuttgart.riot.commons.model.Storable;

/**
 * {@link UserRole} represents the n:m relation between {@link UMUser} and {@link Role}. Thus this class only maps User to Role not adding any
 * information.
 * 
 * @author Jonas Tangermann
 *
 */
public class UserRole implements Storable {

    private Long id;
    private Long userID;
    private Long roleID;

    public UserRole() {
    }

    public UserRole(UMUser user, Role role) {
        this.userID = user.getId();
        this.roleID = role.getId();
        this.id = -1L;
    }

    public UserRole(Long userID, Long roleID) {
        this.userID = userID;
        this.roleID = roleID;
        this.id = -1L;
    }

    public UserRole(Long userID, Long roleID, Long userRoleID) {
        this.userID = userID;
        this.roleID = roleID;
        this.id = userRoleID;
    }

    @Override
    public long getId() {
        return this.id;
    }

    public Long getUserID() {
        return userID;
    }

    public Long getRoleID() {
        return roleID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof UserRole))
            return false;
        UserRole other = (UserRole) obj;
        if (roleID == null) {
            if (other.roleID != null)
                return false;
        } else if (!roleID.equals(other.roleID))
            return false;
        if (userID == null) {
            if (other.userID != null)
                return false;
        } else if (!userID.equals(other.userID))
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
        builder.append("UserRole [userRoleID=");
        builder.append(id);
        builder.append(", userID=");
        builder.append(userID);
        builder.append(", roleID=");
        builder.append(roleID);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

}
>>>>>>> TEMP-140: checkstyle fixes
