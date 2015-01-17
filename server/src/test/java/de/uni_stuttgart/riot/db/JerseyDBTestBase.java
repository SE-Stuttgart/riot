
package de.uni_stuttgart.riot.db;

import javax.sql.DataSource;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.ClassRule;

import de.uni_stuttgart.riot.commons.test.H2DatabaseRule;
import de.uni_stuttgart.riot.commons.test.SqlRunner;

/**
 * Base class for tests that need database access and Jersey.
 */
public class JerseyDBTestBase extends JerseyTest {

    @ClassRule
    public static H2DatabaseRule database = new H2DatabaseRule();

    protected static DataSource getDataSource() {
        return database.getDataSource();
    }

    /**
     * creates calendarEntries Tables used at Tests.
     * 
     * @throws Exception
     */
    @Before
    public void setupTestData() throws Exception {
        try (SqlRunner sqlRunner = new SqlRunner(getDataSource())) {
            sqlRunner.runScript("/createCalendarEntries.sql");
            sqlRunner.runScript("/createThingEntries.sql");
            sqlRunner.runScript("/createContacts.sql");
            sqlRunner.runScript("/insertTestValues.sql");
        }
    }

}
