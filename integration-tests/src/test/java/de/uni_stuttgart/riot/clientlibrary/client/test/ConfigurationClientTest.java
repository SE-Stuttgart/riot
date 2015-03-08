package de.uni_stuttgart.riot.clientlibrary.client.test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.client.ConfigurationClient;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;
import de.uni_stuttgart.riot.commons.test.TestData;

@TestData({ "/schema/schema_configuration.sql", "/testData/testdata_configuration2.sql", "/data/testdata_configuration.sql", "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql" })
public class ConfigurationClientTest extends BaseClientTest {

    ConfigurationClient cc;

    @Before
    public void setupClient() {
        cc = new ConfigurationClient(getLoggedInConnector());
    }

    @Test
    public void getConfigurationEntryByIdTest() throws Exception {
        ConfigurationEntry ce = cc.getConfigurationEntry(1);
        assertThat(ce.getConfigKey(), is(ConfigurationKey.test_int.name()));
    }

    @Test(expected = NotFoundException.class)
    public void getConfigurationEntryByNotExistingIdTest() throws Exception {
        cc.getConfigurationEntry(100);
    }

    @Test
    public void getConfigurationEntryByKeyTest() throws Exception {
        ConfigurationEntry ce = cc.getConfigurationEntry(ConfigurationKey.test_int);
        assertThat(ce.getConfigKey(), is(ConfigurationKey.test_int.name()));
    }

    @Test(expected = NotFoundException.class)
    public void getConfigurationEntryByNotExistingKeyTest() throws Exception {
        cc.getConfigurationEntry(ConfigurationKey.test_nonexistingDouble);
    }

    @Test
    public void shouldRemoveByIdAndAdd() throws Exception {
        // Find out the ID of one entry.
        ConfigurationEntry oldEntry = cc.getConfigurationEntry(ConfigurationKey.test_long);

        // Delete it and check that it is gone.
        cc.removeConfigurationEntry(oldEntry.getId());
        try {
            cc.getConfigurationEntry(oldEntry.getId());
            fail();
        } catch (NotFoundException e) {
            // exception expected, deleting twice should not work
        }

        // Double-delete it, this should not work.
        try {
            cc.removeConfigurationEntry(oldEntry.getId());
            fail();
        } catch (RequestException e) {
            // exception expected
        }

        // Try adding it with a wrong type.
        try {
            cc.addConfigurationEntry(ConfigurationKey.test_long, "bla");
        } catch (RequestException e) {
            // exception expected, was a String instead of an int.
        }

        // Add it with another value.
        cc.addConfigurationEntry(ConfigurationKey.test_long, 4242L);
        assertThat(cc.getConfigurationValue(ConfigurationKey.test_long, Long.class), is(4242L));

        // Delete it again, this time by key.
        cc.removeConfigurationEntry(ConfigurationKey.test_long);
        try {
            cc.getConfigurationEntry(ConfigurationKey.test_long);
            fail();
        } catch (NotFoundException e) {
            // exception expected
        }

        // Double-delete it, this should not work.
        try {
            cc.removeConfigurationEntry(ConfigurationKey.test_long);
            fail();
        } catch (RequestException e) {
            // exception expected
        }

        // And add it with the original value.
        cc.addConfigurationEntry(ConfigurationKey.test_long, 100L);
        assertThat(cc.getConfigurationValue(ConfigurationKey.test_long, Long.class), is(100L));
    }

    @Test(expected = RequestException.class)
    public void addDoubleConfigurationEntryTest() throws Exception {
        // This entry already exists in the database, so adding it a second time will throw an error.
        cc.addConfigurationEntry(ConfigurationKey.um_hashIterations, 100);
    }

    @Test(expected = RequestException.class)
    public void removeNotExistingConfigurationEntryByIdTest() throws Exception {
        cc.removeConfigurationEntry(100);
    }

    @Test(expected = RequestException.class)
    public void removeNotExistingConfigurationEntryByKeyTest() throws Exception {
        cc.removeConfigurationEntry(ConfigurationKey.test_nonexistingDouble);
    }

    @Test
    public void updateConfigurationEntryTest() throws Exception {
        cc.updateConfigurationEntry(1, new ConfigurationEntry(ConfigurationKey.test_int, "10", Integer.class.getSimpleName()));
        assertThat(cc.getConfigurationValue(1, Integer.class), is(10));
    }

    @Test(expected = RequestException.class)
    public void updateNotExistingConfigurationEntryTest() throws Exception {
        cc.updateConfigurationEntry(100, new ConfigurationEntry(ConfigurationKey.test_int, "10", Integer.class.getSimpleName()));
    }

    @Test
    public void getAllTest() throws RequestException, IOException {
        assertThat(cc.getConfiguration(), not(empty()));
    }
}
