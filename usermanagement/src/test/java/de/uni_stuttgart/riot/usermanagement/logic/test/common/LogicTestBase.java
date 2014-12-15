package de.uni_stuttgart.riot.usermanagement.logic.test.common;

import org.junit.Before;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.SqlRunner;

public class LogicTestBase extends BaseDatabaseTest {

    @Before
    public void setUp() throws Exception {
        try (SqlRunner sqlRunner = new SqlRunner(getDataSource())) {
            sqlRunner.runScript("/createschema.sql");
            sqlRunner.runScript("/insertTestValues.sql");
        }
    }

}
