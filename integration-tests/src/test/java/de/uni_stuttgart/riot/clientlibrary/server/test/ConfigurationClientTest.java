package de.uni_stuttgart.riot.clientlibrary.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.server.client.ConfigurationClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.DefaultTokenManager;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.test.ShiroEnabledTest;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;
import de.uni_stuttgart.riot.commons.test.TestData;

@TestData({ "/schema/schema_configuration.sql", "/testData/testdata_configuration2.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class ConfigurationClientTest extends ShiroEnabledTest {

    /*
     * getConfigurationEntry Tests
     */

    @Test
    public void getConfigurationEntryByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        ConfigurationEntry ce = cc.getConfigurationEntry(1);
        assertEquals(ConfigurationKey.test_int.name(), ce.getConfigKey());
    }

    @Test(expected = RequestException.class)
    public void getConfigurationEntryByNotExistingIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getConfigurationEntry(100);
    }

    @Test
    public void getConfigurationEntryByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        ConfigurationEntry ce = cc.getConfigurationEntry(ConfigurationKey.test_int);
        assertEquals(ConfigurationKey.test_int.name(), ce.getConfigKey());
    }

    @Test(expected = RequestException.class)
    public void getConfigurationEntryByNotExistingKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getConfigurationEntry(ConfigurationKey.test_nonexistingDouble);
    }

    /*
     * getIntegerConfigurationValue Tests
     */

    @Test
    public void getIntegerConfigurationValueByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        int cv = cc.getIntegerConfigurationValue(ConfigurationKey.test_int);
        assertEquals(200000, cv);
    }

    @Test
    public void getIntegerConfigurationValueByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        int cv = cc.getIntegerConfigurationValue(1);
        assertEquals(200000, cv);
    }

    @Test(expected = RequestException.class)
    public void getIntegerConfigurationValueByKeyInvalidTypeTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getIntegerConfigurationValue(ConfigurationKey.test_boolean);
    }

    /*
     * getLongConfigurationValue Tests
     */

    @Test
    public void getLongConfigurationValueByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        long cv = cc.getLongConfigurationValue(ConfigurationKey.test_long);
        assertEquals(100, cv);
    }

    @Test
    public void getLongConfigurationValueByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        long cv = cc.getLongConfigurationValue(5);
        assertEquals(100, cv);
    }

    @Test(expected = RequestException.class)
    public void getLongConfigurationValueByInvalidKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getLongConfigurationValue(ConfigurationKey.um_hashIterations);
    }

    @Test(expected = RequestException.class)
    public void getLongConfigurationValueByKeyInvalidTypeTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getLongConfigurationValue(ConfigurationKey.test_boolean);
    }

    /*
     * getFloatConfigurationValue Tests
     */

    @Test
    public void getFloatConfigurationValueByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        float cv = cc.getFloatConfigurationValue(ConfigurationKey.test_float);
        assertEquals(6.1, cv, 0.1);
    }

    @Test
    public void getFloatConfigurationValueByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        float cv = cc.getFloatConfigurationValue(2);
        assertEquals(6.1, cv, 0.1);
    }

    @Test(expected = RequestException.class)
    public void getFloatConfigurationValueByInvalidKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getFloatConfigurationValue(ConfigurationKey.um_hashIterations);
    }

    @Test(expected = RequestException.class)
    public void getFloatConfigurationValueByKeyInvalidTypeTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getFloatConfigurationValue(ConfigurationKey.test_boolean);
    }

    /*
     * getDoubleConfigurationValue Tests
     */

    @Test
    public void getDoubleConfigurationValueByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        double cv = cc.getDoubleConfigurationValue(ConfigurationKey.test_double);
        assertEquals(30.123, cv, 0);
    }

    @Test
    public void getDoubleConfigurationValueByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        double cv = cc.getDoubleConfigurationValue(3);
        assertEquals(30.123, cv, 0);
    }

    @Test(expected = RequestException.class)
    public void getDoubleConfigurationValueByInvalidKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getDoubleConfigurationValue(ConfigurationKey.um_hashIterations);
    }

    @Test(expected = RequestException.class)
    public void getDoubleConfigurationValueByKeyInvalidTypeTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getDoubleConfigurationValue(ConfigurationKey.test_boolean);
    }

    /*
     * getBooleanConfigurationValue Tests
     */

    @Test
    public void getBooleanConfigurationValueByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        boolean cv = cc.getBooleanConfigurationValue(ConfigurationKey.test_boolean);
        assertTrue(cv);
    }

    @Test
    public void getBooleanConfigurationValueByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        boolean cv = cc.getBooleanConfigurationValue(4);
        assertTrue(cv);
    }

    @Test(expected = RequestException.class)
    public void getBooleanConfigurationValueByInvalidKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getBooleanConfigurationValue(ConfigurationKey.um_hashIterations);
    }

    @Test(expected = RequestException.class)
    public void getBooleanConfigurationValueByKeyInvalidTypeTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getBooleanConfigurationValue(ConfigurationKey.test_int);
    }

    /*
     * getStringConfigurationValue Tests
     */

    @Test
    public void getStringConfigurationValueByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        String cv = cc.getStringConfigurationValue(ConfigurationKey.test_string);
        assertEquals("!?=", cv);
    }

    @Test
    public void getStringConfigurationValueByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        String cv = cc.getStringConfigurationValue(6);
        assertEquals("!?=", cv);
    }

    @Test(expected = RequestException.class)
    public void getStringConfigurationValueByInvalidKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getStringConfigurationValue(ConfigurationKey.um_hashIterations);
    }

    @Test(expected = RequestException.class)
    public void getStringConfigurationValueByKeyInvalidTypeTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.getStringConfigurationValue(ConfigurationKey.test_int);
    }

    /*
     * addConfigurationEntry Tests
     */

    // @Test TODO Fix this test. It can't work because the value is already in testdata_configuration.sql.
    public void addConfigurationEntryTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.addConfigurationEntry(ConfigurationKey.um_hashIterations, 100);
        int cv = cc.getIntegerConfigurationValue(ConfigurationKey.um_hashIterations);
        assertEquals(100, cv);
    }

    @Test(expected = RequestException.class)
    public void addDoubleConfigurationEntryTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.addConfigurationEntry(ConfigurationKey.um_hashIterations, 100);
        cc.addConfigurationEntry(ConfigurationKey.um_hashIterations, 100);
    }

    @Test(expected = RequestException.class)
    public void addWrongTypeConfigurationEntryTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.addConfigurationEntry(ConfigurationKey.um_hashIterations, "bla");
    }

    /*
     * removeConfigurationEntry Tests
     */

    @Test
    public void removeConfigurationEntryByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.removeConfigurationEntry(1);
        try {
            cc.getConfigurationEntry(1);
            fail();
        } catch (RequestException e) {
            // exception expected
        }
    }

    @Test
    public void removeConfigurationEntryByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.removeConfigurationEntry(ConfigurationKey.test_int);
        try {
            cc.getConfigurationEntry(ConfigurationKey.test_int);
            fail();
        } catch (RequestException e) {
            // exception expected
        }
    }

    @Test(expected = RequestException.class)
    public void removeDoubleConfigurationEntryByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.removeConfigurationEntry(1);
        cc.removeConfigurationEntry(1);
    }

    @Test(expected = RequestException.class)
    public void removeDoubleConfigurationEntryByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.removeConfigurationEntry(ConfigurationKey.test_int);
        cc.removeConfigurationEntry(ConfigurationKey.test_int);
    }

    @Test(expected = RequestException.class)
    public void removeNotExistingConfigurationEntryByIdTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.removeConfigurationEntry(100);
    }

    @Test(expected = RequestException.class)
    public void removeNotExistingConfigurationEntryByKeyTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.removeConfigurationEntry(ConfigurationKey.test_nonexistingDouble);
    }

    /*
     * updateConfigurationEntry Tests
     */

    @Test
    public void updateConfigurationEntryTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.updateConfigurationEntry(1, new ConfigurationEntry(ConfigurationKey.test_int, "10", Integer.class.getSimpleName()));
        int cv = cc.getIntegerConfigurationValue(1);
        assertEquals(10, cv);
    }

    @Test(expected = RequestException.class)
    public void updateNotExistingConfigurationEntryTest() throws Exception {
        ConfigurationClient cc = getLoggedInConfigurationClient();
        cc.updateConfigurationEntry(100, new ConfigurationEntry(ConfigurationKey.test_int, "10", Integer.class.getSimpleName()));
    }

    /*
     * helper methods
     */

    private ConfigurationClient getLoggedInConfigurationClient() throws Exception {
        LoginClient loginClient = new LoginClient("http://localhost:" + getPort(), "TestThing", new DefaultTokenManager());
        loginClient.login("Yoda", "YodaPW");
        return new ConfigurationClient(loginClient);
    }

}
