package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.userManagement.data.storable.Role;

public class RoleQueryBuilder extends StorableQueryBuilder implements QueryBuilder<Role> {

    private static final String DELETE_QUERY = "DELETE FROM roles WHERE roles.roleID = ?";
    private static final String INSERT_QUERY = "INSERT INTO roles(roleID, rolename)VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE roles SET rolename=? WHERE roleID = ?;";
    private static final String FIND_ID_QUERY = "SELECT roleID,rolename FROM roles WHERE roleID = ?;";
    private static final String FIND_PARAM_QUERY = "SELECT roleID,rolename FROM roles ";
    private static final String FIND_ALL_QUERY = "SELECT roleID,rolename FROM roles";

    @Override
    public PreparedStatement buildDelete(Role t, Connection connection) throws SQLException {
        return super.buildDelete(t, connection, DELETE_QUERY);
    }

    @Override
    public PreparedStatement buildInsert(Role t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY);
        stmt.setLong(1, t.getID());
        stmt.setString(2, t.getRoleName());
        return stmt;
    }

    @Override
    public PreparedStatement buildUpdate(Role t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
        stmt.setString(1, t.getRoleName());
        stmt.setLong(2, t.getID());
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
