package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.rest.MultipleEventsRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterEventRequest;
import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingMetainfo;
import de.uni_stuttgart.riot.thing.rest.ThingInformation.Field;
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
     * Maps a thing with the given fields.
     * 
     * @param thing
     *            The thing to be mapped.
     * @param fields
     *            The fields to include. May be empty or null.
     * @param defaultFields
     *            This parameter replaces <tt>fields</tt>, if <tt>fields</tt> is empty.
     * @return The mapped thing information.
     */
    private ThingInformation mapThing(Thing thing, Collection<Field> fields, Collection<Field> defaultFields) {
        return logic.map(null, thing, (fields == null || fields.isEmpty()) ? defaultFields : fields);
    }

    /**
     * Creates a mapper function for {@link #mapThing(Thing, Collection, Collection)}.
     * 
     * @param fields
     *            The fields to include. May be empty or null.
     * @param defaultFields
     *            This parameter replaces <tt>fields</tt>, if <tt>fields</tt> is empty.
     * @return A mapper that does the same as {@link #mapThing(Thing, Collection, Collection)}.
     */
    private Function<Thing, ThingInformation> thingMapper(Collection<Field> fields, Collection<Field> defaultFields) {
        Collection<Field> effectiveFields = (fields == null || fields.isEmpty()) ? defaultFields : fields;
        return (thing) -> logic.map(null, thing, effectiveFields);
    }

    /**
     * Gets information about a thing. This is a JSON object containing the thing's type, name, id and state.
     *
     * @param id
     *            The id of the thing.
     * @param fields
     *            Note that the actual parameter name is <tt>return</tt>. You may specify this parameter multiple times. It determines the
     *            fields to be returned, see {@link ThingInformation}. The default is to only return the meta-info.
     * @return The thing if it exists, HTTP 404 otherwise
     */
    @GET
    @Path("{id}")
    public ThingInformation getThingInformation(@PathParam("id") long id, @QueryParam("return") List<Field> fields) {
        assertPermitted(id, ThingPermission.READ);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            return mapThing(thing, fields, EnumSet.of(Field.METAINFO));
        }
    }

    /**
     * Returns the meta-info of the thing, that is, the values of the base fields in {@link Thing}.
     * 
     * @param id
     *            The id of the thing.
     * @return The state.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @GET
    @Path("{id}/metainfo")
    public ThingMetainfo getThingMetainfo(@PathParam("id") long id) throws DatasourceFindException {
        assertPermitted(id, ThingPermission.READ);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            return ThingMetainfo.create(thing);
        }
    }

    /**
     * Changes the meta-info of the thing. Note that the caller needs to be the owner of the thing in order to change the anything.
     * 
     * @param id
     *            The id of the thing.
     * @param metainfo
     *            The new meta-info.
     * @return The new meta-info (some parts may have been ignored).
     * @throws DatasourceFindException
     *             If the thing does not exist.
     * @throws DatasourceUpdateException
     *             When storing the information fails.
     */
    @PUT
    @Path("{id}/metainfo")
    public ThingMetainfo setThingMetainfo(@PathParam("id") long id, ThingMetainfo metainfo) throws DatasourceFindException, DatasourceUpdateException {
        assertOwner(id);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            logic.setMetainfo(thing, metainfo);
            return ThingMetainfo.create(thing);
        }
    }

    /**
     * Returns the state of the thing, that is, the value of its properties.
     * 
     * @param id
     *            The id of the thing.
     * @return The state.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @GET
    @Path("{id}/state")
    public ThingState getThingState(@PathParam("id") long id) throws DatasourceFindException {
        assertPermitted(id, ThingPermission.READ);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            return ThingState.create(thing);
        }
    }

    /**
     * Changes the state of the thing, that is, the value of its properties. Note that this does not fire any events!!
     * 
     * @param state
     *            The state to be set.
     * @param id
     *            The id of the thing.
     * @return The new overall state.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @PUT
    @Path("{id}/state")
    public ThingState getThingState(@PathParam("id") long id, ThingState state) throws DatasourceFindException {
        assertPermitted(id, ThingPermission.EXECUTE);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            state.apply(thing);
            return ThingState.create(thing);
        }
    }

    /**
     * Returns the structural description of the thing.
     * 
     * @param id
     *            The id of the thing.
     * @return The description.
     * @throws DatasourceFindException
     *             If the thing does not exist.
     */
    @GET
    @Path("{id}/description")
    public ThingDescription getThingDescription(@PathParam("id") long id) throws DatasourceFindException {
        assertPermitted(id, ThingPermission.READ);
        Thing thing = logic.getThing(id);
        if (thing == null) {
            throw new NotFoundException();
        } else {
            return ThingDescription.create(thing);
        }
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
    @Path("{id}/lastconnection")
    public Date lastConnection(@PathParam("id") long id) throws DatasourceFindException {
        assertPermitted(id, ThingPermission.READ);
        return this.logic.getLastConnection(id);
    }

    /**
     * Gets the collection for resources.
     *
     * @param offset
     *            the beginning item number
     * @param limit
     *            maximum number of items to return
     * @param fields
     *            Note that the actual parameter name is <tt>return</tt>. You may specify this parameter multiple times. It determines the
     *            fields to be returned, see {@link ThingInformation}. The default is to only return the meta-info.
     * @return the collection. If the both parameters are 0, it returns at maximum 20 elements.
     * 
     * @throws DatasourceFindException
     *             Exception on initialization of things.
     */
    @GET
    public Collection<ThingInformation> get(@QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("return") List<Field> fields) throws DatasourceFindException {
        if (limit < 0 || offset < 0) {
            throw new BadRequestException("please provide valid parameter values");
        }

        // Fetch the results
        return logic.findThings(offset, limit == 0 ? DEFAULT_PAGE_SIZE : limit, null, ThingPermission.READ) //
                .map(thingMapper(fields, EnumSet.of(Field.METAINFO))) //
                .collect(Collectors.toList());
    }

    /**
     * Gets all things of the given type that the current user has the given permissions on.
     * 
     * @param type
     *            The type of the wanted things (fully qualified Java class name). If <tt>null</tt>, all types will be returned.
     * @param requiredPermissions
     *            Note that the actual paramter name is <tt>requiresPermission</tt>. The permissions that are (all) required on the returned
     *            things. Note that the {@link ThingPermission#READ} permission is always required.
     * @param fields
     *            Note that the actual parameter name is <tt>return</tt>. You may specify this parameter multiple times. It determines the
     *            fields to be returned, see {@link ThingInformation}. The default is to only return the meta-info.
     * @return The matching things.
     */
    @GET
    @Path("/find")
    public Collection<ThingInformation> get(@QueryParam("type") String type, @QueryParam("requiresPermission") Set<ThingPermission> requiredPermissions, @QueryParam("return") List<Field> fields) {
        Class<? extends Thing> classType = null;
        if (type != null && !type.isEmpty()) {
            try {
                classType = Class.forName(type).asSubclass(Thing.class);
            } catch (ClassNotFoundException e) {
                throw new BadRequestException(type + " is not a class!");
            } catch (ClassCastException e) {
                throw new BadRequestException(type + " is not a subclass of Thing!");
            }
        }

        EnumSet<ThingPermission> permissions = EnumSet.of(ThingPermission.READ);
        permissions.addAll(requiredPermissions);
        return logic.findThings(classType, null, permissions) //
                .map(thingMapper(fields, EnumSet.of(Field.METAINFO))) //
                .collect(Collectors.toList());
    }

    /**
     * Creates a new model with data from the request body.
     *
     * @param request
     *            object specifying the filter attributes (pagination also possible)
     * @param fields
     *            Note that the actual parameter name is <tt>return</tt>. You may specify this parameter multiple times. It determines the
     *            fields to be returned, see {@link ThingInformation}. The default is to only return the meta-info.
     * @return collection containing elements that applied to filter
     * @throws DatasourceFindException
     *             when retrieving the data fails
     */
    // FIXME: should use query attribute filtering like the base resource
    @POST
    @Path("/filter")
    public Collection<ThingInformation> getBy(FilteredRequest request, @QueryParam("return") List<Field> fields) throws DatasourceFindException {
        if (request == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        return logic.findThings(request, null, ThingPermission.READ) //
                .map(thingMapper(fields, EnumSet.of(Field.METAINFO))) //
                .collect(Collectors.toList());
    }

    /**
     * Registers a new thing and returns the created thing.
     *
     * @param request
     *            The request data for creating the thing. Only some of the meta-info and the state will be used!
     * @param fields
     *            Note that the actual parameter name is <tt>return</tt>. You may specify this parameter multiple times. It determines the
     *            fields to be returned, see {@link ThingInformation}. The default is to only return the meta-info.
     * @return An HTTP created (201) response if successful
     * @throws DatasourceInsertException
     *             When insertion failed
     */
    @POST
    @RequiresPermissions("thing:create")
    public Response registerNewThing(ThingInformation request, @QueryParam("return") List<Field> fields) throws DatasourceInsertException {
        if (request == null) {
            throw new BadRequestException("Please provide an entity in the request body.");
        } else if (request.getMetainfo() == null) {
            throw new BadRequestException("The request must contain a metainfo field!");
        }

        // Check the metainfo.
        ThingMetainfo metainfo = request.getMetainfo();
        if (metainfo.getParentId() != null) {
            if (metainfo.getParentId() == 0) {
                metainfo.setParentId(null);
            } else {
                assertPermitted(metainfo.getParentId(), ThingPermission.READ);
            }
        }

        // Find out the owner (which is the current user).
        UserManagementFacade umFacade = UserManagementFacade.getInstance();
        metainfo.setOwnerId(umFacade.getCurrentUserId());

        // Register the thing.
        Thing thing = this.logic.registerThing(request.getType(), metainfo);

        // Give the owner full access.
        try {
            this.logic.addOrUpdateShare(thing.getId(), new ThingShare(metainfo.getOwnerId(), EnumSet.allOf(ThingPermission.class)));
        } catch (DatasourceFindException | DatasourceDeleteException e) {
            throw new RuntimeException("The thing just inserted is not there!", e);
        }

        // Fill with initial values provided by the client.
        if (request.getState() != null) {
            request.getState().apply(thing);
        }

        ThingInformation info = mapThing(thing, fields, EnumSet.of(Field.METAINFO, Field.STATE));
        return Response.created(getUriForThing(thing)).entity(info).build();
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
        if (thingId != eventInstance.getThingId()) {
            throw new IllegalArgumentException("Mismatching Thing ID!");
        }
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
        if (thingId != actionInstance.getThingId()) {
            throw new IllegalArgumentException("Mismatching thing ID!");
        }
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
                throw new ForbiddenException("The user is not permitted to perform the desired action");
            }
        } catch (DatasourceFindException e) {
            throw new NotFoundException(e);
        }
    }

    /**
     * Checks if the user is the owner of the given thing. Throws a {@link ForbiddenException} if not.
     * 
     * @param id
     *            The id of the thing.
     */
    private void assertOwner(long id) {
        try {
            if (!logic.isOwner(id, null)) {
                throw new ForbiddenException("The user is not permitted to perform the desired action");
            }
        } catch (DatasourceFindException e) {
            throw new NotFoundException(e);
        }
    }

}
