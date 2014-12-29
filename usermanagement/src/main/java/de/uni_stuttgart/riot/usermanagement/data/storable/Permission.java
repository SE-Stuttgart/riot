package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.shiro.authz.permission.WildcardPermission;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * A {@link Permission} is the lowest level of authorization in the usermanagement. Permissions can be assigned to {@link UMUser}s over
 * {@link Role}s. The permission value is used internaly by the shiro framework. It can be used by calling //FIXME.
 * 
 * @author Jonas Tangermann
 *
 */
public class Permission implements Storable {

    private long id;

    /**
     * The permission string that is interpreted as {@link WildcardPermission}.
     */
    private String permissionValue;

    /**
     * FIXME There is no need for this constructor. Plus, the id field should be final and we don't need a setter for the ID?? This should
     * be resolved when the DB layer is merged with the server project.
     */
    public Permission() {
    }

    /**
     * Constructor for {@link Permission}.
     * 
     * @param permissionValue
     *            the permission string in {@link WildcardPermission} format.
     */
    public Permission(String permissionValue) {
        this.id = -1L;
        this.setPermissionValue(permissionValue);
    }

    /**
     * Constructor for {@link Permission}.
     * 
     * @param id
     *            the unique id
     * @param permissionValue
     *            the permission string in {@link WildcardPermission} format.
     */
    public Permission(long id, String permissionValue) {
        this.id = id;
        this.setPermissionValue(permissionValue);
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> params = new LinkedList<SearchParameter>();
        params.add(new SearchParameter(SearchFields.PERMISSIONVALUE, this.getPermissionValue()));
        return params;
    }

    /**
     * Getter for {@link Permission#permissionValue}.
     * 
     * @return Permission string in {@link WildcardPermission} format.
     */
    public String getPermissionValue() {
        return permissionValue;
    }

    /**
     * Setter for {@link Permission#permissionValue}.
     * 
     * @param permissionValue
     *            Permission string in {@link WildcardPermission} format.
     */
    public void setPermissionValue(String permissionValue) {
        this.permissionValue = permissionValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32)); // NOCS
        result = prime * result + ((permissionValue == null) ? 0 : permissionValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Permission other = (Permission) obj;
        if (id != other.id) {
            return false;
        }
        if (permissionValue == null) {
            if (other.permissionValue != null) {
                return false;
            }
        } else if (!permissionValue.equals(other.permissionValue)) {
            return false;
        }
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
        this.id = id;
    }

}
