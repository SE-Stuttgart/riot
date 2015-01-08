package de.uni_stuttgart.riot.commons.rest.usermanagement.response;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;

public class UserResponse {

    private User user;
    private Collection<RoleResponse> roles;

    public UserResponse() {

    }

    public UserResponse(User user, Collection<RoleResponse> userRoles) {
        this.setUser(user);
        this.roles = userRoles;
    }

    public Collection<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleResponse> roles) {
        this.roles = roles;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "UserResponse [user=" + user + ", roles=" + roles + "]";
	}
	
	

}
