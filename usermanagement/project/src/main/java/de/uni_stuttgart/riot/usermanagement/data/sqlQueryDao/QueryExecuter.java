package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.sql.Connection;

public abstract class QueryExecuter {

    private final Connection connection;

    public QueryExecuter(Connection connection) {
        this.connection = connection;
    }
    public Connection getConnection() {
        return connection;
    }
}
