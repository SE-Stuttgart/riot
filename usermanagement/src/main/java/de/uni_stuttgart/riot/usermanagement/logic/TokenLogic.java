package de.uni_stuttgart.riot.usermanagement.logic;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.logic.exception.token.GetTokenException;

/**
 * Logic class for tokens.
 */
public class TokenLogic {

    private DAO<Token> dao = new TokenSqlQueryDAO();

    /**
     * Retrieves a token by its string representation.
     * 
     * @param token
     *            The token string.
     * @return The token instance.
     * @throws GetTokenException
     *             When retrieving the token failed.
     */
    public Token getToken(String token) throws GetTokenException {
        try {
            return dao.findByUniqueField(new SearchParameter(SearchFields.TOKENVALUE, token));
        } catch (Exception e) {
            throw new GetTokenException(e);
        }
    }

}
