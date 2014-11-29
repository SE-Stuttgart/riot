package de.uni_stuttgart.riot.data.sqlQueryDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import de.uni_stuttgart.riot.data.storable.Storable;

public interface QueryBuilder<T extends Storable> {
	
	public PreparedStatement buildDelete(T t, Connection connection)throws SQLException;
	
	public PreparedStatement buildInsert(T t, Connection connection)throws SQLException;

	public PreparedStatement buildUpdate(T t, Connection connection)throws SQLException;

	public PreparedStatement buildFindByID(Long id, Connection connection)throws SQLException;

	public PreparedStatement buildFindBySearchParam(Collection<String> params, Connection connection)throws SQLException;
}
