package de.uni_stuttgart.riot.commons.test;

import javax.sql.DataSource;

import org.junit.ClassRule;

/**
 * Base class for tests that use a database.
 */
public abstract class BaseDatabaseTest {

    /**
     * This rule will manage the database.
     */
    @ClassRule
    public static H2DatabaseRule database = new H2DatabaseRule();

    protected static DataSource getDataSource() {
        return database.getDataSource();
    }

}
