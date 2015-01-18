package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.db.JerseyDBTestBase;

/**
 * Test class for the contacts which uses the Jersey Test Framework.
 * 
 * @see https://jersey.java.net/documentation/latest/test-framework.html
 */
@TestData({ "/schema/schema_contacts.sql", "/data/testdata_contacts.sql" })
public class ContactsResourceTest extends JerseyDBTestBase {

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#getBaseUri()
     */
    @Override
    protected URI getBaseUri() {
        return UriBuilder.fromUri(super.getBaseUri()).path("api/v1/").build();
    }

    /**
     * Tests if a GET request to the resource work.
     */
    @Test
    public void testGetRequest() {
        final String response = target("contacts").request().get(String.class);
        assertNotNull(response);
    }

    /**
     * Tests if a GET request (with id) to the resource work.
     */
    @Test
    public void testGetRequestWithId() {
        final Contact response = target("contacts/1").request().get(Contact.class);
        assertEquals("John", response.getFirstName());
    }
}
