package de.uni_stuttgart.riot.usermanagement.service.rest.response;

import java.util.Collection;
import java.util.stream.Collectors;

import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * Wrapper around a {@link User}.<br>
 * FIXME These wrapper classes should not be necessary. Instead, use JAXB annotations to tell, which attributes are to be sent to the
 * client.
 */
public class UserResponse {

    private User user;
    private Collection<RoleResponse> roles;

    /**
     * Default-Constructor for JAXB.
     */
    public UserResponse() {
    }

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

    /**
     * Gets the ID of the underlying user.
     * 
     * @return The ID.
     */
    public long getId() {
        return user.getId();
    }

    /**
     * Sets the ID of the underlying user.
     * 
     * @param id
     *            The new ID.
     */
    public void setId(long id) {
        user.setId(id);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Collection<RoleResponse> getRoles() {
        return roles;
    }

    public void setRoles(Collection<RoleResponse> roles) {
        this.roles = roles;
    }

}
