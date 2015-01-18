package de.uni_stuttgart.riot.commons.test;

import javax.sql.DataSource;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestWatcher;

/**
 * Base class for tests that use a database.
 */
public abstract class BaseDatabaseTest {

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
