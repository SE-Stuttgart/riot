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
     * Creates a sql2o instance. Use {@link #openConnection()} in a try-with-resources block if you do not need to perform transactions.
     * 
     * @return an sql2o instance with an assigned DataSource
     * @throws NamingException
     *             if the resource is not configured in the jee server.
     * @throws SQLException
     *             if a DB error occurs
     */
    public static Sql2o getSql2o() throws NamingException, SQLException {
        DataSource ds = getDataSource();
        return new Sql2o(ds);
    }

    /**
     * Opens an sql2o connection and returns it. Use {@link #getSql2o()} if transactions are needed.
     * 
     * @return An open sql2o connection
     * @throws NamingException
     *             if the resource is not configured in the jee server.
     * @throws SQLException
     *             if a DB error occurs
     */
    public static Connection openConnection() throws NamingException, SQLException {
        return getSql2o().open();
    }

	public static Connection beginTransaction() throws NamingException, SQLException {
        return getSql2o().beginTransaction();
	}

}
