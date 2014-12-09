package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;

/**
 * Data access class for all {@link RolePermission} objects.
 * @author Jonas Tangermann
 *
 */
public class RolePermissionSqlQueryDAO extends SqlQueryDAO<RolePermission> {

    public RolePermissionSqlQueryDAO(DataSource ds) {
        super(ds, new RolePermissionQueryBuilder(), new RolePermissionObjectBuilder());
    }

}
