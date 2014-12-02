package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.userManagement.data.storable.RolePermission;

public class RolePermissionQueryBuilder extends StorableQueryBuilder implements QueryBuilder<RolePermission> {

    private static final String DELETE_QUERY = "DELETE FROM roles_permissions WHERE roles_permissions.rolepermissionID = ?";
    private static final String INSERT_QUERY = "INSERT INTO roles_permissions(rolepermissionID,roleID,permissionID)VALUES (?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE roles_permissions SET roleID=?, permissionID=? WHERE rolepermissionID = ?;";
    private static final String FIND_ID_QUERY = "SELECT rolepermissionID,roleID,permissionID FROM roles_permissions WHERE rolepermissionID = ?;";
    private static final String FIND_PARAM_QUERY = "SELECT rolepermissionID,roleID,permissionID FROM roles_permissions ";
    private static final String FIND_ALL_QUERY = "SELECT rolepermissionID,roleID,permissionID FROM roles_permissions";

    @Override
    public PreparedStatement buildDelete(RolePermission t, Connection connection) throws SQLException {
        return super.buildDelete(t, connection, DELETE_QUERY);
    }

    @Override
    public PreparedStatement buildInsert(RolePermission t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY);
        stmt.setLong(1, t.getId());
        stmt.setLong(2, t.getRoleID());
        stmt.setLong(3, t.getPermissionID());
        return stmt;
    }

    @Override
    public PreparedStatement buildUpdate(RolePermission t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
        stmt.setLong(1, t.getRoleID());
        stmt.setLong(2, t.getPermissionID());
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
