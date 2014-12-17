package de.uni_stuttgart.riot.commons.test;

import java.io.File;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.rules.TemporaryFolder;

/**
 * Provides a temporary (initially empty) H2 database, also through JNDI.
 */
public class H2DatabaseRule extends JNDIRule {

    private final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private DataSource dataSource;

    @Override
    protected void before() throws Throwable {
        super.before();
        temporaryFolder.create();

        File tempFile = temporaryFolder.newFile();
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:" + tempFile.getAbsolutePath());
        ds.setUser("sa");
        ds.setUser("sa");
        dataSource = ds;
        registerJNDI("jdbc/riot", ds);

        // For now, we just use our regular DataSource as the pool, too, since
        // pool users expect a normal DataSource, anyway, and don't case about
        // the pooling and pooling is not necessary during unit tests.
        registerJNDI("jdbc/riot-pool", ds);
    }

    @Override
    protected void after() {
        super.after();
        temporaryFolder.delete();
    }

    /**
     * @return The JDBC DataSource.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

}