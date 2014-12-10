package de.uni_stuttgart.riot.usermanagement.logic;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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

        Subject subject = SecurityUtils.getSubject();

        subject.login(new UsernamePasswordToken(username, password));

        if (subject.isAuthenticated()) {
            try {
                String authToken = TokenUtil.generateToken();
                String refreshToken = TokenUtil.generateToken();

                Token token = saveTokensInDB(authToken, refreshToken);

                UserLogic ul = new UserLogic();
                User user;

                user = ul.getUser(username);
                Collection<Role> roles = ul.getAllRolesFromUser(user.getId());

                DAO<TokenRole> tokenRoleDao = new TokenRoleSqlQueryDAO(DatasourceUtil.getDataSource());

                for (Role role : roles) {
                    tokenRoleDao.insert(new TokenRole(DatasourceUtil.nextId(), token.getId(), role.getId()));
                }

                return new AuthenticationResponse(authToken, refreshToken);
            } catch (Exception e) {
                throw new GenerateTokenException(e);
            }

        } else {
            throw new GenerateTokenException("Wrong Username/Password");
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
            // test if refresh token is valid (valid: exists in database and was not used yet)
            // FIXME boolean valid = dao.isRefreshTokenValid(providedRefreshToken);
            boolean valid = true;

            if (valid) {
                String authToken = TokenUtil.generateToken();
                String newRefreshToken = TokenUtil.generateToken();

                Token token = saveTokensInDB(authToken, newRefreshToken);

                Collection<SearchParameter> searchParameter = new ArrayList<SearchParameter>();
                searchParameter.add(new SearchParameter(SearchFields.REFRESHTOKEN, providedRefreshToken));

                Collection<Token> tokens = dao.findBy(searchParameter, false);
                Iterator<Token> i = tokens.iterator();

                if (i.hasNext()) {
                    DAO<TokenRole> tokenRoleDao = new TokenRoleSqlQueryDAO(DatasourceUtil.getDataSource());
                    searchParameter.clear();
                    searchParameter.add(new SearchParameter(SearchFields.TOKENID, i.next().getId()));

                    Collection<TokenRole> tokenRoles = tokenRoleDao.findBy(searchParameter, false);

                    for (TokenRole tokenRole : tokenRoles) {
                        tokenRoleDao.insert(new TokenRole(DatasourceUtil.nextId(), token.getId(), tokenRole.getRoleID()));
                    }
                }

                // invalidate old tokens
                // FIXME dao.invalidateTokensFromRefreshToken(providedRefreshToken);

                return new AuthenticationResponse(authToken, newRefreshToken);
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
            // invalidate tokens
            // FIXME dao.invalidateTokensFromBearerToken(currentBearerToken);
        } catch (Exception e) {
            throw new LogoutException(e);
        }
    }

    private Token saveTokensInDB(String authToken, String refreshToken) throws GenerateTokenException {
        Timestamp issueTime = new Timestamp(System.currentTimeMillis());
        Timestamp expirationTime = new Timestamp(System.currentTimeMillis() + VALID_TOKEN_TIME_IN_MS);

        // FIXME Ids of the user and the token
        Token token = new Token(1L, 1L, authToken, refreshToken, issueTime, expirationTime);
        try {
            dao.insert(token);
        } catch (DatasourceInsertException e) {
            throw new GenerateTokenException(e);
        }
        return token;
    }
}
