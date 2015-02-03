package de.uni_stuttgart.riot.rest;

import java.net.URI;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.Stack;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;
import de.uni_stuttgart.riot.thing.commons.RegisterRequest;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.usermanagement.exception.UserManagementException;
import de.uni_stuttgart.riot.usermanagement.service.facade.UserManagementFacade;

/**
 * The thing service will handle any access (create, read, update, delete) to the "things".
 *
 */
@Path("thing")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ThingService extends BaseResource<RemoteThing> {

    private final ThingLogic logic;

    /**
     * Default Constructor.
     * 
     * @throws DatasourceFindException
     * 
     * @throws SQLException .
     * @throws NamingException .
     */
    public ThingService() throws DatasourceFindException {
        super(new RemoteThingSqlQueryDAO());
        this.logic = ThingLogic.getThingLogic();
    }

    @Override
    public void init(RemoteThing storable) throws Exception {
        this.logic.completeRemoteThing(storable);
    }

    @GET
    @Path("/online/{id}")
    public Timestamp lastConnection(@PathParam("id") long id) {
        return this.logic.lastConnection(id);
    }

    // FIXME switch from id to something secure.
    @GET
    @Path("/action/{id}")
    public Queue<ActionInstance> getActionInstances(@PathParam("id") long id) throws DatasourceFindException {
        return this.logic.getCurrentActionInstances(id);
    }

    @GET
    @Path("event/{id}")
    public Stack<EventInstance> getEventInstances(@PathParam("id") long id) throws DatasourceFindException {
        return this.logic.getRegisteredEvents(id);
    }

    @POST
    @Path("register")
    public Response registerOnEvent(RegisterRequest request) throws DatasourceFindException {
        this.logic.registerOnEvent(request.getThingId(), request.getRegisterOnThingId(), request.getEvent());
        return Response.noContent().build();
    }

    @POST
    @Path("deregister")
    public Response deRegisterOnEvent(RegisterRequest request) throws DatasourceFindException {
        this.logic.deRegisterOnEvent(request.getThingId(), request.getRegisterOnThingId(), request.getEvent());
        return Response.noContent().build();
    }

    @POST
    @Path("event")
    public Response notifyEvent(EventInstance eventInstance) throws DatasourceFindException {
        this.logic.submitEvent(eventInstance);
        return Response.noContent().build();
    }

    @POST
    @Path("action")
    public void submitAction(ActionInstance actionInstance) throws DatasourceFindException {
        this.logic.submitAction(actionInstance);
    }

    /**
     * Overrides create of baseresource.
     */
    @POST
    public Response create(RemoteThing model) throws DatasourceInsertException {
        if (model == null) {
            throw new BadRequestException("please provide an entity in the request body.");
        }
        this.logic.registerThing(model);
        URI relative = getUriForModel(model);
        return Response.created(relative).entity(model).build();
    }

    /**
     * Overrides update of baseresource.
     */
    @PUT
    @Path("{id}")
    public Response update(@PathParam("id") long id, RemoteThing model) {
        return Response.status(Status.MOVED_PERMANENTLY).build();
    }

    /**
     * Overrides delete of baseresource.
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") long id) {
        try {
            this.logic.unregisterThing(id);
        } catch (DatasourceDeleteException exception) {
            throw new NotFoundException("No such resource exists or it has already been deleted.", exception);
        }
        return Response.noContent().build();
    }

}
