package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.data.TableName;

/**
 * {@link Role}s can be assigned to {@link UMUser}s to grand certain rights. It can also be used
 * to group {@link Permission}s. By assigning a role to a user, the users receives all permissions associated to the role.
 * @author Jonas Tangermann
 *
 */
@TableName("roles")
public class Role extends Storable {

    private String roleName;
    
    private transient Collection<Permission> permissions;

    /**
     * // FIXME.
     */
    public Role() {
    } 

    /**
     * Constructor.
     * @param id .
     * @param roleName .
     */
    public Role(Long id, String roleName) {
        super(id);
        this.setRoleName(roleName);
        this.permissions = new ArrayList<Permission>();
    }

    /**
     * Constructor.
     * @param roleName .
     */
    public Role(String roleName) {
        super(-1L);
        this.setRoleName(roleName);
        this.permissions = new ArrayList<Permission>();

    }
    
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
        return result;
    }

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
        Role other = (Role) obj;
        if (roleName == null) {
            if (other.roleName != null) {
                return false;
            }
        } else if (!roleName.equals(other.roleName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Role [roleName=" + roleName + "]";
    }

    public Collection<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Collection<Permission> permissions) {
        this.permissions = permissions;
    }


}
