package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * DAO for {@link Permission}
 * 
 * @author Jonas Tangermann
 *
 */
public class PermissionSqlQueryDAO extends SqlQueryDAO<Permission> {

    public PermissionSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    protected Class<Permission> getMyClazz() {
        return Permission.class;
    }

}
