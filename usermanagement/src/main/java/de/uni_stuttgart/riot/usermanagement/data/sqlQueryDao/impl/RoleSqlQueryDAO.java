package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;

/**
 * Data access class for all {@link Role} objects.
 * @author Jonas Tangermann
 *
 */
public class RoleSqlQueryDAO extends SqlQueryDAO<Role> {


    public RoleSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection,transaction);
    }

    @Override
	protected Class<Role> getMyClazz() {
		return Role.class;
	}
}
