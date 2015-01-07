package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;

/**
 * Data access class for all {@link RolePermission} objects.
 * @author Jonas Tangermann
 *
 */
public class RolePermissionSqlQueryDAO extends SqlQueryDAO<RolePermission> {


    public RolePermissionSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection,transaction);
    }
    
    @Override
	protected Class<RolePermission> getMyClazz() {
		return RolePermission.class;
	}
}
