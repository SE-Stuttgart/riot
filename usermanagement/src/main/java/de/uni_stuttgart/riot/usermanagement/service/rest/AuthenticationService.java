package de.uni_stuttgart.riot.usermanagement.service.rest;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.LoginRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.request.RefreshRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.response.AuthenticationResponse;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.InvalidTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LoginException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LogoutException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.RefreshException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.WrongCredentialsException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.role.GetPermissionsFromRoleException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetRolesFromUserException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * The authentication service will handle the authentication of a user based on a username + password and provide access tokens for further
 * usage of the API.
 *
 * @author Marcel Lehwald
 *
 */
@Path("auth")
@Consumes("application/json")
@Produces("application/json")
public class AuthenticationService {

    /**
     * Login as a user based on a provided username and password. On a successful authentication a access token and refresh token will be
     * returned which can be used to access the API. Returns HTTP 401 if the credentials were wrong.
     *
     * @param request
     *            The login request containing username and password.
     * @return Returns a new access and refresh token on success.
     * @throws LoginException
     *             When the operation fails for technical reasons.
     */
    @PUT
    @Path("/login")
    public Response login(LoginRequest request) throws LoginException {
        try {
            Token token = UserManagementFacade.getInstance().login(request.getUsername(), request.getPassword());
            User user = getUser(token);
            return Response.ok().entity(new AuthenticationResponse(token.getTokenValue(), token.getRefreshtokenValue(), user)).build();
        } catch (WrongCredentialsException e) {
            return Response.status(Status.UNAUTHORIZED).build();
        } catch (LoginException e) {
            throw e;
        } catch (UserManagementException e) {
            throw new LoginException(e);
        }
    }

    /**
     * Refresh an expired access token based on a refresh token. A refresh token will be used to generate a new access and refresh token. A
     * refresh token can only be used once and will be marked invalid when successfully used.
     *
     * @param request
     *            The refresh request containing a refresh token.
     * @return Returns a new access and refresh token on success.
     * @throws InvalidTokenException
     *             When the provided token is invalid.
     * @throws RefreshException
     *             When another, rather unexpected error occurs.
     */
    @PUT
    @Path("/refresh")
    public Response refresh(RefreshRequest request) throws InvalidTokenException, RefreshException {
        try {
            Token token = UserManagementFacade.getInstance().refreshToken(request.getRefreshToken());
            User user = getUser(token);
            return Response.ok().entity(new AuthenticationResponse(token.getTokenValue(), token.getRefreshtokenValue(), user)).build();
        } catch (InvalidTokenException e) {
            return Response.status(Status.UNAUTHORIZED).build();
        } catch (UserManagementException e) {
            throw new RefreshException(e);
        }
    }

    /**
     * Logout an authenticated user. The access and refresh token will therefore be marked as invalid.
     *
     * @return Returns an empty response.
     * @throws LogoutException
     *             When the operation fails.
     */
    @PUT
    @Path("/logout")
    @RequiresAuthentication
    public Response logout() throws LogoutException {
        String accessToken = (String) SecurityUtils.getSubject().getPrincipal();
        UserManagementFacade.getInstance().logout(accessToken);

        return Response.ok().build();
    }

    private User getUser(Token token) throws GetUserException, GetRolesFromUserException, GetPermissionsFromRoleException {
        UserManagementFacade facade = UserManagementFacade.getInstance();
        User u = facade.getUser(token);
        Collection<Permission> permissions = facade.getAllPermissionsFromUser(u.getId());
        Collection<Role> roles = facade.getAllRolesFromUser(u.getId());
        for (Role role : roles) {
            role.setPermissions(facade.getAllPermissionsOfRole(role.getId()));
        }

        return new User(u.getUsername(), u.getEmail(), roles, permissions);
    }

}
