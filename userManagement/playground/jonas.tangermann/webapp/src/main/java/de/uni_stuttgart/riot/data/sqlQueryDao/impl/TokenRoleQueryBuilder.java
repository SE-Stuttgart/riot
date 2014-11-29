package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.data.storable.TokenRole;

public class TokenRoleQueryBuilder extends StorableQueryBuilder implements QueryBuilder<TokenRole> {

	private static final String DELETE_QUERY= "DELETE FROM tokens_roles WHERE tokens_roles.tokenroleID = ?";
	private static final String INSERT_QUERY= "INSERT INTO tokens_roles(tokenroleID,tokenID,roleID)VALUES (?, ?, ?)";
	private static final String UPDATE_QUERY= "UPDATE tokens_roles SET tokenID=?, roleID=? WHERE tokenroleID = ?;";
	private static final String FIND_ID_QUERY= "SELECT tokenroleID,tokenID,toleID FROM tokens_roles WHERE tokenroleID = ?;";
	private static final String FIND_PARAM_QUERY= "SELECT tokenroleID,tokenID,toleID FROM tokens_roles WHERE roleID or tokenID in (?)"; 
	
	@Override
	public PreparedStatement buildDelete(TokenRole t, Connection connection)
			throws SQLException {
		return super.buildDelete(t, connection, DELETE_QUERY);
	}

	@Override
	public PreparedStatement buildInsert(TokenRole t, Connection connection)
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(INSERT_QUERY);
		stmt.setLong(1, t.getID());
		stmt.setLong(2, t.getTokenID());
		stmt.setLong(3, t.getRoleID());
		return stmt;
	}

	@Override
	public PreparedStatement buildUpdate(TokenRole t, Connection connection)
			throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(UPDATE_QUERY);
		stmt.setLong(1, t.getTokenID());
		stmt.setLong(2, t.getRoleID());
		stmt.setLong(3, t.getID());
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
