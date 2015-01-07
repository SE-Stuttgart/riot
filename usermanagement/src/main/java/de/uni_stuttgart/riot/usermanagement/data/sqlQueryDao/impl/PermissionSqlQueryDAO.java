package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Permission;

/**
 * DAO for {@link Permission}
 * @author Jonas Tangermann
 *
 */
public class PermissionSqlQueryDAO extends SqlQueryDAO<Permission> {

    public PermissionSqlQueryDAO(Connection connection) throws SQLException {
        super(connection);
    }

    protected Class<Permission> getMyClazz(){
    	return Permission.class;
    }

    
}
