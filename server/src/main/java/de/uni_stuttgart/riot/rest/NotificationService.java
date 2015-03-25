package de.uni_stuttgart.riot.rest;

import java.util.Collection;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.server.NotificationLogic;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

/**
 * The notification service handles access to nofications.
 */
@Path("notifications")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class NotificationService {

    /** The maximum page size. */
    protected static final int DEFAULT_PAGE_SIZE = 20;

    private final NotificationLogic logic = NotificationLogic.getNotificationLogic();

    /**
     * Gets all non-dismissed notifications of the current user, ordered by time (newest first).
     * 
     * @return The notifications.
     * @throws DatasourceFindException
     *             When reading the database fails.
     */
    @GET
    public Collection<Notification> getMyNotifications() throws DatasourceFindException {
        return logic.getOutstandingNotifications(null);
    }

    /**
     * Gets all notifications for the current user (paginated, also dismissed ones).
     *
     * @param offset
     *            The beginning item number
     * @param limit
     *            Maximum number of items to return
     * @return The notifications in a collection.
     * @throws DatasourceFindException
     *             the datasource find exception
     */
    @GET
    @Path("/all")
    public Collection<Notification> getNotifications(@QueryParam("offset") int offset, @QueryParam("limit") int limit) throws DatasourceFindException {
        if (limit < 0 || offset < 0) {
            throw new BadRequestException("please provide valid parameter values");
        }

        // Fetch the results
        if (limit == 0) {
            return logic.findNotifications(null, offset, DEFAULT_PAGE_SIZE);
        } else {
            return logic.findNotifications(null, offset, limit);
        }
    }

    /**
     * Updates a notification. Note that only the {@link Notification#isDismissed()} field can be changed this way.
     * 
     * @param id
     *            The ID of the notification.
     * @param notification
     *            The notification entity.
     * @return An empty response (HTTP 200), 404 if not found.
     * @throws DatasourceUpdateException
     *             When storing the information failed.
     */
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id, Notification notification) throws DatasourceUpdateException {
        if (notification == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        notification.setId(id);

        try {
            logic.updateNotification(notification, null);
        } catch (DatasourceFindException e) {
            throw new NotFoundException(e);
        }

        return Response.noContent().build();
    }

}
