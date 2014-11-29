package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.UserRole;

public class UserRoleSqlQueryDAO extends SqlQueryDAO<UserRole>{

	public UserRoleSqlQueryDAO(DataSource ds) {
		super(ds, new UserRoleQueryBuilder(), new UserRoleObjectBuilder());
	}

}
