package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
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
    public UserRoleSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    @Override
	protected Class<UserRole> getMyClazz() {
		return UserRole.class;
	}
}
