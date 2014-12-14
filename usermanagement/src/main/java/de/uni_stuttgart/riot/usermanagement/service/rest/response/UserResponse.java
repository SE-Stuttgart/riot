package de.uni_stuttgart.riot.usermanagement.service.rest.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

public class UserResponse {

    private User user;
    private Collection<RoleResponse> roles;

    public UserResponse() {

    }

    public UserResponse(User user) throws UserManagementException {
        this.user = user;
        this.roles = new ArrayList<RoleResponse>();

        Collection<Role> roles = UserManagementFacade.getInstance().getAllRolesFromUser(user.getId());
        for (Iterator<Role> it = roles.iterator(); it.hasNext();) {
            this.roles.add(new RoleResponse(it.next()));
        }
    }

    public Long getId() {
        return user.getId();
    }

    public void setId(Long id) {
        user.setId(id);
    }

    public String getUsername() {
        return user.getUsername();
    }

    public void setUsername(String username) {
        user.setUsername(username);
    }

    public Collection<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleResponse> roles) {
        this.roles = roles;
    }

}
