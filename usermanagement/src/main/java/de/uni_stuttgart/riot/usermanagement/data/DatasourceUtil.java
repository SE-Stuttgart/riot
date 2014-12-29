package de.uni_stuttgart.riot.usermanagement.data;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Helper for opening database connections.
 */
public final class DatasourceUtil {

    private static final String JNDI_PATH = "jdbc/riot";

    /**
     * No instantiation possible.
     */
    private DatasourceUtil() {
    }

    /**
     * Retrieves the JDBC data source.
     * 
     * @return The JDBC data source.
     * @throws NamingException
     *             If the JDBC data source cannot be found.
     */
    public static DataSource getDataSource() throws NamingException {
        InitialContext in;
        in = new InitialContext();
        return (DataSource) in.lookup(JNDI_PATH);
    }

}
