package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;

/**
 * Data access class for all {@link Token} objects.
 * 
 * @author Jonas Tangermann
 */
public class TokenSqlQueryDAO extends SqlQueryDAO<Token> {

    @Override
    protected Class<Token> getMyClazz() {
        return Token.class;
    }

    /**
     * Retrieves the ID of the user that the given token belongs to.
     * 
     * @param token
     *            The String representation of the token.
     * @return The user ID.
     * @throws DatasourceFindException
     *             When querying the database fails.
     */
    public long getUserIdFromToken(String token) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = connection.createQuery("SELECT userID FROM tokens WHERE tokenValue = :tokenValue");
            stmt.addParameter("tokenValue", token);
            return stmt.executeScalar(Long.class);
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

}
