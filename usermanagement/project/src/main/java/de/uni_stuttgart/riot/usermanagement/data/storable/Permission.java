package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.shiro.authz.permission.WildcardPermission;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * A {@link Permission} is the lowest level of authorization in the usermanagement.
 * Permissions can be assigned to {@link User}s over {@link Role}s. The permission value
 *  is used internaly by the shiro framework. It can be used by calling //FIXME. 
 * @author Jonas Tangermann
 *
 */
public class Permission implements Storable {

    private final Long permissionID;

    /**
     * The permission string that is interpreted as {@link WildcardPermission}.
     */
    private String permissionValue;

    /**
     * Constructor for {@link Permission}
     * @param permissionID the unique id
     * @param permissionValue the permission string in {@link WildcardPermission} format.
     */
    public Permission(Long permissionID, String permissionValue) {
        this.permissionID = permissionID;
        this.setPermissionValue(permissionValue);
    }

    @Override
    public long getId() {
        return this.permissionID;
    }

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> params = new LinkedList<SearchParameter>();
        params.add(new SearchParameter(SearchFields.PERMISSIONVALUE, this.getPermissionValue()));
        return params;
    }

    /**
     * Getter for {@link Permission#permissionValue}
     * @return Permission string in {@link WildcardPermission} format.
     */
    public String getPermissionValue() {
        return permissionValue;
    }

    /**
     * Setter for {@link Permission#permissionValue}
     * @param permissionValue Permission string in {@link WildcardPermission} format.
     */
    public void setPermissionValue(String permissionValue) {
        this.permissionValue = permissionValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((permissionID == null) ? 0 : permissionID.hashCode());
        result = prime * result + ((permissionValue == null) ? 0 : permissionValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Permission other = (Permission) obj;
        if (permissionID == null) {
            if (other.permissionID != null)
                return false;
        } else if (!permissionID.equals(other.permissionID))
            return false;
        if (permissionValue == null) {
            if (other.permissionValue != null)
                return false;
        } else if (!permissionValue.equals(other.permissionValue))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Permission [permissionID=");
        builder.append(permissionID);
        builder.append(", permissionValue=");
        builder.append(permissionValue);
        builder.append("]");
        return builder.toString();
    }

}
