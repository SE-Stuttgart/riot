package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.util.Collection;
import java.util.Date;

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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.commons.ShareRequest;
import de.uni_stuttgart.riot.thing.commons.ThingPermission;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.usermanagement.logic.exception.permission.GetPermissionException;
import de.uni_stuttgart.riot.usermanagement.logic.exception.user.GetUserException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Permission;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.rest.RegisterRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterThingRequest;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;

/**
 * The thing service will handle any access (create, read, update, delete) to the "things".
 */
@Path("things")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiresAuthentication
public class ThingService {

    /** Default format for serialization. */
    protected static final String PRODUCED_FORMAT = MediaType.APPLICATION_JSON;

    /** Default format for consumption. */
    protected static final String CONSUMED_FORMAT = MediaType.APPLICATION_JSON;

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
    @Produces(PRODUCED_FORMAT)
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
    @Produces(PRODUCED_FORMAT)
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
    @Produces(PRODUCED_FORMAT)
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
    @Produces(PRODUCED_FORMAT)
    public Collection<Thing> get(@QueryParam("offset") int offset, @QueryParam("limit") int limit) throws DatasourceFindException {
        if (limit < 0 || offset < 0) {
            throw new BadRequestException("please provide valid parameter values");
        }

        // Fetch the results
        if (limit == 0) {
            return logic.findThings(offset, DEFAULT_PAGE_SIZE, ThingPermission.READ);
        } else {
            return logic.findThings(offset, limit, ThingPermission.READ);
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
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
    public Collection<Thing> getBy(FilteredRequest request) throws DatasourceFindException {
        if (request == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        return logic.findThings(request, ThingPermission.READ);
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
    @Consumes(CONSUMED_FORMAT)
    @Produces(PRODUCED_FORMAT)
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
            umFacade.addNewPermissionToUser(ownerId, new Permission(ThingPermission.FULL.buildShiroPermission(thing.getId())));
        } catch (GetPermissionException e) {
            throw new DatasourceInsertException(e);
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
    @Consumes(CONSUMED_FORMAT)
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
     * @param shareRequest
     *            the share request
     * @throws DatasourceFindException
     *             the datasource find exception
     * @throws GetPermissionException
     *             the get permission exception
     * @throws GetUserException
     *             the get user exception
     * @throws DatasourceInsertException
     *             the datasource insert exception
     */
    @POST
    @Path("share")
    public void share(ShareRequest shareRequest) throws DatasourceFindException, GetPermissionException, GetUserException, DatasourceInsertException {
        if (shareRequest != null) {
            assertPermitted(shareRequest.getThingId(), ThingPermission.SHARE);
            this.logic.share(shareRequest.getThingId(), shareRequest.getUserId(), shareRequest.getPermission());
        } else {
            throw new BadRequestException("An empty request is not allowed");
        }
    }

    /**
     * Called by the executing thing to raise an event.
     * 
     * @param eventInstance
     *            The event instance.
     * @return Nothing.
     * @throws DatasourceFindException
     *             If the corresponding thing or its event could not be found.
     */
    @POST
    @Path("notify")
    public Response notifyEvent(EventInstance eventInstance) throws DatasourceFindException {
        assertPermitted(eventInstance.getThingId(), ThingPermission.EXECUTE);
        this.logic.fireEvent(eventInstance);
        return Response.noContent().build();
    }

    /**
     * See {@link ThingLogic#submitAction(ActionInstance)}.
     * 
     * @param actionInstance
     *            The action instance.
     * 
     * @throws DatasourceFindException
     *             If the thing was not found.
     */
    @POST
    @Path("action")
    public void submitAction(ActionInstance actionInstance) throws DatasourceFindException {
        assertPermitted(actionInstance.getThingId(), ThingPermission.CONTROL);
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
     * Checks if the user has the permission which is build from the given parts. Throws a {@link ForbiddenException} if the user is not
     * permitted.
     *
     * @param parts
     *            the parts
     * @return true, if is permitted. If not permitted a {@link ForbiddenException} will be thrown.
     */
    private void assertPermitted(long id, ThingPermission permission) {
        if (!isPermitted(id, permission)) {
            throw new ForbiddenException("The user is not permitted to do the desired action");
        }
    }

    /**
     * Checks if the user is permitted.
     *
     * @param parts
     *            the parts
     * @return true, if is permitted
     */
    private boolean isPermitted(long id, ThingPermission permission) {
        return SecurityUtils.getSubject().isPermitted(permission.buildShiroPermission(id));
    }

}
