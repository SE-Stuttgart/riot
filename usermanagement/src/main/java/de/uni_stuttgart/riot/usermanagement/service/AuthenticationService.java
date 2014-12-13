package de.uni_stuttgart.riot.usermanagement.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.GenerateTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LogoutException;
import de.uni_stuttgart.riot.usermanagement.service.request.LoginRequest;
import de.uni_stuttgart.riot.usermanagement.service.request.RefreshRequest;
import de.uni_stuttgart.riot.usermanagement.service.response.AuthenticationResponse;

/**
 * The authentication service will handle the authentication of a user based on a username + password and provide access tokens for further
 * usage of the API.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/auth")
@Consumes("application/json")
@Produces("application/json")
public class AuthenticationService {

    /**
     * Login as a user based on a provided username and password. On a successful authentication a access token and refresh token will be
     * returned which can be used to access the API.
     * 
     * @param request
     *            The login request containing username and password.
     * @return Returns a new access and refresh token on success.
     * @throws GenerateTokenException
     */
    @PUT
    @Path("/login")
    public AuthenticationResponse login(LoginRequest request) throws GenerateTokenException {
        return UserManagementFacade.getInstance().login(request.getUsername(), request.getPassword());
    }

    /**
     * Refresh an expired access token based on a refresh token. A refresh token will be used to generate a new access and refresh token. A
     * refresh token can only be used once and will be marked invalid when successfully used.
     * 
     * @param request
     *            The refresh request containing a refresh token.
     * @return Returns a new access and refresh token on success.
     * @throws GenerateTokenException
     */
    @PUT
    @Path("/refresh")
    public AuthenticationResponse refresh(RefreshRequest request) throws GenerateTokenException {
        return UserManagementFacade.getInstance().refreshToken(request.getRefreshToken());
    }

    /**
     * Logout an authenticated user. The access and refresh token will therefore be marked as invalid.
     * 
     * @return Returns an empty response.
     * @throws LogoutException
     */
    @PUT
    @Path("/logout")
    @RequiresAuthentication
    public Response logout() throws LogoutException {
        String accessToken = (String) SecurityUtils.getSubject().getPrincipal();
        UserManagementFacade.getInstance().logout(accessToken);

        return Response.ok().build();
    }
}