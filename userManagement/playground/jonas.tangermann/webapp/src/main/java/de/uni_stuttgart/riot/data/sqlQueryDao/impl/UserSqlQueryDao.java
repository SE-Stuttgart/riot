package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.User;

public class UserSqlQueryDao extends SqlQueryDAO<User> {

	public UserSqlQueryDao(DataSource ds) {
		super(ds, new UserQueryBuilder(), new UserObjectBuilder());
	}

}
