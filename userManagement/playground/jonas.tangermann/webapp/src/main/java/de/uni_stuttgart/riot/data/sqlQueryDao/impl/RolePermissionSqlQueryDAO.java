package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.sqlquerydao.SqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.RolePermission;

public class RolePermissionSqlQueryDAO extends SqlQueryDAO<RolePermission> {

    public RolePermissionSqlQueryDAO(DataSource ds) {
        super(ds, new RolePermissionQueryBuilder(), new RolePermissionObjectBuilder());
    }

}
