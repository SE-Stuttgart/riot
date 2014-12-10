package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;
import java.util.LinkedList;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * {@link Role}s can be assigned to {@link User}s to grand certain rights. It can also be used
 * to group {@link Permission}s. By assigning a role to a user, the users receives all permissions associated to the role.
 * @author Jonas Tangermann
 *
 */
public class Role implements Storable {

    private Long id;
    private String roleName;

    public Role(Long id, String roleName) {
        this.id = id;
        this.setRoleName(roleName);
    }
    
    public Role(String roleName) {
        this.id = -1L;
        this.setRoleName(roleName);
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Collection<SearchParameter> getSearchParam() {
        LinkedList<SearchParameter> params = new LinkedList<SearchParameter>();
        params.add(new SearchParameter(SearchFields.ROLENAME, this.getRoleName()));
        return params;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((roleName == null) ? 0 : roleName.hashCode());
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
        Role other = (Role) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (roleName == null) {
            if (other.roleName != null)
                return false;
        } else if (!roleName.equals(other.roleName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [id=");
        builder.append(id);
        builder.append(", roleName=");
        builder.append(roleName);
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public void setId(long id) {
        this.id =id;
    }


}
