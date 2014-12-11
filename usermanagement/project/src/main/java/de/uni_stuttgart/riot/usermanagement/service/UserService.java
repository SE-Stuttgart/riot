package de.uni_stuttgart.riot.usermanagement.service;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.exception.UserManagementExceptionMapper;

/**
 * The users service will handle any access (create, read, update, delete) to the users.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

    UserManagementFacade facade = UserManagementFacade.getInstance();

    /**
     * Get all users.
     * 
     * @return Returns a list of all users.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    public Collection<User> getUsers() throws UserManagementException {
        // TODO limit returned users
        return facade.getAllUsers();
    }

    /**
     * Get a user.
     * 
     * @param userID
     *            The user ID.
     * @return Returns the user with the user ID.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{userID}")
    public User getUser(@PathParam("userID") Long userID) throws UserManagementException {
        return facade.getUser(userID);
    }

    /**
     * Add new user.
     * 
     * @param user
     *            The user ID.
     * @return Returns the added user.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    public User addUser(User user) throws UserManagementException {
        facade.addUser(user);

        // TODO return new user
        return null;
    }

    /**
     * Update user.
     * 
     * @param userID
     *            The user ID.
     * @param user
     *            The user.
     * @return Returns the updated user.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @Path("/{userID}")
    public User updateUser(@PathParam("userID") Long userID, User user) throws UserManagementException {
        facade.updateUser(userID, user);

        // TODO return updated user
        return null;
    }

    /**
     * Remove user.
     * 
     * @param userID
     *            The user ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @DELETE
    @Path("/{userID}")
    public Response removeUser(@PathParam("userID") Long userID) throws UserManagementException {
        facade.deleteUser(userID);

        return Response.ok().build();
    }

    /**
     * Get roles of a user.
     * 
     * @param userID
     *            The user ID.
     * @return Returns a list of all roles of a user.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{userID}/roles")
    public Collection<Role> getUserRoles(@PathParam("userID") Long userID) throws UserManagementException {
        // TODO limit returned roles
        return facade.getAllRolesFromUser(userID);
    }

    /**
     * Add role to a user.
     * 
     * @param userID
     *            The user ID.
     * @param roleID
     *            The role ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @Path("/{userID}/roles/{roleID}")
    public Response addUserRole(@PathParam("userID") Long userID, @PathParam("roleID") Long roleID) throws UserManagementException {
        facade.addRoleToUser(userID, roleID);

        return Response.ok().build();
    }

    /**
     * Remove role of a user.
     * 
     * @param userID
     *            The user ID.
     * @param roleID
     *            The role ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @DELETE
    @Path("/{userID}/roles/{roleID}")
    public Response removeUserRole(@PathParam("userID") Long userID, @PathParam("roleID") Long roleID) throws UserManagementException {
        facade.removeRoleFromUser(userID, roleID);

        return Response.ok().build();
    }

    /**
     * Get tokens of a user.
     * 
     * @param userID
     *            The user ID.
     * @return Returns a list of all tokens of a user.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{userID}/tokens")
    public Collection<Token> getUserTokens(@PathParam("userID") Long userID) throws UserManagementException {
        // TODO limit returned tokens
        return facade.getActiveTokensFromUser(userID);
    }

}
