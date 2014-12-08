package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;

public class TokenRoleObjectBuilder implements ObjectBuilder<TokenRole> {

    @Override
    public TokenRole build(ResultSet resultSet) throws SQLException {
        return new TokenRole(resultSet.getLong(1), resultSet.getLong(2), resultSet.getLong(3));
    }

}