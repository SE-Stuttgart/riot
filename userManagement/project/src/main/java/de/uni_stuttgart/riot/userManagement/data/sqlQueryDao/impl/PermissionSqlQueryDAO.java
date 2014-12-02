package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.userManagement.data.storable.Permission;

public class PermissionSqlQueryDAO extends SqlQueryDAO<Permission> {

    public PermissionSqlQueryDAO(DataSource ds) {
        super(ds, new PermissionQueryBuilder(), new PermissionObjectBuilder());
    }

}
