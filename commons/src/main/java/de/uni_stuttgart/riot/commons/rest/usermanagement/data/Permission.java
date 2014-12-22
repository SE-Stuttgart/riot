package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A {@link Permission} is the lowest level of authorization in the usermanagement.
 * Permissions can be assigned to {@link User}s over {@link Role}s. The permission value
 *  is used internaly by the shiro framework. It can be used by calling //FIXME. 
 * @author Jonas Tangermann
 *
 */
public class Permission implements Storable {

    private long id;
    
    public Permission() {
    }

    /**
     * The permission string that is interpreted as {@link WildcardPermission}.
     */
    private String permissionValue;

    /**
     * Constructor for {@link Permission}
     * @param id the unique id
     * @param permissionValue the permission string in {@link WildcardPermission} format.
     */
    public Permission(String permissionValue) {
        this.id = -1L;
        this.setPermissionValue(permissionValue);
    }

    public Permission(long id, String permissionValue) {
        this.id = id;
        this.setPermissionValue(permissionValue);    }

    @Override
    public long getId() {
        return this.id;
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


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((permissionValue == null) ? 0 : permissionValue.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Permission))
            return false;
        Permission other = (Permission) obj;
        if (id != other.id)
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
        builder.append(id);
        builder.append(", permissionValue=");
        builder.append(permissionValue);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public void setId(long id) {
        this.id =id;
    }

}
