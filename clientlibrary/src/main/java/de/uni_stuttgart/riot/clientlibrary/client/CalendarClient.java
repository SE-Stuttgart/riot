package de.uni_stuttgart.riot.clientlibrary.client;

import java.io.IOException;
import java.util.Collection;

import de.uni_stuttgart.riot.clientlibrary.BaseClient;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;

/**
 * Created by dirk on 1/17/15.
 */
public class CalendarClient extends BaseClient {

    private static final String EVENTS = "calendar/";

    /**
     * Constructor.
     * 
     * @param serverConnector
     *            the {@link ServerConnector} to be used
     */
    public CalendarClient(ServerConnector serverConnector) {
        super(serverConnector);
    }

    /**
     * Creates the event on the server.
     * 
     * @param entry
     *            new values for the event
     * @return the updated Entry
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public CalendarEntry createEvent(CalendarEntry entry) throws IOException, RequestException {
        return getConnector().doPOST(EVENTS, entry, CalendarEntry.class);
    }

    /**
     * Updates the event.
     * 
     * @param entry
     *            new values for the event
     * @return the updated Entry
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public CalendarEntry updateEvent(CalendarEntry entry) throws RequestException, IOException {
        return getConnector().doPUT(EVENTS + entry.getId(), entry, CalendarEntry.class);
    }

    /**
     * Delete the event on the server.
     * 
     * @param entry
     *            The entry to delete.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public void deleteEvent(CalendarEntry entry) throws RequestException, IOException {
        getConnector().doDELETE(EVENTS + entry.getId());
    }

    /**
     * Get all events from the server.
     * 
     * @return the list of all events
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public Collection<CalendarEntry> getEvents() throws RequestException, IOException {
        try {
            return getConnector().doGETCollection(EVENTS, CalendarEntry.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

}
