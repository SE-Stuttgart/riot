package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.Role;

public class RoleSqlQueryDAO extends SqlQueryDAO<Role>{

	public RoleSqlQueryDAO(DataSource ds) {
		super(ds, new RoleQueryBuilder(), new RoleObjectBuilder());
	}

}
