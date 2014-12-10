package de.uni_stuttgart.riot.usermanagement.service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.logic.exception.LogicException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.AddUserException;
import de.uni_stuttgart.riot.usermanagement.security.TokenUtil;
import de.uni_stuttgart.riot.usermanagement.service.exception.ApiErrorResponse;
import de.uni_stuttgart.riot.usermanagement.service.request.LoginRequest;
import de.uni_stuttgart.riot.usermanagement.service.request.RefreshRequest;
import de.uni_stuttgart.riot.usermanagement.service.response.AuthenticationResponse;
import de.uni_stuttgart.riot.usermanagement.service.response.LogoutResponse;


/**
 * The authentication service will handle the authentication of a user based on a username + password and provide access tokens for further
 * usage of the API.
 * 
 * @author Marcel Lehwald
 *
 */
@Path("/auth/")
@Consumes("application/json")
@Produces("application/json")
public class AuthenticationService {

    /**
     * Login as a user based on a provided username and password.
     * 
     * @param request The login request containing username and password.
     * @return Returns the refresh response containing a new access and refresh token on success.
     */
    @PUT
    @Path("/login/")
    public AuthenticationResponse login(LoginRequest request) {
       return new AuthenticationResponse("TODO", "TODO");
    }

    /**
     * Refresh an expired access token based on a refresh token. An refresh token will be used to generate a new access and refresh token.
     * An refresh token can only be used once and will be marked invalid when successfully used.
     * 
     * @param request The refresh request containing a refresh token.
     * @return Returns the refresh response containing a new access and refresh token on success.
     */
    @PUT
    @Path("/refresh/")
    public AuthenticationResponse refresh(RefreshRequest request) {
        String authToken = TokenUtil.generateToken();
        String refreshToken = TokenUtil.generateToken();

        // TODO save tokens in db

        return new AuthenticationResponse(authToken, refreshToken);
    }

    /**
     * Logout an authenticated user. The access and refresh token will therefore be marked as invalid.
     * 
     * @return Returns the logout response.
     */
    @GET
    @Path("/logout/")
    public LogoutResponse logout() {
        // TODO
        // return new LogoutResponse();
        return null;
    }
}
