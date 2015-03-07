package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.commons.ShareRequest;
import de.uni_stuttgart.riot.thing.commons.ThingPermission;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.commons.ShareRequest;
import de.uni_stuttgart.riot.thing.commons.ThingPermission;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.thing.rest.RegisterRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterThingRequest;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
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
            this.logic.share(thing.getId(), ownerId, ThingPermission.FULL);
        } catch (DatasourceFindException e) {
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
     * See {@link RegisterRequest} and {@link ThingLogic#registerToEvent(long, long, String)}.
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
    public Response registerToEvent(@PathParam("id") long observerId, RegisterRequest request) throws DatasourceFindException {
        assertPermitted(observerId, ThingPermission.EXECUTE);
        assertPermitted(request.getTargetThingID(), ThingPermission.READ);
        this.logic.registerToEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
        return Response.noContent().build();
    }

    /**
     * See {@link RegisterRequest} and {@link ThingLogic#registerToEvent(long, long, String)}.
     * 
     * @param observerId
     *            The ID of the thing that wants to register to the given events.
     * @param requests
     *            The request content.
     * @return Nothing.
     * @throws DatasourceFindException
     *             If one of the things or the event could not be found.
     */
    @POST
    @Path("{id}/registerMultiple")
    public Response registerToEvents(@PathParam("id") long observerId, Collection<RegisterRequest> requests) throws DatasourceFindException {
        for (RegisterRequest request : requests) {
            assertPermitted(observerId, ThingPermission.EXECUTE);
            assertPermitted(request.getTargetThingID(), ThingPermission.READ);
            this.logic.registerToEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
        }
        return Response.noContent().build();
    }

    /**
     * See {@link RegisterRequest} and {@link ThingLogic#unregisterFromEvent(long, long, String)}.
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
    public Response unregisterFromEvent(@PathParam("id") long observerId, RegisterRequest request) throws DatasourceFindException {
        assertPermitted(observerId, ThingPermission.EXECUTE);
        this.logic.unregisterFromEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
        return Response.noContent().build();
    }

    /**
     * See {@link RegisterRequest} and {@link ThingLogic#unregisterFromEvent(long, long, String)}.
     * 
     * @param observerId
     *            The ID of the thing that wants to unregister from the given events.
     * @param requests
     *            The request content.
     * @return Nothing.
     * @throws DatasourceFindException
     *             If one of the things or the event could not be found.
     */
    @POST
    @Path("{id}/unregisterMultiple")
    public Response unregisterFromEvents(@PathParam("id") long observerId, Collection<RegisterRequest> requests) throws DatasourceFindException {
        for (RegisterRequest request : requests) {
            assertPermitted(observerId, ThingPermission.EXECUTE);
            this.logic.unregisterFromEvent(observerId, request.getTargetThingID(), request.getTargetEventName());
        }
        return Response.noContent().build();
    }

    /**
     * Share a thing with another user.
     *
     * @param thingId
     *            The id of the thing to be shared.
     * @param shareRequest
     *            the share request
     * @throws DatasourceFindException
     *             the datasource find exception
     * @throws DatasourceInsertException
     *             the datasource insert exception
     */
    @POST
    @Path("{id}/share")
    public void share(@PathParam("id") long thingId, ShareRequest shareRequest) throws DatasourceFindException, DatasourceInsertException {
        if (shareRequest != null) {
            assertPermitted(thingId, ThingPermission.SHARE);
            this.logic.share(thingId, shareRequest.getUserId(), shareRequest.getPermission());
        } else {
            throw new BadRequestException("An empty request is not allowed");
        }
    }

    /**
     * Share a thing with another user.
     *
     * @param thingId
     *            The id of the thing to be unshared.
     * @param shareRequest
     *            the share request
     * @throws DatasourceFindException
     *             the datasource find exception
     * @throws DatasourceDeleteException
     *             When deleting the entry failed.
     */
    @POST
    @Path("{id}/unshare")
    public void unshare(@PathParam("id") long thingId, ShareRequest shareRequest) throws DatasourceFindException, DatasourceDeleteException {
        if (shareRequest != null) {
            assertPermitted(thingId, ThingPermission.SHARE);
            this.logic.unshare(thingId, shareRequest.getUserId(), shareRequest.getPermission());
        } else {
            throw new BadRequestException("An empty request is not allowed");
        }
    }

    /**
     * Gets all the users and their permissions for a given thing. The result is a JSON object that contains the user IDs as keys and arrays
     * of their permissions as values.
     *
     * @param thingId
     *            The id to request the permissions for.
     * @return The permissions of users for the thing.
     * @throws DatasourceFindException
     *             the datasource find exception
     */
    @GET
    @Path("{id}/sharedWith")
    public Map<Long, Set<ThingPermission>> sharedWith(@PathParam("id") long thingId) throws DatasourceFindException {
        assertPermitted(thingId, ThingPermission.SHARE);
        return this.logic.getThingUserPermissions(thingId);
    }

    /**
     * Gets all the users and their permissions for a given thing. The result is a JSON object that contains full users as keys and arrays
     * of their permissions as values.
     *
     * @param thingId
     *            The id to request the permissions for.
     * @return The permissions of users for the thing.
     * @throws DatasourceFindException
     *             the datasource find exception
     * @throws GetUserException
     *             the get user exception
     */
    @GET
    @Path("{id}/sharedWithUsers")
    public Collection<Entry<User, Set<ThingPermission>>> sharedWithUser(@PathParam("id") long thingId) throws DatasourceFindException, GetUserException {
        assertPermitted(thingId, ThingPermission.SHARE);
        return this.logic.getThingUserPermissionsFullUser(thingId);
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
