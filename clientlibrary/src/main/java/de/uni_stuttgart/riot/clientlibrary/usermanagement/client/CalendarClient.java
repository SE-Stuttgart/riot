package de.uni_stuttgart.riot.clientlibrary.usermanagement.client;

// this file needs to be in this package cause the loginClient jsonMapper and the get,post,put,delete functions are only visible here

import org.apache.http.HttpResponse;
import org.codehaus.jackson.type.TypeReference;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;

/**
 * Created by dirk on 1/17/15.
 */
public class CalendarClient {

    private static final String PREFIX = "riot/api/v1/calendar";

    private static final String EVENTS = PREFIX + "/";

    private static final int DELETE_OK_CODE = 204;

    private final LoginClient loginClient;

    /**
     * Constructor.
     * @param loginClient the {@link LoginClient} to be used
     */
    public CalendarClient(LoginClient loginClient) {
        this.loginClient = loginClient;
    }


    /**
     * Creates the event on the server.
     * @param entry new values for the event
     * @return the updated Entry
     * @throws RequestException .
     */
    public CalendarEntry createEvent(CalendarEntry entry) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + EVENTS, entry);
        try {
            CalendarEntry result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(), CalendarEntry.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }


    /**
     * Updates the event.
     * @param entry new values for the event
     * @return the updated Entry
     * @throws RequestException .
     */
    public CalendarEntry updateEvent(CalendarEntry entry) throws RequestException {
        HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl() + EVENTS + entry.getId(), entry);
        try {
            CalendarEntry result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(), CalendarEntry.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }


    /**
     * Delete the event on the server.
     * @param entry new values for the event
     * @return the updated Entry
     * @throws RequestException .
     */
    public boolean deleteEvent(CalendarEntry entry) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + EVENTS + entry.getId());
        try {
            return response.getStatusLine().getStatusCode() == DELETE_OK_CODE;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }


    /**
     * Get all events from the server.
     * @return the list of all events
     * @throws RequestException .
     */
    public Collection<CalendarEntry> getEvents() throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + EVENTS);
        try {
            Collection<CalendarEntry> result = this.loginClient.jsonMapper.readValue(response.getEntity().getContent(), new TypeReference<Collection<CalendarEntry>>() { });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

}
