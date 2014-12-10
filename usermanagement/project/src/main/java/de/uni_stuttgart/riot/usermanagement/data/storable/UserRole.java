package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * {@link UserRole} represents the n:m relation between {@link User} and {@link Role}.
 * Thus this class only maps User to Role not adding any information.
 * @author Jonas Tangermann
 *
 */
public class UserRole implements Storable {

    private Long id;
    private Long userID;
    private Long roleID;

    public UserRole() {
    }
    
    public UserRole(User user, Role role) {
        this.userID = user.getId();
        this.roleID = role.getId();
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

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> result = new LinkedList<SearchParameter>();
        result.add(new SearchParameter(SearchFields.ROLEID, this.getRoleID()));
        result.add(new SearchParameter(SearchFields.USERID, this.getUserID()));
        return result;
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
        this.id =id;
    }

}
