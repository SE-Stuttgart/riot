package de.uni_stuttgart.riot.usermanagement.logic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NamingException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchFields;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.GenerateTokenException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.authentication.LogoutException;
import de.uni_stuttgart.riot.usermanagement.security.TokenUtil;
import de.uni_stuttgart.riot.usermanagement.service.response.AuthenticationResponse;

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

    private DAO<Token> dao;

    /**
     * Constructor
     */
    public AuthenticationLogic() {
        try {
            dao = new TokenSqlQueryDAO(DatasourceUtil.getDataSource());
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate one bearer and one refresh token.
     * 
     * @param username
     *            User name of the user. Is used for authentication.
     * @param password
     *            Password of the user. Is used for authentication.
     * @throws GenerateTokenException
     *             Thrown if any error happens
     * 
     * @return A response containing the bearer and refresh token
     */
    public AuthenticationResponse generateTokens(String username, String password) throws GenerateTokenException {
        try {
            Subject subject = SecurityUtils.getSubject();

            subject.login(new UsernamePasswordToken(username, password));

            if (subject.isAuthenticated()) {
                try {
                    UserLogic ul = new UserLogic();

                    // get the user with the given user name
                    User user = ul.getUser(username);

                    Token token = generateAndSaveTokens(user.getId());

                    // get all roles of the user
                    Collection<Role> roles = ul.getAllRolesFromUser(user.getId());
                    DAO<TokenRole> tokenRoleDao = new TokenRoleSqlQueryDAO(DatasourceUtil.getDataSource());

                    // assign the token the same roles as the user has
                    for (Role role : roles) {
                        tokenRoleDao.insert(new TokenRole(token.getId(), role.getId()));
                    }

                    return new AuthenticationResponse(token.getTokenValue(), token.getRefreshtokenValue());
                } catch (Exception e) {
                    throw new GenerateTokenException(e);
                }

            } else {
                throw new GenerateTokenException("Wrong Username/Password");
            }
        } catch (Exception e) {
            throw new GenerateTokenException(e);
        }
    }

    /**
     * Generate a new set of bearer and refresh token from a given refresh token.
     * 
     * @param providedRefreshToken
     *            The refresh token used for generating the new tokens
     * @throws GenerateTokenException
     *             Thrown if any error happens
     * 
     * @return A response containing the bearer and refresh token
     */
    public AuthenticationResponse refreshToken(String providedRefreshToken) throws GenerateTokenException {

        try {
            // find the token belonging to the given refresh token
            Token token = dao.findByUniqueField(new SearchParameter(SearchFields.REFRESHTOKEN, providedRefreshToken));

            // test, if token is valid
            if (token != null && token.isValid()) {

                DAO<TokenRole> tokenRoleDao = new TokenRoleSqlQueryDAO(DatasourceUtil.getDataSource());

                // get all connections between the given token and roles
                Collection<SearchParameter> searchParameter = new ArrayList<SearchParameter>();
                searchParameter.add(new SearchParameter(SearchFields.TOKENID, token.getId()));
                Collection<TokenRole> tokenRoles = tokenRoleDao.findBy(searchParameter, false);

                for (TokenRole tokenRole : tokenRoles) {
                    tokenRoleDao.insert(new TokenRole(token.getId(), tokenRole.getRoleID()));
                }

                // invalidate old token
                token.setValid(false);
                dao.update(token);

                // generate a new token and save it in the db
                Token newToken = generateAndSaveTokens(token.getUserID());

                return new AuthenticationResponse(newToken.getTokenValue(), newToken.getRefreshtokenValue());
            } else {
                throw new GenerateTokenException("The provided refresh token is not valid");
            }
        } catch (Exception e) {
            throw new GenerateTokenException(e);
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
     * @throws GenerateTokenException
     */
    private Token generateAndSaveTokens(Long userId) throws GenerateTokenException {

        Token token = null;
        int retries = TOKEN_GENERATION_MAX_RETRIES;
        Exception lastException = null;

        Timestamp issueTime = new Timestamp(System.currentTimeMillis());
        Timestamp expirationTime = new Timestamp(System.currentTimeMillis() + VALID_TOKEN_TIME_IN_MS);

        do {
            // generate new tokens
            String authToken = TokenUtil.generateToken();
            String refreshToken = TokenUtil.generateToken();

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
            throw new GenerateTokenException(lastException);
        }

        return token;
    }
}
