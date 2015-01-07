package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.SQLException;
import java.util.Collection;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.server.commons.db.SQLQueryUtil;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.usermanagement.data.storable.Storable;

public class QueryBuilderImpl implements QueryBuilder{

	@Override
	public Query buildDelete(String tableName, Storable t, Connection connection) throws SQLException {
		return connection.createQuery(SQLQueryUtil.buildDelete(tableName)).bind(t);
	}

	@Override
	public Query buildInsert(String tableName, Storable t, Connection connection) throws SQLException {
		return connection.createQuery(SQLQueryUtil.buildInsertStatement(t.getClass(), tableName)).bind(t);
	}

	@Override
	public Query buildUpdate(String tableName, Storable t, Connection connection) throws SQLException {
		return connection.createQuery(SQLQueryUtil.buildUpdateStatement(t.getClass(), tableName)).bind(t);
	}

	@Override
	public Query buildFindByID(String tableName, Long id, Connection connection) throws SQLException {
		return connection.createQuery(SQLQueryUtil.buildGetById(tableName)).addParameter("id", id);
	}

	@Override
	public Query buildFindBySearchParam(String tableName, Collection<SearchParameter> params, Connection connection, boolean or) throws SQLException {
		return connection.createQuery(SQLQueryUtil.buildFindByParam(params, or, tableName));
	}

	@Override
	public Query buildFindAll(String tableName, Connection connection) throws SQLException {
		return connection.createQuery(SQLQueryUtil.buildGetAll(tableName));
	}

}
