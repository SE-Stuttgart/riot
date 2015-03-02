package de.uni_stuttgart.riot.commons.test;

import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.h2.engine.Mode;
import org.h2.engine.Session;
import org.h2.engine.SessionInterface;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.rules.TemporaryFolder;

import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;

/**
 * Provides a temporary (initially empty) H2 database, also through JNDI.
 */
public class H2DatabaseRule extends JNDIRule {

    private final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DataSource dataSource;

    @Override
    protected void before() throws Throwable { // NOCS
        super.before();

        // Create H2 database
        temporaryFolder.create();
        File tempFile = temporaryFolder.newFile();
        JdbcDataSource ds = new JdbcDataSource();

        ds.setURL("jdbc:h2:" + tempFile.getAbsolutePath());
        ds.setUser("sa");
        ds.setUser("sa");
        dataSource = new DataSourceWrapper(ds);

        // Register the data source with JNDI
        registerJNDI("jdbc/riot", dataSource);

        // Make sure that ConnectionMgr uses the new connection
        Field sql2oField = ConnectionMgr.class.getDeclaredField("sql2o");
        sql2oField.setAccessible(true);
        sql2oField.set(null, null);

    }

    @Override
    protected void after() {
        super.after();
        temporaryFolder.delete();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Dirty hack. We need to modify the created connections for the tests to allow for using MySQL specific features. Note: Changing the H2
     * Mode to MySQL is not sufficient, since this mode will replace NULL values with empty values when inserting them on NON-NULL columns,
     * but we need the exception to be thrown in this case.
     * 
     * @author Philipp Keck
     */
    private static class DataSourceWrapper implements DataSource {

        private final DataSource wrapped;

        /**
         * Creates a new wrapper.
         * 
         * @param wrapped
         *            The wrapped data source.
         */
        public DataSourceWrapper(DataSource wrapped) {
            this.wrapped = wrapped;
        }

        /**
         * Activates {@link Mode#onDuplicateKeyUpdate} for the created connections.
         * 
         * @param connection
         *            The connection.
         * @return The modified connection.
         */
        private Connection modifyConnection(Connection connection) {
            if (connection instanceof JdbcConnection) {
                SessionInterface session = ((JdbcConnection) connection).getSession();
                ((Session) session).getDatabase().getMode().onDuplicateKeyUpdate = true;
            }
            return connection;
        }

        @Override
        public Connection getConnection() throws SQLException {
            return modifyConnection(wrapped.getConnection());
        }

        @Override
        public Connection getConnection(String username, String password) throws SQLException {
            return modifyConnection(wrapped.getConnection(username, password));
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return wrapped.getLogWriter();
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            wrapped.setLogWriter(out);
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            wrapped.setLoginTimeout(seconds);
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return wrapped.getLoginTimeout();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return wrapped.getParentLogger();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return wrapped.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return wrapped.isWrapperFor(iface);
        }

    }

}
