package de.uni_stuttgart.riot.commons.rest.usermanagement.data;

/**
 * {@link Role}s can be assigned to {@link UMUser}s to grand certain rights. It can also be used
 * to group {@link Permission}s. By assigning a role to a user, the users receives all permissions associated to the role.
 * @author Jonas Tangermann
 *
 */
public class Role extends Storable {

    private String roleName;

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
    }

    /**
     * Constructor.
     * @param roleName .
     */
    public Role(String roleName) {
        super(-1L);
        this.setRoleName(roleName);
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


}
