package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

public class UserResponse {

    private User user;
    private Collection<RoleResponse> roles;

    public UserResponse() {

    }

    public UserResponse(User user, Collection<RoleResponse> userRoles) {
        this.user = user;
        this.roles = userRoles;
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
