package de.uni_stuttgart.riot.usermanagement.data.test.common;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;

/**
 * Base class for DAOs.
 */
@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class DaoTestBase extends BaseDatabaseTest {

    /**
     * Opens a new Connection with sql2o.
     * 
     * @return The newly opened connection.
     */
    public Connection getConn() {
        return new Sql2o(getDataSource()).open();
    }

}
