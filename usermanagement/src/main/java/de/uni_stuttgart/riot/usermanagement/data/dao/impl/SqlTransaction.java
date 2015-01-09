package de.uni_stuttgart.riot.usermanagement.data.dao.impl;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.TransactionInterface;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

public class SqlTransaction implements TransactionInterface {

    private final Connection connection;

    public SqlTransaction() throws SQLException, NamingException {
        this.connection = ConnectionMgr.beginTransaction();
    }

    public DAO<UMUser> getUserDao() throws SQLException {
        return new UserSqlQueryDao(connection, true);
    }

    public DAO<Token> getTokenDao() throws SQLException {
        return new TokenSqlQueryDAO(connection, true);
    }

    public DAO<Role> getRoleDao() throws SQLException {
        return new RoleSqlQueryDAO(connection, true);
    }

    public DAO<RolePermission> getRolePermissionDao() throws SQLException {
        return new RolePermissionSqlQueryDAO(connection, true);
    }

    public DAO<Permission> getPermissionDao() throws SQLException {
        return new PermissionSqlQueryDAO(connection, true);
    }

    public DAO<TokenRole> getTokenRoleDao() throws SQLException {
        return new TokenRoleSqlQueryDAO(connection, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.server.commons.db.TransactionInterface#commit()
     */
    @Override
    public void commit() throws SQLException {
        try {
            this.connection.commit();
        } finally {
            this.connection.close();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.server.commons.db.TransactionInterface#rollback()
     */
    @Override
    public void rollback() throws SQLException {
        try {
            this.connection.rollback();
        } finally {
            this.connection.close();
        }
    }
}
