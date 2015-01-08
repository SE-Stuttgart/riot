package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.DataSource;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
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
        this.connection = DatasourceUtil.getDataSource().getConnection();
    }
    
    public SqlTransaction(DataSource ds) throws SQLException {
        this.connection = ds.getConnection();
    }

    public DAO<UMUser> getUserDao() throws SQLException {
        return new UserSqlQueryDao(connection);
    }
    
    public DAO<Token> getTokenDao() throws SQLException {
        return new TokenSqlQueryDAO(connection);
    }
    
    public DAO<Role> getRoleDao() throws SQLException {
        return new RoleSqlQueryDAO(connection);
    }
    
    public DAO<RolePermission> getRolePermissionDao() throws SQLException {
        return new RolePermissionSqlQueryDAO(connection);
    }
    
    public DAO<Permission> getPermissionDao() throws SQLException {
        return new PermissionSqlQueryDAO(connection);
    }
    
    public DAO<TokenRole> getTokenRoleDao() throws SQLException {
        return new TokenRoleSqlQueryDAO(connection);
    }

    public void commit() throws SQLException{
        try {
            this.connection.commit();
        } finally {
            this.connection.close();
        }
    }
    
    public void rollback() throws SQLException{
        try {
            this.connection.rollback();
        } finally {
            this.connection.close();
        }
    }
}
