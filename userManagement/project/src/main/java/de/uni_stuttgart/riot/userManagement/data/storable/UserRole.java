package de.uni_stuttgart.riot.userManagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;

public class UserRole implements Storable {

    private final Long userRoleID;
    private final Long userID;
    private final Long roleID;

    public UserRole(User user, Role role, Long userRoleID) {
        this.userID = user.getId();
        this.roleID = role.getId();
        this.userRoleID = userRoleID;
    }

    public UserRole(Long userID, Long roleID, Long userRoleID) {
        this.userID = userID;
        this.roleID = roleID;
        this.userRoleID = userRoleID;
    }

    @Override
    public long getId() {
        return this.userRoleID;
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
        result = prime * result + ((userRoleID == null) ? 0 : userRoleID.hashCode());
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
        if (userRoleID == null) {
            if (other.userRoleID != null)
                return false;
        } else if (!userRoleID.equals(other.userRoleID))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserRole [userRoleID=");
        builder.append(userRoleID);
        builder.append(", userID=");
        builder.append(userID);
        builder.append(", roleID=");
        builder.append(roleID);
        builder.append("]");
        return builder.toString();
    }

}
