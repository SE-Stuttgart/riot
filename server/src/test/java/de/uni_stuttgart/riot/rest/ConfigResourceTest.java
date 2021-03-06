package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;
import de.uni_stuttgart.riot.commons.test.JerseyDBTestBase;
import de.uni_stuttgart.riot.commons.test.TestData;

/**
 * Test class for the calendar which uses the Jersey Test Framework.
 * 
 * @see https://jersey.java.net/documentation/latest/test-framework.html
 */
@TestData({ "/schema/schema_configuration.sql", "/testData/testdata_configuration2.sql" })
public class ConfigResourceTest extends JerseyDBTestBase {

    /** The Constant TARGET. */
    private static final String TARGET = "config";

    /** The Constant TYPE_CONFIG_STORABLE_COLLECTION. */
    private static final GenericType<Collection<ConfigurationEntry>> TYPE_CONFIG_STORABLE_COLLECTION = new GenericType<Collection<ConfigurationEntry>>() {
    };

    /**
     * Tests if a GET request to the resource works.
     */
    @Test
    public void testGetRequest() {
        final Response response = target(TARGET).request().get();
        assertEquals(200, response.getStatus());

        Collection<ConfigurationEntry> configuration = response.readEntity(TYPE_CONFIG_STORABLE_COLLECTION);
        ConfigurationEntry configItem = configuration.iterator().next();

        assertEquals("test_int", configItem.getConfigKey());
        assertEquals("200000", configItem.getConfigValue());
    }

    /**
     * Test get by id request.
     */
    @Test
    public void testGetByIdRequest() {
        ConfigurationEntry configItem = target(TARGET + "/2").request().get().readEntity(ConfigurationEntry.class);
        assertEquals("test_float", configItem.getConfigKey());
        assertEquals("6.1", configItem.getConfigValue());
    }

    /**
     * Test get by key request.
     */
    @Test
    public void testGetByKeyRequest() {
        ConfigurationEntry configItem = target(TARGET + "/key/test_float").request().get().readEntity(ConfigurationEntry.class);
        assertEquals("test_float", configItem.getConfigKey());
        assertEquals("6.1", configItem.getConfigValue());
    }

    /**
     * Tests if a PUT request to the resource works.
     */
    @Test
    public void testPutRequest() {
        ConfigurationEntry csWrite = new ConfigurationEntry(ConfigurationKey.um_hashIterations, "1", Integer.class.getSimpleName());
        Entity<ConfigurationEntry> entity = Entity.entity(csWrite, MediaType.APPLICATION_JSON);

        final Response putResponse = target(TARGET).request().post(entity);
        assertEquals(201, putResponse.getStatus());

        // set the id which was assigned by the DB and check if the read item is the same as the written one
        csWrite.setId(7L);
        ConfigurationEntry csRead = target(TARGET + "/key/um_hashIterations").request().get().readEntity(ConfigurationEntry.class);
        assertEquals(csWrite, csRead);
    }

    /**
     * Test delete by id request.
     */
    @Test
    public void testDeleteByIdRequest() {
        Response delResponse = target(TARGET + "/2").request().delete();
        assertEquals(204, delResponse.getStatus());
        Response getResponse = target(TARGET + "/2").request().get();
        assertEquals(404, getResponse.getStatus());
    }

    /**
     * Test delete by key request.
     */
    @Test
    public void testDeleteByKeyRequest() {
        Response delResponse = target(TARGET + "/key/test_int").request().delete();
        assertEquals(204, delResponse.getStatus());
        Response getResponse = target(TARGET + "/key/test_int").request().get();
        assertEquals(404, getResponse.getStatus());
    }
}
