package de.uni_stuttgart.riot.usermanagement.service.rest;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.usermanagement.service.rest.exception.UserManagementExceptionMapper;
import de.uni_stuttgart.riot.usermanagement.service.rest.request.UserRequest;
import de.uni_stuttgart.riot.usermanagement.service.rest.response.RoleResponse;
import de.uni_stuttgart.riot.usermanagement.service.rest.response.TokenResponse;
import de.uni_stuttgart.riot.usermanagement.service.rest.response.UserResponse;

/**
 * The users service will handle any access (create, read, update, delete) to the users.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserService {

    private final UserManagementFacade facade = UserManagementFacade.getInstance();

    @Context
    private UriInfo uriInfo;

    /**
     * Get all users.
     * 
     * @return Returns a list of all users.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @RequiresAuthentication
    public Collection<UserResponse> getUsers() throws UserManagementException {
        // TODO limit returned users
        return facade.getAllUsers().stream().map(UserResponse::new).collect(Collectors.toList());
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
    @RequiresAuthentication
    public UserResponse getUser(@PathParam("userID") Long userID) throws UserManagementException {
        return new UserResponse(facade.getUser(userID));
    }
    
    /**
     * Get the currently executing user.
     * 
     * @return Returns the currently executing user.
     * @throws UserManagementException
     */
    @GET
    @Path("/self")
    @RequiresAuthentication
    public UserResponse getUser() throws UserManagementException {
    	String accessToken = (String) SecurityUtils.getSubject().getPrincipal();
    	System.out.println(accessToken);
    	Token token = facade.getToken(accessToken);
        return new UserResponse(facade.getUser(token));
    }

    /**
     * Add new user.
     * 
     * @param userRequest
     *            The user ID.
     * @return Returns the added user.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @RequiresAuthentication
    public UserResponse addUser(UserRequest userRequest) throws UserManagementException {
        User user = facade.addUser(userRequest.getUsername(), userRequest.getPassword());
        return new UserResponse(user);
    }

    /**
     * Update user.
     * 
     * @param userID
     *            The user ID.
     * @param userRequest
     *            The user.
     * @return Returns the updated user.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @Path("/{userID}")
    @RequiresAuthentication
    public UserResponse updateUser(@PathParam("userID") Long userID, UserRequest userRequest) throws UserManagementException {
        User user = facade.getUser(userID);

        if (StringUtils.isNotEmpty(userRequest.getUsername())) {
            user.setUsername(userRequest.getUsername());
        }

        facade.updateUser(user, userRequest.getPassword());

        // the user contains after it is updated all the information it is updated with
        return new UserResponse(user);
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
    @RequiresAuthentication
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
    @RequiresAuthentication
    public Collection<RoleResponse> getUserRoles(@PathParam("userID") Long userID) throws UserManagementException {
        // TODO limit returned roles
        return facade.getAllRolesFromUser(userID).stream().map(RoleResponse::new).collect(Collectors.toList());
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
    @RequiresAuthentication
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
    @RequiresAuthentication
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
    @RequiresAuthentication
    public Collection<TokenResponse> getUserTokens(@PathParam("userID") Long userID) throws UserManagementException {
        // TODO limit returned tokens
        return facade.getActiveTokensFromUser(userID).stream().map(TokenResponse::new).collect(Collectors.toList());
    }

}
