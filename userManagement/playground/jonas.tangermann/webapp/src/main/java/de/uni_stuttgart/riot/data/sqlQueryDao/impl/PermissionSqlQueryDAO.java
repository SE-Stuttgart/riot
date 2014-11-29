package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlQueryDao.ObjectBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.Permission;

public class PermissionSqlQueryDAO extends SqlQueryDAO<Permission>{

	public PermissionSqlQueryDAO(DataSource ds) {
		super(ds, new PermissionQueryBuilder(), new PermissionObjectBuilder());
	}

}
