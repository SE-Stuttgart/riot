package de.uni_stuttgart.riot.commons.test;

import java.io.File;
import java.lang.reflect.Field;

import javax.sql.DataSource;

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
        dataSource = ds;

        // Register the data source with JNDI
        registerJNDI("jdbc/riot", ds);

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

}
