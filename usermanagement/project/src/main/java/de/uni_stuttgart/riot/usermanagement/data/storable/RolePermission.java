package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * {@link RolePermission} represents the n:m relation between {@link Role} and {@link Permission}.
 * Thus this class only maps Roles to Permission not adding any information.
 * @author Jonas Tangermann
 *
 */
public class RolePermission implements Storable {

    private Long rolePermissionID;
    private final Long permissionID;
    private final Long roleID;

    public RolePermission(Role role, Permission permission) {
        this.roleID = role.getId();
        this.rolePermissionID = -1L;
        this.permissionID = permission.getId();
    }

    public RolePermission(Long roleID, Long permissionID, Long rolePermissionID) {
        this.roleID = roleID;
        this.rolePermissionID = rolePermissionID;
        this.permissionID = permissionID;
    }

    @Override
    public long getId() {
        return this.rolePermissionID;
    }

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> result = new LinkedList<SearchParameter>();
        result.add(new SearchParameter(SearchFields.ROLEID, this.getRoleID()));
        result.add(new SearchParameter(SearchFields.PERMISSIONID, this.getPermissionID()));
        return result;
    }

    public Long getRoleID() {
        return roleID;
    }

    public Long getPermissionID() {
        return permissionID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((permissionID == null) ? 0 : permissionID.hashCode());
        result = prime * result + ((roleID == null) ? 0 : roleID.hashCode());
        result = prime * result + ((rolePermissionID == null) ? 0 : rolePermissionID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof RolePermission))
            return false;
        RolePermission other = (RolePermission) obj;
        if (permissionID == null) {
            if (other.permissionID != null)
                return false;
        } else if (!permissionID.equals(other.permissionID))
            return false;
        if (roleID == null) {
            if (other.roleID != null)
                return false;
        } else if (!roleID.equals(other.roleID))
            return false;
        if (rolePermissionID == null) {
            if (other.rolePermissionID != null)
                return false;
        } else if (!rolePermissionID.equals(other.rolePermissionID))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RolePermission [rolePermissionID=");
        builder.append(rolePermissionID);
        builder.append(", permissionID=");
        builder.append(permissionID);
        builder.append(", roleID=");
        builder.append(roleID);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public void setId(long id) {
        this.rolePermissionID =id;
    }

}
