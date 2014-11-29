package de.uni_stuttgart.riot.data.sqlQueryDao.impl;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.data.storable.Storable;
import de.uni_stuttgart.riot.data.storable.User;

public class StorableQueryBuilder {
	
	public PreparedStatement buildDelete(Storable t, Connection connection,String query) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setLong(1, t.getID());
		return stmt;
	}
	
	public PreparedStatement buildFindByID(Long id, Connection connection,String query)throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setLong(1, id);
		return stmt;
	}
	
	public PreparedStatement buildFindBySearchParam(Collection<String> params,
			Connection connection, String query) throws SQLException {
		PreparedStatement stmt = connection.prepareStatement(query);
		Array array = connection.createArrayOf("VARCHAR", params.toArray());
		stmt.setArray(1, array);
		return stmt;
	}
}
