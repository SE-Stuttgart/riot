package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

/**
 * An exemplary test class which uses the Jersey Test Framework.
 * 
 * @see https://jersey.java.net/documentation/latest/test-framework.html
 */
public class ExampleResourceTest extends JerseyTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.glassfish.jersey.test.JerseyTest#configure()
     */
    @Override
    protected Application configure() {
        return new Application();
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
     * Tests if a GET request to the resource returns the expected plain string.
     */
    @Test
    public void testGetRequest() {
        final String exampleResponse = target("example").request().get(String.class);
        assertEquals("a plaintext response.", exampleResponse);
    }

}
