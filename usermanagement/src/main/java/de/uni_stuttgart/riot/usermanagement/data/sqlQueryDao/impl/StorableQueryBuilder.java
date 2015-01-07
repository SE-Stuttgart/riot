package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Storable;
import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.QueryBuilder;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

/**
 * Abstraction for all {@link QueryBuilder} to prevent code clones.
 * @author Jonas Tangermann
 *
 */
public abstract class StorableQueryBuilder {

    public Query buildDelete(Storable t, Connection connection, String query) throws SQLException {
        return connection.createQuery(query).addParameter("id", t.getId());
    }

    public Query buildFindByID(Long id, Connection connection, String query) throws SQLException {
        return connection.createQuery(query).addParameter("id",id);
    }

    public Query buildFindBySearchParam(Collection<SearchParameter> params, Connection connection, String query, boolean or) throws SQLException {
        query += this.getWherePart(params, or);
        PreparedStatement stmt = connection.prepareStatement(query);
        Iterator<SearchParameter> i = params.iterator();
        int count = 1;
        while (i.hasNext()) {
            stmt.setObject(count, i.next().getValue());
            count++;
        }
        return stmt;
    }

    public String getWherePart(Collection<SearchParameter> params, boolean or) {
        StringBuffer res = new StringBuffer();
        res.append("WHERE ");
        Iterator<SearchParameter> i = params.iterator();
        while (i.hasNext()) {
            res.append(i.next().getValueName());
            res.append(" = ? ");
            if (i.hasNext()) {
                if (or) {
                    res.append(" OR ");
                } else {
                    res.append(" AND ");
                }
            }
        }
        return res.toString();
    }

    public Query buildFindAll(Connection connection, String findAllQuery) throws SQLException {
        return connection.prepareStatement(findAllQuery);
    }
}
