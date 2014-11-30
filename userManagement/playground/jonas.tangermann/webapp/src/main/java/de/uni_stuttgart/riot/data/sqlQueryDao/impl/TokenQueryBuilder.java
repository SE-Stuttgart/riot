package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.storable.Token;

public class TokenQueryBuilder extends StorableQueryBuilder implements QueryBuilder<Token> {

	private static final String DELETE_QUERY= "DELETE FROM tokens WHERE tokens.tokenID = ?";
	private static final String INSERT_QUERY= "INSERT INTO tokens(tokenID, userID,tokenValue,issueDate,expirationdate)VALUES (?,?,?,?,?)";
	private static final String UPDATE_QUERY= "UPDATE tokens SET userid=?, tokenvalue=?, issuedate=?, expirationdate=? WHERE tokenID=?";
	private static final String FIND_ID_QUERY= "SELECT tokenid, userid, tokenvalue, issuedate, expirationdate FROM tokens WHERE tokenID = ?;";
	private static final String FIND_PARAM_QUERY= "SELECT tokenid, userid, tokenvalue, issuedate, expirationdate FROM tokens WHERE tokenValue in (?)"; 
	
	@Override
	public PreparedStatement buildDelete(Token t, Connection connection)
			throws SQLException {
		return super.buildDelete(t, connection, DELETE_QUERY);
	}

	@Override
	public PreparedStatement buildInsert(Token t, Connection connection)
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY);
		stmt.setLong(1, t.getID());
		stmt.setLong(2, t.getUserID());
		stmt.setString(3, t.getTokenValue());
		stmt.setTimestamp(4, t.getIssueTime());
		stmt.setTimestamp(5, t.getExpirationTime());
		return stmt;
	}

	@Override
	public PreparedStatement buildUpdate(Token t, Connection connection)
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
		stmt.setLong(1, t.getUserID());
		stmt.setString(2, t.getTokenValue());
		stmt.setTimestamp(3, t.getIssueTime());
		stmt.setTimestamp(4, t.getExpirationTime());
		stmt.setLong(5, t.getID());
		return stmt;
	}

	@Override
	public PreparedStatement buildFindByID(Long id, Connection connection)
			throws SQLException {
		return super.buildFindByID(id, connection, FIND_ID_QUERY);
	}

	@Override
	public PreparedStatement buildFindBySearchParam(Collection<String> params,
			Connection connection) throws SQLException {
		return super.buildFindBySearchParam(params, connection, FIND_PARAM_QUERY);
	}

}
