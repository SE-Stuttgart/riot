package de.uni_stuttgart.riot.server.commons.db;

import java.sql.SQLException;
import java.util.Collection;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * This class is used to create the sql2o queries for every operation in {@link DAO}. <br>
 * It is used every {@link SqlQueryDAO}
 */
public class QueryBuilderImpl implements QueryBuilder {

    @Override
    public Query buildDelete(String tableName, Storable t, Connection connection) throws SQLException {
        return connection.createQuery(SQLQueryUtil.buildDelete(tableName)).addParameter("id", t.getId());
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
        Query q = connection.createQuery(SQLQueryUtil.buildFindByParam(params, or, tableName));
        int x = 0;
        for (SearchParameter searchParameter : params) {
            q.addParameter(searchParameter.getValueName() + x, searchParameter.getValue());
            x = x + 1;
        }
        return q;
    }

    @Override
    public Query buildFindAll(String tableName, Connection connection) throws SQLException {
        return connection.createQuery(SQLQueryUtil.buildGetAll(tableName));
    }

    @Override
    public Query buildFindWithPagination(String tableName, Connection connection, int offset, int limit) throws SQLException {
        return connection.createQuery(SQLQueryUtil.buildFindWithPagination(tableName)).addParameter("offset", offset).addParameter("limit", limit);
    }
}
