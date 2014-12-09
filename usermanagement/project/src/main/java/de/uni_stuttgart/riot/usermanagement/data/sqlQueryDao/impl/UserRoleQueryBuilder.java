package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;

/**
 * {@link QueryBuilder} for {@link UserRole}. Used in {@link UserRoleSqlQueryDAO}.
 * @author Jonas Tangermann
 *
 */
public class UserRoleQueryBuilder extends StorableQueryBuilder implements QueryBuilder<UserRole> {

    private static final String DELETE_QUERY = "DELETE FROM users_roles WHERE users_roles.userroleID = ?";
    private static final String INSERT_QUERY = "INSERT INTO users_roles(userroleID,userID,roleID)VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users_roles SET userID=?, roleID=? WHERE userroleID = ?;";
    private static final String FIND_ID_QUERY = "SELECT userroleID,userID,roleID FROM users_roles WHERE userroleID = ?;";
    private static final String FIND_PARAM_QUERY = "SELECT userroleID,userID,roleID FROM users_roles ";
    private static final String FIND_ALL_QUERY = "SELECT userroleID,userID,roleID FROM users_roles";

    @Override
    public PreparedStatement buildDelete(UserRole t, Connection connection) throws SQLException {
        return super.buildDelete(t, connection, DELETE_QUERY);
    }

    @Override
    public PreparedStatement buildInsert(UserRole t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY);
        stmt.setLong(1, t.getId());
        stmt.setLong(2, t.getUserID());
        stmt.setLong(3, t.getRoleID());
        return stmt;
    }

    @Override
    public PreparedStatement buildUpdate(UserRole t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
        stmt.setLong(1, t.getUserID());
        stmt.setLong(2, t.getRoleID());
        stmt.setLong(3, t.getId());
        return stmt;
    }

    @Override
    public PreparedStatement buildFindByID(Long id, Connection connection) throws SQLException {
        return super.buildFindByID(id, connection, FIND_ID_QUERY);
    }

    @Override
    public PreparedStatement buildFindBySearchParam(Collection<SearchParameter> params, Connection connection, boolean or) throws SQLException {
        return super.buildFindBySearchParam(params, connection, FIND_PARAM_QUERY, or);
    }

    @Override
    public PreparedStatement buildFindAll(Connection connection) throws SQLException {
        return super.buildFindAll(connection, FIND_ALL_QUERY);
    }

}
