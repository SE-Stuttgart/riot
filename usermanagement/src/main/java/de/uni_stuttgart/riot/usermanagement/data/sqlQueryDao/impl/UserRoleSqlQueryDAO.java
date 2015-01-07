package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

/**
 * DAO for {@link UserRole}
 * @author Jonas Tangermann
 *
 */
public class UserRoleSqlQueryDAO extends SqlQueryDAO<UserRole> {

    /**
     * Constructor
     * @param ds The datasource to be used by the dao
     * @throws SQLException 
     */
    public UserRoleSqlQueryDAO(Connection connection) throws SQLException {
        super(connection);
    }

	@Override
	protected Class<UserRole> getMyClazz() {
		return UserRole.class;
	}
}
