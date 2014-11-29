package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.uni_stuttgart.riot.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.data.storable.User;

public class UserObjectBuilder implements ObjectBuilder<User> {

	@Override
	public User build(ResultSet resultSet) throws SQLException {
		return new User(resultSet.getLong(1), resultSet.getString(2));
	}

}
