package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.rest.MultipleEventsRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterEventRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterThingRequest;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;
import de.uni_stuttgart.riot.thing.rest.UserThingShare;
import de.uni_stuttgart.riot.thing.server.ThingLogic;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * The thing service will handle any access (create, read, update, delete) to the "things".
 */
@Path("things")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class ThingService {

    /** The maximum page size. */
    protected static final int DEFAULT_PAGE_SIZE = 20;

    private final ThingLogic logic = ThingLogic.getThingLogic();

    @Context
    private UriInfo uriInfo;

    /**
     * Gets information about a thing. This is a JSON object containing the thing's type, name, id and state.
     *
     * @param id
     *            The id of the thing.
     * @return The thing if it exists, HTTP 404 otherwise
     */
    @GET
    @Path("{id}")
    public Thing getExistingThing(@PathParam("id") long id) {
        assertPermitted(id, ThingPermission.READ);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            return thing;
        }
    }

    /**
     * Gets the current state of a thing. The JSON format is an array of objects, where each objects represents a property and specifies its
     * <tt>name</tt>, <tt>valueType</tt> and <tt>value</tt>.
     *
     * @param id
     *            The id of the thing.
     * @return The thing's state (or 404 if the thing does not exist).
     */
    @GET
    @Path("{id}/state")
    public ThingState getThingState(@PathParam("id") long id) {
        assertPermitted(id, ThingPermission.READ);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            return ThingState.create(thing);
        }
    }

    /**
     * Gets a description of a thing. See {@link ThingDescription} for details about the JSON format.
     *
     * @param id
     *            The id of the thing.
     * @return A description of the thing's structure (or 404 if the thing does not exist).
     */
    @GET
    @Path("{id}/description")
    public ThingDescription getThingDescription(@PathParam("id") long id) {
        assertPermitted(id, ThingPermission.READ);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            return ThingDescription.create(thing);
        }
    }

    /**
     * Gets the collection for resources.
     *
     * @param offset
     *            the beginning item number
     * @param limit
     *            maximum number of items to return
     * @return the collection. If the both parameters are 0, it returns at maximum 20 elements.
     * 
     * @throws DatasourceFindException
     *             Exception on initialization of things.
     */
    @GET
    public Collection<Thing> get(@QueryParam("offset") int offset, @QueryParam("limit") int limit) throws DatasourceFindException {
        if (limit < 0 || offset < 0) {
            throw new BadRequestException("please provide valid parameter values");
        }

        // Fetch the results
        if (limit == 0) {
            return logic.findThings(offset, DEFAULT_PAGE_SIZE, null, ThingPermission.READ);
        } else {
            return logic.findThings(offset, limit, null, ThingPermission.READ);
        }
    }

    /**
     * Creates a new model with data from the request body.
     *
     * @param request
     *            object specifying the filter attributes (pagination also possible)
     * @return collection containing elements that applied to filter
     * @throws DatasourceFindException
     *             when retrieving the data fails
     */
    @POST
    @Path("/filter")
    public Collection<Thing> getBy(FilteredRequest request) throws DatasourceFindException {
        if (request == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        return logic.findThings(request, null, ThingPermission.READ);
    }

    /**
     * Registers a new thing and returns the created thing.
     *
     * @param request
     *            The request data for creating the thing.
     * @return An HTTP created (201) response if successful
     * @throws DatasourceInsertException
     *             When insertion failed
     */
    @POST
    @RequiresPermissions("thing:create")
    public Response registerNewThing(RegisterThingRequest request) throws DatasourceInsertException {
        if (request == null) {
            throw new BadRequestException("Please provide an entity in the request body.");
        }

        // Find out the owner (which is the current user).
        UserManagementFacade umFacade = UserManagementFacade.getInstance();
        long ownerId = umFacade.getCurrentUserId();

        // Register the thing.
        Thing thing = this.logic.registerThing(request.getType(), request.getName(), ownerId);

        // Give the owner full access.
        try {
            this.logic.addOrUpdateShare(thing.getId(), new ThingShare(ownerId, EnumSet.allOf(ThingPermission.class)));
        } catch (DatasourceFindException | DatasourceDeleteException e) {
            throw new RuntimeException("The thing just inserted is not there!", e);
        }

        // Fill with initial values provided by the client.
        if (request.getInitialState() != null) {
            request.getInitialState().apply(thing);
        }

        return Response.created(getUriForThing(thing)).entity(thing).build();
    }

    /**
     * Deletes the thing with the given id.
     *
     * @param id
     *            the id
     * @return the response, which is either HTTP 204 or a HTTP 404 if no row matched the id.
     */
    @DELETE
    @Path("{id}")
    public Response unregisterThing(@PathParam("id") long id) {
        assertPermitted(id, ThingPermission.DELETE);
        try {
            this.logic.unregisterThing(id);
        } catch (DatasourceDeleteException exception) {
            throw new NotFoundException("No such resource exists or it has already been deleted.", exception);
        }

        return Response.noContent().build();
    }

    /**
     * Gets the uri for a thing.
     *
     * @param thing
     *            the thing
     * @return the uri for the thing
     */
    protected URI getUriForThing(Thing thing) {
        return uriInfo.getBaseUriBuilder().path(this.getClass()).path(Long.toString(thing.getId())).build();
    }

    /**
     * Returns the last time at which the thing connected to the server.
     * 
     * @param id
     *            The id of the thing.
     * @return The time.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @GET
    @Path("{id}/online")
    public Date lastConnection(@PathParam("id") long id) throws DatasourceFindException {
        assertPermitted(id, ThingPermission.READ);
        return this.logic.getLastConnection(id);
    }

    /**
     * See {@link RegisterEventRequest} and {@link ThingLogic#registerToEvent(long, long, String)}.
     * 
     * @param observerId
     *            The ID of the thing that wants to register to the given event.
     * @param request
     *            The request content.
     * @return Nothing.
     * @throws DatasourceFindException
     *             If one of the things or the event could not be found.
     */
    @POST
    @Path("{id}/register")
    public Response registerToEvent(@PathParam("id") long observerId, RegisterEventRequest request) throws DatasourceFindException {
        assertPermitted(observerId, ThingPermission.EXECUTE);
        assertPermitted(request.getTargetThingID(), ThingPermission.READ);
        this.logic.registerToEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
        return Response.noContent().build();
    }

    /**
     * See {@link RegisterEventRequest} and {@link ThingLogic#unregisterFromEvent(long, long, String)}.
     * 
     * @param observerId
     *            The ID of the thing that wants to unregister from the given event.
     * @param request
     *            The request content.
     * @return Nothing.
     * @throws DatasourceFindException
     *             If one of the things or the event could not be found.
     */
    @POST
    @Path("{id}/unregister")
    public Response unregisterFromEvent(@PathParam("id") long observerId, RegisterEventRequest request) throws DatasourceFindException {
        assertPermitted(observerId, ThingPermission.EXECUTE);
        this.logic.unregisterFromEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
        return Response.noContent().build();
    }

    /**
     * See {@link RegisterEventRequest}. This method allows to register to and unregister from multiple events at once.
     * 
     * @param observerId
     *            The ID of the thing that wants to register to or unregister from the given events.
     * @param requests
     *            The request content.
     * @return Nothing.
     * @throws DatasourceFindException
     *             If one of the things or the event could not be found.
     */
    @POST
    @Path("{id}/multipleEvents")
    public Response multipleEvents(@PathParam("id") long observerId, MultipleEventsRequest requests) throws DatasourceFindException {
        if (requests.getRegisterTo() != null) {
            for (RegisterEventRequest request : requests.getRegisterTo()) {
                assertPermitted(observerId, ThingPermission.EXECUTE);
                assertPermitted(request.getTargetThingID(), ThingPermission.READ);
                this.logic.registerToEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
            }
        }
        if (requests.getUnregisterFrom() != null) {
            for (RegisterEventRequest request : requests.getUnregisterFrom()) {
                assertPermitted(observerId, ThingPermission.EXECUTE);
                this.logic.unregisterFromEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
            }
        }
        return Response.noContent().build();
    }

    /**
     * (Un)shares a thing with another user.
     * 
     * @param thingId
     *            The id of the thing to be (un)shared.
     * @param share
     *            The sharing information, containing the user's ID and the permission he/she gets. In particular, the thing will be
     *            unshared if the permissions are empty.
     * @throws DatasourceInsertException
     *             When storing the information failed.
     * @throws DatasourceDeleteException
     *             When storing the information failed.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @POST
    @Path("{id}/shares")
    public void addOrUpdateShare(@PathParam("id") long thingId, ThingShare share) throws DatasourceFindException, DatasourceDeleteException, DatasourceInsertException {
        assertPermitted(thingId, ThingPermission.SHARE);
        this.logic.addOrUpdateShare(thingId, share);
    }

    /**
     * Revokes all permissions from a user for a thing.
     * 
     * @param thingId
     *            The thing id.
     * @param userId
     *            The user id.
     * @throws DatasourceDeleteException
     *             When storing the information failed.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @DELETE
    @Path("{id}/shares/{userId}")
    public void unshare(@PathParam("id") long thingId, @PathParam("userId") long userId) throws DatasourceDeleteException, DatasourceFindException {
        assertPermitted(thingId, ThingPermission.SHARE);
        this.logic.unshare(thingId, userId);
    }

    /**
     * Retrieves all shares for a thing, that is, all users that can access the thing and their permissions. The users are simply included
     * as user IDs.
     * 
     * @param thingId
     *            The thing id.
     * @return The shares for the thing.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @GET
    @Path("{id}/shares")
    public Collection<ThingShare> getThingShares(@PathParam("id") long thingId) throws DatasourceFindException {
        assertPermitted(thingId, ThingPermission.SHARE);
        return this.logic.getThingShares(thingId);
    }

    /**
     * Retrieves all shares for a thing, that is, all users that can access the thing and their permissions. The users are included as User
     * objects.
     * 
     * @param thingId
     *            The thing id.
     * @return The shares for the thing.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @GET
    @Path("{id}/sharesWithUsers")
    public Collection<UserThingShare> getUserThingShares(@PathParam("id") long thingId) throws DatasourceFindException {
        assertPermitted(thingId, ThingPermission.SHARE);
        return this.logic.getUserThingShares(thingId);
    }

    /**
     * Called by the executing thing to raise an event.
     * 
     * @param thingId
     *            ID of the thing
     * @param eventInstance
     *            The event instance.
     * @return Nothing.
     * @throws DatasourceFindException
     *             If the corresponding thing or its event could not be found.
     */
    @POST
    @Path("{id}/notify")
    public Response notifyEvent(@PathParam("id") long thingId, EventInstance eventInstance) throws DatasourceFindException {
        assertPermitted(thingId, ThingPermission.EXECUTE);
        this.logic.fireEvent(eventInstance);
        return Response.noContent().build();
    }

    /**
     * See {@link ThingLogic#submitAction(ActionInstance)}.
     * 
     * @param actionInstance
     *            The action instance.
     * @param thingId
     *            ID of the thing
     * @throws DatasourceFindException
     *             If the thing was not found.
     */
    @POST
    @Path("{id}/action")
    public void submitAction(@PathParam("id") long thingId, ActionInstance actionInstance) throws DatasourceFindException {
        assertPermitted(thingId, ThingPermission.CONTROL);
        this.logic.submitAction(actionInstance);
    }

    /**
     * See {@link ThingLogic#getThingUpdates(long)} and {@link ThingUpdatesResponse} for details.
     * 
     * @param id
     *            The ID of the thing.
     * @return The updates since the last call.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @GET
    @Path("{id}/updates")
    public ThingUpdatesResponse getUpdates(@PathParam("id") long id) throws DatasourceFindException {
        assertPermitted(id, ThingPermission.EXECUTE);
        return this.logic.getThingUpdates(id);
    }

    /**
     * Checks if the user has the permission for the given thing. Throws a {@link ForbiddenException} if the user is not permitted.
     *
     * @param id
     *            The id of the thing.
     * @param permission
     *            The permission to check for.
     * @return true, if is permitted. If not permitted a {@link ForbiddenException} will be thrown.
     */
    private void assertPermitted(long id, ThingPermission permission) {
        try {
            if (!logic.canAccess(id, null, permission)) {
                throw new ForbiddenException("The user is not permitted to do the desired action");
            }
        } catch (DatasourceFindException e) {
            throw new NotFoundException(e);
        }
    }

}
