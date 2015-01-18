package de.uni_stuttgart.riot.usermanagement.logic;

import java.sql.SQLException;

import javax.naming.NamingException;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.logic.exception.token.GetTokenException;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class TokenLogic {

    private DAO<Token> dao;

    /**
     * Constructor.
     */
    public TokenLogic() {
        try {
            dao = new TokenSqlQueryDAO(ConnectionMgr.openConnection(), false);
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Token getToken(String token) throws GetTokenException {

        try {
            System.out.println(token);
            // search token by token value
            return dao.findByUniqueField(new SearchParameter(SearchFields.TOKENVALUE, token));
        } catch (Exception e) {
            throw new GetTokenException(e);
        }
    }

}
