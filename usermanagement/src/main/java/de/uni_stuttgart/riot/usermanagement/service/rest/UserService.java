package de.uni_stuttgart.riot.usermanagement.service.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

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

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.PermissionResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.RoleResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.TokenResponse;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.UserResponse;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetRolesFromUserException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.usermanagement.service.rest.exception.UserManagementExceptionMapper;

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

    @Context
    private UriInfo uriInfo;
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
    @RequiresAuthentication
    public Collection<UserResponse> getUsers() throws UserManagementException {
        // TODO limit returned users
        Collection<User> users = facade.getAllUsers();

        Collection<UserResponse> userResponse = new ArrayList<UserResponse>();
        for (Iterator<User> it = users.iterator(); it.hasNext();) {
            User u = it.next();
        	userResponse.add(new UserResponse(u,this.getUserRoles(u)));
        }

        return userResponse;
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
        User u = facade.getUser(userID);
    	return new UserResponse(u,this.getUserRoles(u));
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
    @RequiresAuthentication
    public UserResponse addUser(User user) throws UserManagementException {
        facade.addUser(user);
        User u = facade.getUser(user.getId());
        return new UserResponse(u,this.getUserRoles(u));
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
    @RequiresAuthentication
    public UserResponse updateUser(@PathParam("userID") Long userID, User user) throws UserManagementException {
        user.setId(userID);
        facade.updateUser(user);
        User u = facade.getUser(user.getId());
        return new UserResponse(u,this.getUserRoles(u));
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
        Collection<Role> roles = facade.getAllRolesFromUser(userID);

        Collection<RoleResponse> roleResponse = new ArrayList<RoleResponse>();
        for (Iterator<Role> it = roles.iterator(); it.hasNext();) {            
            Role r = it.next();
            roleResponse.add(new RoleResponse(r,this.getRolePermissions(r)));
        }

        return roleResponse;
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
        Collection<Token> tokens = facade.getActiveTokensFromUser(userID);

        Collection<TokenResponse> tokenResponse = new ArrayList<TokenResponse>();
        for (Iterator<Token> it = tokens.iterator(); it.hasNext();) {
            tokenResponse.add(new TokenResponse(it.next()));
        }

        return tokenResponse;
    }
    
    private Collection<RoleResponse> getUserRoles(User user) throws GetRolesFromUserException, GetPermissionsFromRoleException  {
    	Collection<Role> roles = UserManagementFacade.getInstance().getAllRolesFromUser(user.getId());
    	Collection<RoleResponse> roleResponses = new LinkedList<RoleResponse>();
    	for (Role role : roles) {
			roleResponses.add(new RoleResponse(role, this.getRolePermissions(role)));
		}
    	return roleResponses;
    }
    
    private Collection<PermissionResponse> getRolePermissions(Role role) throws GetPermissionsFromRoleException{
    	Collection<Permission> permissions = UserManagementFacade.getInstance().getAllPermissionsOfRole(role.getId());
    	Collection<PermissionResponse> permissionResponses = new LinkedList<PermissionResponse>();
    	for (Permission permission : permissions) {
			permissionResponses.add(new PermissionResponse(permission));
		}
    	return permissionResponses;
    }

}
