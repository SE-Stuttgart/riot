package de.uni_stuttgart.riot.usermanagement.data.test.common;

import org.junit.Before;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.SqlRunner;

/**
 * Base class for DAOs.
 */
public class DaoTestBase extends BaseDatabaseTest {

    @Before
    public void setupTestData() throws Exception {
        try (SqlRunner sqlRunner = new SqlRunner(getDataSource())) {
            sqlRunner.runScript("/createschema.sql");
            sqlRunner.runScript("/insertTestValues.sql");
        }
    }

    public Connection getConn() {
        return new Sql2o(getDataSource()).open();
    }

}
