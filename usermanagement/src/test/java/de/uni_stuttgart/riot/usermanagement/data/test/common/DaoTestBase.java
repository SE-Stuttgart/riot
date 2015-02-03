package de.uni_stuttgart.riot.usermanagement.data.test.common;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;

/**
 * Base class for DAOs.
 */
@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class DaoTestBase extends BaseDatabaseTest {

}
