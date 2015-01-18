package de.uni_stuttgart.riot.usermanagement.logic.test.common;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class LogicTestBase extends BaseDatabaseTest {

}
