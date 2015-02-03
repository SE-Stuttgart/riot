package de.uni_stuttgart.riot.server.commons.config.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.config.Configuration;
import de.uni_stuttgart.riot.server.commons.config.ConfigurationException;
import de.uni_stuttgart.riot.server.commons.config.ConfigurationKey;

@TestData({ "/schema/schema_configuration.sql" })
public class ConfigurationTest extends BaseDatabaseTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @TestData({ "/schema/schema_configuration.sql", "/testData/testdata_configuration2.sql" })
    public void testGet() {
        assertEquals(200000, Configuration.getInt(ConfigurationKey.test_int));
        assertEquals(6.1f, Configuration.getFloat(ConfigurationKey.test_float), 0);
        assertEquals(30.123, Configuration.getDouble(ConfigurationKey.test_double), 0);
        assertEquals(true, Configuration.getBoolean(ConfigurationKey.test_boolean));
        assertEquals(100L, Configuration.getLong(ConfigurationKey.test_long));
        assertEquals("!?=", Configuration.getString(ConfigurationKey.test_string));
    }

    @Test
    public void testAddOrUpdateInt() throws Exception {
        // add the value
        Configuration.addOrUpdate(ConfigurationKey.test_int, 2);
        assertEquals(2, Configuration.getInt(ConfigurationKey.test_int));

        // update the value
        Configuration.addOrUpdate(ConfigurationKey.test_int, 4);
        assertEquals(4, Configuration.getInt(ConfigurationKey.test_int));
    }

    @Test
    public void testAddOrUpdateString() throws Exception {
        // add the value
        Configuration.addOrUpdate(ConfigurationKey.test_string, "!§$");
        assertEquals("!§$", Configuration.getString(ConfigurationKey.test_string));

        // update the value
        Configuration.addOrUpdate(ConfigurationKey.test_string, "!§$=");
        assertEquals("!§$=", Configuration.getString(ConfigurationKey.test_string));
    }

    @Test
    public void testAddOrUpdateFloat() throws Exception {
        // add the value
        Configuration.addOrUpdate(ConfigurationKey.test_float, 2.5f);
        assertEquals(2.5f, Configuration.getFloat(ConfigurationKey.test_float), 0);

        // update the value
        Configuration.addOrUpdate(ConfigurationKey.test_float, 3.5f);
        assertEquals(3.5f, Configuration.getFloat(ConfigurationKey.test_float), 0);
    }

    @Test
    public void testAddOrUpdateDouble() throws Exception {
        // add the value
        Configuration.addOrUpdate(ConfigurationKey.test_double, 5.5);
        assertEquals(5.5, Configuration.getDouble(ConfigurationKey.test_double), 0);

        // update the value
        Configuration.addOrUpdate(ConfigurationKey.test_double, 6.5);
        assertEquals(6.5, Configuration.getDouble(ConfigurationKey.test_double), 0);
    }

    @Test
    public void testAddOrUpdateLong() throws Exception {
        // add the value
        Configuration.addOrUpdate(ConfigurationKey.test_long, 5L);
        assertEquals(5L, Configuration.getLong(ConfigurationKey.test_long));

        // update the value
        Configuration.addOrUpdate(ConfigurationKey.test_long, 6L);
        assertEquals(6L, Configuration.getLong(ConfigurationKey.test_long));
    }

    @Test
    public void testAddOrUpdateBoolean() throws Exception {
        // add the value
        Configuration.addOrUpdate(ConfigurationKey.test_boolean, true);
        assertEquals(true, Configuration.getBoolean(ConfigurationKey.test_boolean));

        // update the value
        Configuration.addOrUpdate(ConfigurationKey.test_boolean, false);
        assertEquals(false, Configuration.getBoolean(ConfigurationKey.test_boolean));
    }

    @Test(expected = NullPointerException.class)
    public void testAddOrUpdateFailNull() throws Exception {
        Configuration.addOrUpdate(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddOrUpdateFailKeyNull() throws Exception {
        Configuration.addOrUpdate(null, 1);
    }

    @Test(expected = NullPointerException.class)
    public void testAddOrUpdateFailValueNull() throws Exception {
        Configuration.addOrUpdate(ConfigurationKey.test_int, null);
    }

    @Test(expected = ConfigurationException.class)
    @TestData({ "/schema/schema_configuration.sql", "/testData/testdata_configuration2.sql" })
    public void testDelete() throws Exception {
        Configuration.init();
        try {
            Configuration.getInt(ConfigurationKey.test_int);
        } catch (ConfigurationException e) {
            fail("The value should be available");
        }
        Configuration.delete(ConfigurationKey.test_int);
        Configuration.getInt(ConfigurationKey.test_int); // exception is expected here, because the value does not exist anymore
    }

}
