package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.usermanagement.data.storable.Token;

/**
 * {@link ObjectBuilder} for {@link Token}. Used in {@link TokenSqlQueryDAO}.
 * @author Jonas Tangermann
 *
 */
public class TokenObjectBuilder implements ObjectBuilder<Token> {

    @Override
    public Token build(ResultSet resultSet) throws SQLException {
        return new Token(resultSet.getLong(1),
                resultSet.getLong(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getTimestamp(5),
                resultSet.getTimestamp(6),
                resultSet.getBoolean(7));
    }

}
