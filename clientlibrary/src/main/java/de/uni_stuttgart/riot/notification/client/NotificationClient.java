package de.uni_stuttgart.riot.notification.client;

import java.io.IOException;
import java.util.Collection;

import de.uni_stuttgart.riot.clientlibrary.BaseClient;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.notification.Notification;

/**
 * Rest Client for handling {@link Notification} operations.
 */
public class NotificationClient extends BaseClient {

    private static final String THINGS_PREFIX = "notifications/";
    private static final String GET_OUTSTANDING = THINGS_PREFIX;

    /**
     * Creates a new NotificationClient.
     * 
     * @param connector
     *            The {@link ServerConnector} to be used.
     */
    public NotificationClient(ServerConnector connector) {
        super(connector);
    }

    /**
     * Gets all non-dismissed notifications for the logged-in user.
     * 
     * @return The currently outstanding notifications.
     * @throws RequestException
     *             When executing the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    public Collection<Notification> getOutstandingNotifications() throws IOException, RequestException {
        try {
            return getConnector().doGETCollection(GET_OUTSTANDING, Notification.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

}
