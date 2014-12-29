package de.uni_stuttgart.riot.usermanagement.service.rest.response;

import java.util.Collection;
import java.util.stream.Collectors;

import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * Wrapper around a {@link User}.
 * 
 * @author Philipp
 *
 */
public class UserResponse {

    private final User user;
    private final Collection<RoleResponse> roles;

    /**
     * Creates a new {@link UserResponse} and retrieves the user's roles.
     * 
     * @param user
     *            The user to be wrapped.
     */
    public UserResponse(User user) {
        this.user = user;
        try {
            this.roles = UserManagementFacade.getInstance().getAllRolesFromUser(user.getId()).stream().map(RoleResponse::new).collect(Collectors.toList());
        } catch (UserManagementException e) {
            throw new RuntimeException(e);
        }
    }

    public Long getId() {
        return user.getId();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public Collection<RoleResponse> getRoles() {
        return roles;
    }

}
