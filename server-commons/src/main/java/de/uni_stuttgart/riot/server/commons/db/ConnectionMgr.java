package de.uni_stuttgart.riot.server.commons.db;

import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 * A util class for quick access to connection instances.
 */
public abstract class ConnectionMgr {

    /**
     * JNDI name of the jdbc resource as configured on the jee server.
     */
    private static final String DATASOURCE_NAME = "jdbc/riot";

    private static Sql2o sql2o;

    /**
     * Gets the DataSource. Could probably be injected with CDI.
     * 
     * @return DataSource
     * @throws NamingException
     *             if the resource is not configured in the jee server.
     */
    private static DataSource getDataSource() throws NamingException {
        InitialContext ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup(DATASOURCE_NAME);
        return ds;
    }

    /**
     * Returns a cached sql2o instance. Use {@link #openConnection()} in a try-with-resources block in most cases.
     * 
     * @return an sql2o instance with an assigned DataSource
     * @throws SQLException
     *             if a DB error occurs
     */
    public static Sql2o getSql2o() throws SQLException {
        if (sql2o == null) {
            try {
                sql2o = new Sql2o(getDataSource());
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
        return sql2o;
    }

    /**
     * Opens an sql2o connection and returns it. Use {@link #beginTransaction()} if transactions are needed.
     * 
     * @return An open sql2o connection
     * @throws SQLException
     *             if a DB error occurs
     */
    public static Connection openConnection() throws SQLException {
        return getSql2o().open();
    }

    /**
     * Begins a transaction. A commit or rollback must be called to close the transaction.
     * 
     * @return connection instance to use in the transaction.
     * @throws SQLException
     *             if a DB error occurs
     */
    public static Connection beginTransaction() throws SQLException {
        return getSql2o().beginTransaction();
    }

}
