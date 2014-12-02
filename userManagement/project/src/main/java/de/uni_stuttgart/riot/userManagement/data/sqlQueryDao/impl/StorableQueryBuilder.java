package de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.userManagement.data.storable.Storable;

public class StorableQueryBuilder {

    public PreparedStatement buildDelete(Storable t, Connection connection, String query) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setLong(1, t.getId());
        return stmt;
    }

    public PreparedStatement buildFindByID(Long id, Connection connection, String query) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setLong(1, id);
        return stmt;
    }

    public PreparedStatement buildFindBySearchParam(Collection<SearchParameter> params, Connection connection, String query, boolean or) throws SQLException {
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

    public PreparedStatement buildFindAll(Connection connection, String findAllQuery) throws SQLException {
        return connection.prepareStatement(findAllQuery);
    }
}
