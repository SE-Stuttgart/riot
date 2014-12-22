package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.ObjectBuilder;

/**
 * {@link ObjectBuilder} for {@link User}. Used in {@link UserSqlQueryDao}
 * @author Jonas Tangermann
 *
 */
public class UserObjectBuilder implements ObjectBuilder<User> {

    @Override
    public User build(ResultSet resultSet) throws SQLException {
        return new User(resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
    }

}
