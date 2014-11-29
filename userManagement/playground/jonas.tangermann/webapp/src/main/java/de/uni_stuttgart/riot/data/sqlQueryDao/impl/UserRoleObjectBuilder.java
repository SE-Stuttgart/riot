package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.data.storable.UserRole;

public class UserRoleObjectBuilder implements ObjectBuilder<UserRole> {

	@Override
	public UserRole build(ResultSet resultSet) throws SQLException {
		return new UserRole(resultSet.getLong(1), resultSet.getLong(2), resultSet.getLong(3));
	}

}
