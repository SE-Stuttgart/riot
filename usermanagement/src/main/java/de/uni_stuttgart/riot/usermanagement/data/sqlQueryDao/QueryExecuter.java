package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import org.sql2o.Connection;


public abstract class QueryExecuter {

    private final Connection connection;

    public QueryExecuter(Connection connection) {
        this.connection = connection;
    }
    public Connection getConnection() {
        return connection;
    }
}
