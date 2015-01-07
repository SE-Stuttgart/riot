package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A {@link Permission} is the lowest level of authorization in the usermanagement. Permissions can be assigned to {@link UMUser}s over
 * {@link Role}s. The permission value is used internaly by the shiro framework. It can be used by calling //FIXME.
 * 
 * @author Jonas Tangermann
 *
 */
public class Permission extends Storable {

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
        super(-1L);
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
        super(id);
        this.setPermissionValue(permissionValue);
    }


    @Override
	public String toString() {
		return "Permission [permissionValue=" + permissionValue + "]";
	}

	/**
     * Getter for {@link Permission#permissionValue}.
     * 
     * @return Permission string in {@link WildcardPermission} format.
     */
    public String getPermissionValue() {
        return permissionValue;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((permissionValue == null) ? 0 : permissionValue.hashCode());
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
		Permission other = (Permission) obj;
		if (permissionValue == null) {
			if (other.permissionValue != null)
				return false;
		} else if (!permissionValue.equals(other.permissionValue))
			return false;
		return true;
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
}
