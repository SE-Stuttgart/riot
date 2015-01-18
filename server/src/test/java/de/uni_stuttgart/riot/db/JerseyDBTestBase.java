package de.uni_stuttgart.riot.db;

import javax.sql.DataSource;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

import de.uni_stuttgart.riot.commons.test.H2DatabaseRule;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.commons.test.TestDataWatcher;

/**
 * Base class for tests that need database access and Jersey.
 */
public class JerseyDBTestBase extends JerseyTest {

    /**
     * This rule will manage the database.
     */
    @ClassRule
    public static H2DatabaseRule database = new H2DatabaseRule();

    /**
     * Detects {@link TestData} annotations and executes them.
     */
    @Rule
    public TestWatcher annotationWatcher = new TestDataWatcher(database);

    /**
     * Gets the JDBC data source.
     * 
     * @return The JDBC data source for this test instance.
     */
    protected static DataSource getDataSource() {
        return database.getDataSource();
    }

}
