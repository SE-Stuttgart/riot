package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;
import java.util.Collection;
import java.util.Queue;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.type.TypeReference;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;

/**
 * Rest Client for handling {@link Thing} operations.
 *
 */
public class ThingClient {
    private static final String PREFIX = "/api/v1/";

    private static final String POST_ADD_THING = PREFIX + "thing";
    private static final String POST_SUBMIT_ACTION = PREFIX + "thing/action";
    private static final String POST_NOTIFY_EVENT = PREFIX + "thing/event";

    private static final String GET_THINGS = PREFIX + "thing";
    private static final String GET_THING = PREFIX + "thing/";
    private static final String GET_ACTIONS = PREFIX + "/thing/action/";
    private static final String GET_EVENTS = PREFIX + "/thing/event/";

    private static final String PUT_UPDATE_THING = PREFIX + "thing/";

    private static final String DELETE_THING = PREFIX + "thing/";

    private final LoginClient loginClient;

    /**
     * Constructor.
     * 
     * @param loginClient
     *            the {@link LoginClient} to be used
     */
    public ThingClient(LoginClient loginClient) {
        this.loginClient = loginClient;
    }

    /**
     * registers a {@link RemoteThing}.
     * 
     * @param thing
     *            the Thing to be added
     * @return the added Thing
     * @throws RequestException
     *             if Thing could not be added.
     */
    public RemoteThing registerThing(RemoteThing thing) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + POST_ADD_THING, thing);
        try {
            RemoteThing result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), RemoteThing.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the Thing with the given id.
     * 
     * @param id
     *            user id
     * @return the Thing
     * @throws RequestException .
     */
    public RemoteThing getThing(long id) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_THING + id);
        try {
            RemoteThing result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), RemoteThing.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns all things.
     * 
     * @return collection of all things.
     * @throws RequestException .
     */
    public Collection<RemoteThing> getThings() throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_THINGS);
        try {
            Collection<RemoteThing> result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Collection<RemoteThing>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Updates the Thing with id thingID to the given values in the thing object.
     * 
     * @param thingID
     *            id of Thing to be updated
     * @param thing
     *            new values for the Thing
     * @return http code (204 NO CONTENT)
     * @throws RequestException .
     */
    public int updateThing(long thingID, RemoteThing thing) throws RequestException {
        HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl() + PUT_UPDATE_THING + thingID, thing);
        int result = response.getStatusLine().getStatusCode();
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
        return result;
    }

    /**
     * deregisters the Thing with id thingID.
     * 
     * @param thingID
     *            id of thing to be deleted
     * @return http code (200 OK)
     * @throws RequestException .
     */
    public int deregisterThing(long thingID) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + DELETE_THING + thingID);
        int result = response.getStatusLine().getStatusCode();
        return result;
    }

    /**
     * gets all action instances from server for the Thing with id thingID.
     * 
     * @param thingID
     *            thing id
     * @return queue of action instances
     * @throws RequestException
     *             if it could not get the action instances.
     */
    public Queue<ActionInstance> getActionInstances(long thingID) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_ACTIONS + thingID);
        try {
            Queue<ActionInstance> result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Queue<ActionInstance>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * gets all event instances from server for the Thing with id thingID.
     * 
     * @param thingID
     *            thing id
     * @return stack of event instances
     * @throws RequestException
     *             if it could not get the event instances.
     */
    public Stack<EventInstance> getEventInstances(long thingID) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_EVENTS + thingID);
        try {
            Stack<EventInstance> result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Stack<EventInstance>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Submits an {@link ActionInstance} to the server.
     * 
     * @param actionInstance
     *            the action to be submitted
     * @throws RequestException
     *             if Action instance could not be submitted.
     */
    public void submitActionInstance(ActionInstance actionInstance) throws RequestException {
        this.loginClient.post(this.loginClient.getServerUrl() + POST_SUBMIT_ACTION, actionInstance);
    }

    /**
     * Notifies the server about occurred event.
     * 
     * @param eventInstance
     *            the eventInstance
     * @throws RequestException
     *             if Thing could not be added.
     */
    public void notifyEvent(EventInstance eventInstance) throws RequestException {
        this.loginClient.post(this.loginClient.getServerUrl() + POST_NOTIFY_EVENT, eventInstance);
    }

    // TODO register
    // TODO deregister

}
