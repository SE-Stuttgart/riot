package de.uni_stuttgart.riot.usermanagement.logic;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NamingException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LoginException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LogoutException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.RefreshException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.UpdateUserException;
import de.uni_stuttgart.riot.usermanagement.security.AuthenticationUtil;

/**
 * Contains all logic regarding the authorization process.
 * 
 * @author Niklas Schnabel
 *
 */
public class AuthenticationLogic {

    // How long is the bearer token valid. Time in ms. Current value: 2h (=min * sec * ms)
    private static final int VALID_TOKEN_TIME_IN_MS = 120 * 60 * 1000;

    // If the token already exists in the db, how often should be tried to generate a unique token
    private static final int TOKEN_GENERATION_MAX_RETRIES = 20;

    /**
     * How often can a user enter a wrong password for a single user name?
     */
    public static final int MAX_LOGIN_RETRIES = 5;

    private DAO<Token> dao;

    /**
     * Constructor.
     */
    public AuthenticationLogic() {
        try {
            dao = new TokenSqlQueryDAO(ConnectionMgr.openConnection(),false);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
		}
    }

    /**
     * Generate one bearer and one refresh token.
     * 
     * @param username
     *            User name of the user. Is used for authentication.
     * @param password
     *            Password of the user. Is used for authentication.
     * @throws LoginException
     *             Thrown if any error happens
     * 
     * @return A response containing the bearer and refresh token
     */
    public Token login(String username, String password) throws LoginException {
        try {
            Subject subject = SecurityUtils.getSubject();
            UserLogic ul = new UserLogic();

            UMUser user = ul.getUser(username);

            if (user.getLoginAttemptCount() > MAX_LOGIN_RETRIES) {
                throw new LoginException("Password was too many times wrong. Please change the password.");
            }

            String hashedPassword = AuthenticationUtil.getHashedString(password, user.getPasswordSalt(), user.getHashIterations());

            try {
                subject.login(new UsernamePasswordToken(username, hashedPassword));
            } catch (AuthenticationException e) {
                incLoginRetryCount(ul, user);
                throw new LoginException("Wrong Username/Password", e);
            }

            if (subject.isAuthenticated()) {
                try {
                    // reset user attempt counter, so the user has again the maximum number of login retries
                    user.setLoginAttemptCount(0);
                    ul.updateUser(user, null);

                    Token token = generateAndSaveTokens(user.getId());
         
                    // get all roles of the user
                    Collection<Role> roles = ul.getAllRolesFromUser(user.getId());
                    DAO<TokenRole> tokenRoleDao = new TokenRoleSqlQueryDAO(ConnectionMgr.openConnection(),false);

                    // assign the token the same roles as the user has
                    for (Role role : roles) {
                        tokenRoleDao.insert(new TokenRole(token.getId(), role.getId()));
                    }

                    return token;
                } catch (Exception e) {
                    throw new LoginException(e);
                }
            } else {
                incLoginRetryCount(ul, user);
                throw new LoginException("Wrong Username/Password");
            }
        } catch (Exception e) {
            throw new LoginException(e);
        }
    }

    /**
     * Generate a new set of bearer and refresh token from a given refresh token.
     * 
     * @param providedRefreshToken
     *            The refresh token used for generating the new tokens
     * @throws LoginException
     *             Thrown if any error happens
     * 
     * @return A response containing the bearer and refresh token
     */
    public Token refreshToken(String providedRefreshToken) throws RefreshException {

        try {
            // find the token belonging to the given refresh token
            Token token = dao.findByUniqueField(new SearchParameter(SearchFields.REFRESHTOKEN, providedRefreshToken));

            // test, if token is valid
            if (token != null && token.isValid()) {

                DAO<TokenRole> tokenRoleDao = new TokenRoleSqlQueryDAO(ConnectionMgr.openConnection(),false);

                // generate a new token and save it in the db
                Token newToken = generateAndSaveTokens(token.getUserID());

                // get all connections between the given token and roles
                Collection<SearchParameter> searchParameter = new ArrayList<SearchParameter>();
                searchParameter.add(new SearchParameter(SearchFields.TOKENID, token.getId()));
                Collection<TokenRole> tokenRoles = tokenRoleDao.findBy(searchParameter, false);

                for (TokenRole tokenRole : tokenRoles) {
                    tokenRoleDao.insert(new TokenRole(newToken.getId(), tokenRole.getRoleID()));
                }

                // invalidate old token
                token.setValid(false);
                dao.update(token);

                return newToken;
            } else {
                throw new RefreshException("The provided refresh token is not valid");
            }
        } catch (Exception e) {
            throw new RefreshException(e);
        }
    }

    /**
     * Log an user out be invalidating the bearer and refresh token.
     * 
     * @param currentBearerToken
     *            The current bearer token used to authenticate the user. Will no longer be valid after calling this method.
     * @throws Thrown
     *             if any error happens
     */
    public void logout(String currentBearerToken) throws LogoutException {
        try {
            Token token = dao.findByUniqueField(new SearchParameter(SearchFields.TOKENVALUE, currentBearerToken));
            token.setValid(false);
            dao.update(token);
        } catch (Exception e) {
            throw new LogoutException(e);
        }
    }

    /**
     * Generate new tokens and save them in the database.
     * 
     * @param userId
     *            The id of the user for whom the tokens should be generated
     * @return The generated token
     * @throws LoginException
     */
    private Token generateAndSaveTokens(Long userId) throws LoginException {

        Token token = null;
        int retries = TOKEN_GENERATION_MAX_RETRIES;
        Exception lastException = null;

        Timestamp issueTime = new Timestamp(System.currentTimeMillis()-1000000);//FIXME use git - master 
        Timestamp expirationTime = new Timestamp(System.currentTimeMillis() + VALID_TOKEN_TIME_IN_MS);

        do {
            // generate new tokens
            String authToken = AuthenticationUtil.generateAccessToken();
            String refreshToken = AuthenticationUtil.generateAccessToken();

            token = new Token(userId, authToken, refreshToken, issueTime, expirationTime, true);

            try {
                dao.insert(token);
            } catch (DatasourceInsertException e) {
                retries--;
                token = null;
                lastException = e;
            }
        } while (token == null && retries > 0);

        // throw exception if token generation was not successfull
        if (token == null) {
            throw new LoginException(lastException);
        }

        return token;
    }

    /**
     * @param ul
     * @param user
     * @throws UpdateUserException
     */
    private void incLoginRetryCount(UserLogic ul, UMUser user) throws UpdateUserException {
        user.setLoginAttemptCount(user.getLoginAttemptCount() + 1);
        ul.updateUser(user, null);
    }
}
