package de.uni_stuttgart.riot.usermanagement.service.rest;

import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.UserRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.TokenResponse;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.usermanagement.service.rest.exception.UserManagementExceptionMapper;

/**
 * The users service will handle any access (create, read, update, delete) to the users.
 *
 * @author Marcel Lehwald <- // FIXME how to handle addUser with UMUser
 *
 */
@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class UserService extends BaseResource<UMUser> {

    private final UserManagementFacade facade = UserManagementFacade.getInstance();

    /**
     * Const.
     * 
     * @throws SQLException .
     * @throws NamingException .
     */
    public UserService() throws SQLException, NamingException {
        super(new UserSqlQueryDAO());
    }

    /**
     * Get the currently executing user.
     *
     * @return Returns the currently executing user.
     * @throws UserManagementException
     *             When getting the user fails.
     */
    @GET
    @Path("/self")
    public User getUser() throws UserManagementException {
        String accessToken = (String) SecurityUtils.getSubject().getPrincipal();
        Token token = facade.getToken(accessToken);
        User u = facade.getUser(token);
        return new User(u.getUsername(), u.getEmail(), this.getUserRoles(u.getId()), this.getUserPermissions(u.getId()));
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
    @POST
    @Path("sec")
    public User addUser(UserRequest userRequest) throws UserManagementException {
        User user = facade.addUser(userRequest.getUsername(), userRequest.getEmail(), userRequest.getPassword());
        return new User(user.getUsername(), user.getEmail(), this.getUserRoles(user.getId()), this.getUserPermissions(user.getId()));
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
    @Path("/sec/{userID}")
    public User updateUser(@PathParam("userID") Long userID, UserRequest userRequest) throws UserManagementException {
        User user = facade.getUser(userID);

        if (StringUtils.isNotEmpty(userRequest.getUsername())) {
            user.setUsername(userRequest.getUsername());
        }

        facade.updateUser(user, userRequest.getPassword());

        // the user contains after it is updated all the information it is updated with
        return new User(user.getUsername(), user.getEmail(), this.getUserRoles(userID), this.getUserPermissions(userID));
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
        Collection<Role> roles = facade.getAllRolesFromUser(userID);
        for (Role role : roles) {
            role.setPermissions(facade.getAllPermissionsOfRole(role.getId()));
        }
        return roles;
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
     * Get permissions of a user.
     *
     * @param userID
     *            The user ID.
     * @return Returns a list of all direct assigned permissions of a user.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{userID}/permissions")
    public Collection<Permission> getUserPermissions(@PathParam("userID") Long userID) throws UserManagementException {
        return facade.getAllPermissionsFromUser(userID);
    }

    /**
     * Add permission to a user.
     *
     * @param userID
     *            The user ID.
     * @param permissionID
     *            The permission ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @PUT
    @Path("/{userID}/permissions/{permissionID}")
    public Response addUserPermission(@PathParam("userID") Long userID, @PathParam("permissionID") Long permissionID) throws UserManagementException {
        facade.addPermissionToUser(userID, permissionID);
        return Response.ok().build();
    }

    /**
     * Remove permission of a user.
     *
     * @param userID
     *            The user ID.
     * @param permissionID
     *            The permission ID.
     * @return Returns empty response (with status code 200) on success.
     * @throws UserManagementException
     *             Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @DELETE
    @Path("/{userID}/permissions/{permissionID}")
    public Response removeUserPermission(@PathParam("userID") Long userID, @PathParam("permissionID") Long permissionID) throws UserManagementException {
        facade.removePermissionFromUser(userID, permissionID);
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
    public Collection<TokenResponse> getUserTokens(@PathParam("userID") Long userID) throws UserManagementException {
        return facade.getActiveTokensFromUser(userID).stream().map(TokenResponse::new).collect(Collectors.toList());
    }

    /**
     * Returns the current user online state (online or offline).
     * @param userID 
     * @return online if the user currently is logged in, offline otherwise.
     * @throws UserManagementException   
     *              Thrown when an internal error occurs. The exception will automatically be mapped to a proper response through the
     *             {@link UserManagementExceptionMapper} class.
     */
    @GET
    @Path("/{userID}/state")
    public int getUserOnlineState(@PathParam("userID") Long userID) throws UserManagementException {
        Collection<Token> tokens = facade.getActiveTokensFromUser(userID);
        if (tokens.isEmpty()) {
            return OnlineState.STATUS_OFFLINE.ordinal();
        } else {
            return OnlineState.STATUS_ONLINE.ordinal();
        }
    }

    // FIXME RFC
    @Override
    public void init(UMUser storable) throws Exception {
        storable.setRoles(this.getUserRoles(storable.getId()));
        storable.setPermissions(this.getUserPermissions(storable.getId()));
        storable.setHashedPassword("");
        storable.setHashIterations(0);
        storable.setLoginAttemptCount(0);
        storable.setPasswordSalt("");
    }

}
