package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;
import de.uni_stuttgart.riot.commons.test.JerseyDBTestBase;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;

/**
 * Test class for the calendar which uses the Jersey Test Framework.
 * 
 * @see https://jersey.java.net/documentation/latest/test-framework.html
 */
@TestData({ "/schema/schema_calendar.sql", "/data/testdata_calendar.sql" })
public class CalendarResourceTest extends JerseyDBTestBase {

    private static final String CALENDAR = "calendar/";

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
     * Tests if a GET request to the resource works.
     */
    @Test
    public void testGetRequests() {
        // get collection
        final List<CalendarEntry> response = target(CALENDAR).request().get(new GenericType<List<CalendarEntry>>() {
        });
        assertEquals(3, response.size());
        // get single item
        final CalendarEntry entry = target(CALENDAR + "1").request().get(CalendarEntry.class);
        assertEquals("Standup Meeting 1", entry.getTitle());
    }

    /**
     * Tests post and delete requests by first creating, then deleting a resource.
     */
    @Test
    public void testPostDeleteRequest() {
        CalendarEntry newdate = new CalendarEntry();
        newdate.setTitle("a title");
        Entity<CalendarEntry> ent = Entity.entity(newdate, "application/json");
        Response response = target(CALENDAR).request().post(ent);
        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        assertTrue("response should contain uri of new entity", response.getMetadata().containsKey("location"));

        String uriCreated = (String) response.getMetadata().get("location").get(0);
        response = this.client().target(uriCreated).request().delete();
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

}
