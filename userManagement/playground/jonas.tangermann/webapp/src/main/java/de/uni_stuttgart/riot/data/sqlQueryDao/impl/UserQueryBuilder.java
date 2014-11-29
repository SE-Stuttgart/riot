package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.storable.User;

public class UserQueryBuilder extends StorableQueryBuilder implements QueryBuilder<User> {

	private static final String DELETE_QUERY= "DELETE FROM users WHERE users.userID = ?";
	private static final String INSERT_QUERY= "INSERT INTO users(userid, username, pword, pword_salt)VALUES (?, ?, ?, ?)";
	private static final String UPDATE_QUERY= "UPDATE users SET username=?, pword=?, pword_salt=? WHERE userID = ?;";
	private static final String FIND_ID_QUERY= "SELECT userid, username, pword, pword_salt FROM users WHERE userID = ?;";
	private static final String FIND_PARAM_QUERY= "SELECT userid, username, pword, pword_salt FROM users WHERE username in (?)"; 
	
	@Override
	public PreparedStatement buildDelete(User t, Connection connection) throws SQLException {
		return super.buildDelete(t, connection, DELETE_QUERY);
	}

	@Override
	public PreparedStatement buildInsert(User t, Connection connection)throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY);
		stmt.setLong(1, t.getID());
		stmt.setString(2, t.getUsername());
		stmt.setString(3, t.getPassword());
		stmt.setString(4, t.getPasswordSalt());
		return stmt;
	}

	@Override
	public PreparedStatement buildUpdate(User t, Connection connection)throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
		stmt.setString(1, t.getUsername());
		stmt.setString(2, t.getPassword());
		stmt.setString(3, t.getPasswordSalt());
		stmt.setLong(4, t.getID());
		return stmt;
	}

	@Override
	public PreparedStatement buildFindByID(Long id, Connection connection)throws SQLException {
		return super.buildFindByID(id, connection, FIND_ID_QUERY);
	}

	@Override
	public PreparedStatement buildFindBySearchParam(Collection<String> params, Connection connection)throws SQLException {
		return super.buildFindBySearchParam(params, connection, FIND_PARAM_QUERY);
	}

}
