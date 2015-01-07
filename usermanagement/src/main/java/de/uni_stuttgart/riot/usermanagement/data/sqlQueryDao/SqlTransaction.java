package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.TokenSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.RolePermission;
import de.uni_stuttgart.riot.usermanagement.data.storable.TokenRole;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

public class SqlTransaction {

    private final Connection connection;

    public SqlTransaction() throws SQLException, NamingException {
        this.connection = ConnectionMgr.beginTransaction();
    }

    public DAO<UMUser> getUserDao() throws SQLException {
        return new UserSqlQueryDao(connection,true);
    }

    public DAO<Token> getTokenDao() throws SQLException {
        return new TokenSqlQueryDAO(connection,true);
    }

    public DAO<Role> getRoleDao() throws SQLException {
        return new RoleSqlQueryDAO(connection,true);
    }

    public DAO<RolePermission> getRolePermissionDao() throws SQLException {
        return new RolePermissionSqlQueryDAO(connection,true);
    }

    public DAO<Permission> getPermissionDao() throws SQLException {
        return new PermissionSqlQueryDAO(connection,true);
    }

    public DAO<TokenRole> getTokenRoleDao() throws SQLException {
        return new TokenRoleSqlQueryDAO(connection,true);
    }

    public void commit() throws SQLException {
        try {
            this.connection.commit();
        } finally {
            this.connection.close();
        }
    }

    public void rollback() throws SQLException {
        try {
            this.connection.rollback();
        } finally {
            this.connection.close();
        }
    }
}
