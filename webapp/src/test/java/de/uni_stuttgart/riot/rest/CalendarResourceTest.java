package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import de.uni_stuttgart.riot.contacts.ContactEntry;

/**
 * Test class for the calendar which uses the Jersey Test Framework.
 * 
 * @see https://jersey.java.net/documentation/latest/test-framework.html
 */
public class CalendarResourceTest extends JerseyTest {

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
        final String response = target("calendar").request().get(String.class);
        assertNotNull(response);
    }

}
