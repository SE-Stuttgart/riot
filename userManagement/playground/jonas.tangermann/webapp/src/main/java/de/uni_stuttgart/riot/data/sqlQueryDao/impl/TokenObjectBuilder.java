package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.data.sqlquerydao.ObjectBuilder;
import de.uni_stuttgart.riot.data.storable.Token;

public class TokenObjectBuilder implements ObjectBuilder<Token> {

    @Override
    public Token build(ResultSet resultSet) throws SQLException {
        return new Token(resultSet.getLong(1), resultSet.getLong(2), resultSet.getString(3), resultSet.getTimestamp(4), resultSet.getTimestamp(5));
    }

}
