package de.uni_stuttgart.riot.rest;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.Stack;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Token;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.db.RemoteThingSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
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
     * @throws SQLException .
     * @throws NamingException .
     */
    public ThingService() {
        super(new RemoteThingSqlQueryDAO());
        this.logic = new ThingLogic();
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
    public Stack<EventInstance> getEventInstances(@PathParam("id") long id) {
        return this.logic.getRegisteredEvents(id);
    }

    @POST
    @Path("event")
    public Response registerOnEvent(RegisterRequest request) {
        this.logic.registerOnEvent(request.getThingId(), request.getRegisterOnThingId(), request.getEvent());
        return Response.noContent().build();
    }

    @DELETE
    @Path("event")
    public Response deRegisterOnEvent(RegisterRequest request) {
        this.logic.deRegisterOnEvent(request.getThingId(), request.getRegisterOnThingId(), request.getEvent());
        return Response.noContent().build();
    }

    @POST
    @Path("event")
    public Response notifyEvent(EventInstance eventInstance) {
        this.logic.submitEvent(eventInstance);
        return Response.noContent().build();
    }

    @POST
    @Path("action")
    public void submitAction(ActionInstance actionInstance) {
        this.logic.submitAction(actionInstance);
    }
}
