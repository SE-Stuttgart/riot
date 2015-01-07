package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;

/**
 * Data access class for all {@link RolePermission} objects.
 * @author Jonas Tangermann
 *
 */
public class RolePermissionSqlQueryDAO extends SqlQueryDAO<RolePermission> {


    public RolePermissionSqlQueryDAO(Connection connection) throws SQLException {
        super(connection);
    }
    
    @Override
	protected Class<RolePermission> getMyClazz() {
		return RolePermission.class;
	}
}
