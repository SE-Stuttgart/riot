package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlquerydao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.Permission;

public class PermissionSqlQueryDAO extends SqlQueryDAO<Permission> {

    public PermissionSqlQueryDAO(DataSource ds) {
        super(ds, new PermissionQueryBuilder(), new PermissionObjectBuilder());
    }

}
