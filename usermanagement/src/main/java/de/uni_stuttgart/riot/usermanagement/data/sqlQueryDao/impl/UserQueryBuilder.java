package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;

/**
 * {@link QueryBuilder} for {@link UMUser} sql querys. Is used in {@link UserSqlQueryDao}.
 * 
 * @author Jonas Tangermann
 *
 */
public class UserQueryBuilder extends StorableQueryBuilder implements QueryBuilder<UMUser> {

    private static final String DELETE_QUERY = "DELETE FROM users WHERE users.userID = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(username, password, password_salt, hash_iterations, loginAttemptCount) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET username=?, password=?, password_salt=?, hash_iterations=?, loginAttemptCount=? WHERE userID = ?;";
    private static final String FIND_ID_QUERY = "SELECT userid, username, password, password_salt, hash_iterations, loginAttemptCount FROM users WHERE userID = ?;";
    private static final String FIND_PARAM_QUERY = "SELECT userid, username, password, password_salt, hash_iterations, loginAttemptCount FROM users ";
    private static final String FIND_ALL_QUERY = "SELECT userid, username, password, password_salt, hash_iterations, loginAttemptCount FROM users";

    @Override
    public PreparedStatement buildDelete(UMUser t, Connection connection) throws SQLException {
        return super.buildDelete(t, connection, DELETE_QUERY);
    }

    @Override
    public PreparedStatement buildInsert(UMUser t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, t.getUsername());
        stmt.setString(2, t.getHashedPassword());
        stmt.setString(3, t.getPasswordSalt());
        stmt.setInt(4, t.getHashIterations());
        stmt.setInt(5, t.getLoginAttemptCount());
        return stmt;
    }

    @Override
    public PreparedStatement buildUpdate(UMUser t, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
        stmt.setString(1, t.getUsername());
        stmt.setString(2, t.getHashedPassword());
        stmt.setString(3, t.getPasswordSalt());
        stmt.setInt(4, t.getHashIterations());
        stmt.setInt(5, t.getLoginAttemptCount());
        stmt.setLong(6, t.getId());
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
