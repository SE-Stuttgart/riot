package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

/**
 * {@link ObjectBuilder} for {@link UMUser}. Used in {@link UserSqlQueryDao}
 * 
 * @author Jonas Tangermann
 *
 */
public class UserObjectBuilder implements ObjectBuilder<UMUser> {

    @Override
    public UMUser build(ResultSet resultSet) throws SQLException {
        return new UMUser(resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getInt(5));
    }

}
