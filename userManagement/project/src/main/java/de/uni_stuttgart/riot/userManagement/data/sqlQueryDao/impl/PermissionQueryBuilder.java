package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.userManagement.data.storable.Permission;

public class PermissionQueryBuilder extends StorableQueryBuilder implements QueryBuilder<Permission> {

    private static final String DELETE_QUERY = "DELETE FROM permissions WHERE permissions.permissionID = ?";
    private static final String INSERT_QUERY = "INSERT INTO permissions(permissionID, permissionValue)VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE permissions SET permissionValue=? WHERE permissionID = ?;";
    private static final String FIND_ID_QUERY = "SELECT permissionID,permissionValue FROM permissions WHERE permissionID = ?;";
    private static final String FIND_PARAM_QUERY = "SELECT permissionID,permissionValue FROM permissions ";
    private static final String FIND_ALL_QUERY = "SELECT permissionID,permissionValue FROM permissions;";

    @Override
    public PreparedStatement buildDelete(Permission t, Connection connection) throws SQLException {
        return super.buildDelete(t, connection, DELETE_QUERY);
    }

    @Override
    public PreparedStatement buildInsert(Permission t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY);
        stmt.setLong(1, t.getId());
        stmt.setString(2, t.getPermissionValue());
        return stmt;
    }

    @Override
    public PreparedStatement buildUpdate(Permission t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
        stmt.setString(1, t.getPermissionValue());
        stmt.setLong(2, t.getId());
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
