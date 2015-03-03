package de.uni_stuttgart.riot.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

import de.uni_stuttgart.riot.calendar.CalendarResource;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;
import de.uni_stuttgart.riot.commons.test.BaseResourceTest;
import de.uni_stuttgart.riot.commons.test.TestData;

/**
 * Test class for the calendar which uses the Jersey Test Framework.
 * 
 * @see https://jersey.java.net/documentation/latest/test-framework.html
 */
@TestData({ "/schema/schema_calendar.sql", "/data/testdata_calendar.sql" })
public class CalendarResourceTest extends BaseResourceTest<CalendarResource, CalendarEntry> {

    private static final String CALENDAR = "calendar/";

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

    @Override
    public String getSubPath() {
        return "calendar";
    }

    @Override
    public int getTestDataSize() {
        return 3;
    }

    @Override
    public FilterAttribute getFilter() {
        return new FilterAttribute("title", FilterOperator.EQ, "Standup Meeting 1");
    }

    @Override
    public CalendarEntry getNewObject() {
        return new CalendarEntry(getTestDataSize(), "new appointment", "Standup Meeting x");
    }

    @Override
    public Class<CalendarEntry> getObjectClass() {
        return CalendarEntry.class;
    }

    @Override
    public CalendarEntry getTestObject() {
        return new CalendarEntry(getTestDataSize(), "new appointment", "Standup Meeting x");
    }

}
