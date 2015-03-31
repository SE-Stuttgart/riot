package de.uni_stuttgart.riot.usermanagement.logic;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;

/**
 * Logic class for tokens.
 */
public class TokenLogic {

    private TokenSqlQueryDAO dao = new TokenSqlQueryDAO();

    /**
     * Retrieves a token by its string representation.
     * 
     * @param token
     *            The token string.
     * @return The token instance.
     * @throws UserManagementException
     *             When retrieving the token failed.
     */
    public Token getToken(String token) throws UserManagementException {
        try {
            return dao.findByUniqueField(new SearchParameter(SearchFields.TOKENVALUE, token));
        } catch (Exception e) {
            throw new UserManagementException("Couldn't get token", e);
        }
    }

    /**
     * Retrieves the ID of the user that the given token belongs to.
     * 
     * @param token
     *            The String representation of the token.
     * @return The user ID.
     * @throws UserManagementException
     *             When the token could not be found.
     */
    public long getUserIdFromToken(String token) throws UserManagementException {
        try {
            return dao.getUserIdFromToken(token);
        } catch (DatasourceFindException e) {
            throw new UserManagementException("Couldn't get user from token", e);
        }
    }
}
