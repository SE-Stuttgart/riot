package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.commons.ShareRequest;
import de.uni_stuttgart.riot.thing.commons.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.RegisterRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterThingRequest;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;

/**
 * Rest Client for handling {@link Thing} operations.
 */
public class ThingClient {

    private static final String PREFIX = "/api/v1/";
    private static final String THINGS_PREFIX = PREFIX + "things/";

    private static final String POST_ADD_THING = PREFIX + "things";
    private static final String POST_SUBMIT_ACTION = THINGS_PREFIX + "action";
    private static final String POST_NOTIFY_EVENT = THINGS_PREFIX + "notify";

    private static final String GET_THING = THINGS_PREFIX;
    private static final String GET_UPDATES_SUFFIX = "/updates";
    private static final String GET_STATE_SUFFIX = "/state";

    private static final String DELETE_THING = THINGS_PREFIX;

    private static final String POST_UNREGISTER_EVENT_SUFFIX = "/unregister";
    private static final String POST_REGISTER_EVENT_SUFFIX = "/register";
    private static final String POST_UNREGISTER_EVENTS_SUFFIX = "/unregisterMultiple";
    private static final String POST_REGISTER_EVENTS_SUFFIX = "/registerMultiple";

    private static final String GET_LAST_ONLINE_SUFFIX = "/online";

    private static final String SHARE_THING = THINGS_PREFIX + "share";

    private static final long TEN_MIN = 1000 * 60 * 10;
    private static final long FIVE_MIN = 1000 * 60 * 5;

    /** The login client for authentication handling. */
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
     * Registers a new {@link Thing} with the server and returns it.
     * 
     * @param name
     *            The name of the new thing.
     * @param thingType
     *            The type of the new thing.
     * @param initialState
     *            The initial state for the thing, i.e., its property values. This may be <tt>null</tt>.
     * @param behaviorFactory
     *            The behavior factory that will provide the factory for the new thing.
     * @return The newly registered thing (with its ID set, etc.).
     * @throws RequestException
     *             If Thing could not be added.
     */
    public Thing registerNewThing(String name, String thingType, ThingState initialState, ThingBehaviorFactory<?> behaviorFactory) throws RequestException {
        RegisterThingRequest request = new RegisterThingRequest();
        request.setName(name);
        request.setType(thingType);
        request.setInitialState(initialState);

        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + POST_ADD_THING, request);
        try {
            JsonNode node = this.loginClient.getJsonMapper().readTree(response.getEntity().getContent());
            return readThing(node, behaviorFactory);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the Thing with the given id.
     * 
     * @param id
     *            The ID of the thing.
     * @param behaviorFactory
     *            The behavior factory for the thing.
     * @return The Thing
     * @throws RequestException
     *             When the request to the server failed.
     */
    public Thing getExistingThing(long id, ThingBehaviorFactory<?> behaviorFactory) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_THING + id);
        try {
            JsonNode node = this.loginClient.getJsonMapper().readTree(response.getEntity().getContent());
            return readThing(node, behaviorFactory);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns all Things that are assigned to the logged in user.
     * 
     * @param behaviorFactory
     *            The behavior factory for the thing.
     * @return The Thing
     * @throws RequestException
     *             When the request to the server failed.
     */
    public Collection<Thing> getThings(ThingBehaviorFactory<?> behaviorFactory) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + THINGS_PREFIX);
        try {
            JsonNode node = this.loginClient.getJsonMapper().readTree(response.getEntity().getContent());
            return readThings(node, behaviorFactory);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    private Collection<Thing> readThings(JsonNode node, ThingBehaviorFactory<?> behaviorFactory) throws JsonProcessingException {
        ArrayList<Thing> result = new ArrayList<Thing>();
        Iterator<JsonNode> i = node.elements();
        while (i.hasNext()) {
            result.add(this.readThing(i.next(), behaviorFactory));
        }
        return result;
    }

    /**
     * Reads a thing in the JSON format created by the ThingConverter on the server and instantiates it locally using the given behavior
     * factory.
     * 
     * @param node
     *            The JSON node to read from.
     * @param behaviorFactory
     *            The behavior factory.
     * @throws JsonProcessingException
     *             When parsing the JSON fails.
     */
    private Thing readThing(JsonNode node, ThingBehaviorFactory<?> behaviorFactory) throws JsonProcessingException {
        if (!node.isObject()) {
            throw new JsonMappingException("Expected a JSON node!");
        }

        // Create the thing itself.
        Thing thing = ThingFactory.create(node.get("type").asText(), node.get("id").asLong(), node.get("name").asText(), behaviorFactory);

        // Restore its state from the JSON object.
        // TODO We should be able to remove this part and replace it by ThingState?
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String propertyName = fieldNames.next();
            Property<?> property = thing.getProperty(propertyName);
            if (property != null) {
                Object value = this.loginClient.getJsonMapper().treeToValue(node.get(propertyName), property.getValueType());
                ThingState.silentSetThingProperty(property, value);
            }
        }

        return thing;
    }

    /**
     * Unregisters the thing with the given thingID.
     * 
     * @param thingID
     *            ID of thing to be deleted
     * @throws RequestException
     *             When the request failed.
     */
    public void unregisterThing(long thingID) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + DELETE_THING + thingID);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Reads the state of the thing as returned by the ThingConverter.
     * 
     * @param id
     *            The ID of the thing.
     * @return The current state of the thing (according to the server, at least).
     * @throws RequestException
     *             When retrieving the state fails.
     */
    public ThingState getThingState(long id) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + THINGS_PREFIX + id + GET_STATE_SUFFIX);
        try {
            return this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), ThingState.class);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Submits an {@link ActionInstance} to the server. Hint: If you get 500 "Request failed" exceptions from this method, check if the
     * respective subclass of {@link ActionInstance} provides a Jackson-compatible constructor!
     * 
     * @param actionInstance
     *            the action to be submitted
     * @throws RequestException
     *             if Action instance could not be submitted.
     */
    public void submitAction(ActionInstance actionInstance) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + POST_SUBMIT_ACTION, actionInstance);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Notifies the server about occurred event. Hint: If you get 500 "Request failed" exceptions from this method, check if the respective
     * subclass of {@link EventInstance} provides a Jackson-compatible constructor!
     * 
     * @param eventInstance
     *            the eventInstance
     * @throws RequestException
     *             if Thing could not be added.
     */
    public void notifyEvent(EventInstance eventInstance) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + POST_NOTIFY_EVENT, eventInstance);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Registers to an event.
     * 
     * @param observerId
     *            The ID of the thing that wants to register to the given event.
     * @param request
     *            the register request
     * @throws RequestException
     *             the request exception
     */
    public void registerToEvent(long observerId, RegisterRequest request) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + THINGS_PREFIX + observerId + POST_REGISTER_EVENT_SUFFIX, request);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Registers to multiple events.
     *
     * @param observerId
     *            The ID of the thing that wants to register to the given event.
     * @param requests
     *            the register requests
     * @throws RequestException
     *             the request exception
     */
    public void registerToEvents(long observerId, Collection<RegisterRequest> requests) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + THINGS_PREFIX + observerId + POST_REGISTER_EVENTS_SUFFIX, requests);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Unregisters from an event.
     *
     * @param observerId
     *            The ID of the thing that wants to register to the given event.
     * @param request
     *            the request to unregister
     * @throws RequestException
     *             the request exception
     */
    public void unregisterFromEvent(long observerId, RegisterRequest request) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + THINGS_PREFIX + observerId + POST_UNREGISTER_EVENT_SUFFIX, request);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Unregisters from multiple events.
     *
     * @param observerId
     *            The ID of the thing that wants to register to the given event.
     * @param requests
     *            the requests to unregister
     * @throws RequestException
     *             the request exception
     */
    public void unregisterFromEvents(long observerId, Collection<RegisterRequest> requests) throws RequestException {
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + THINGS_PREFIX + observerId + POST_UNREGISTER_EVENTS_SUFFIX, requests);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Gets all updates from server for the Thing with id thingID.
     * 
     * @param thingID
     *            thing id
     * @return All updates that happened since the last call.
     * @throws RequestException
     *             if it could not get the action instances.
     */
    public ThingUpdatesResponse getUpdates(long thingID) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + THINGS_PREFIX + thingID + GET_UPDATES_SUFFIX);
        try {
            return this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), ThingUpdatesResponse.class);
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Gets the last time the Thing with thingID was online.
     *
     * @param thingID
     *            the thing id
     * @return the last online
     * @throws RequestException
     *             the request exception
     */
    public Date getLastOnline(long thingID) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + THINGS_PREFIX + thingID + GET_LAST_ONLINE_SUFFIX);
        try {
            Date result = this.loginClient.getJsonMapper().readValue(response.getEntity().getContent(), Date.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the current online state for the given thing.
     * 
     * @param thingID
     *            the thing id
     * @return the current online state
     * @throws RequestException .
     */
    public OnlineState getOnlineState(long thingID) throws RequestException {
        long now = System.currentTimeMillis();
        Date lastOnline = this.getLastOnline(thingID);
        if (lastOnline.before(new Date(now - TEN_MIN))) {
            return OnlineState.STATUS_OFFLINE;
        } else if (lastOnline.before(new Date(now - FIVE_MIN))) {
            return OnlineState.STATUS_AWAY;
        } else {
            return OnlineState.STATUS_ONLINE;
        }
    }

    /**
     * Share a thing with another user.
     *
     * @param thingID
     *            the id of the thing to be shared
     * @param userID
     *            the id of the user to share with
     * @param permission
     *            the permission
     * @throws RequestException
     *             the request exception
     */
    public void share(long thingID, long userID, ThingPermission permission) throws RequestException {
        ShareRequest sr = new ShareRequest(userID, thingID, permission);
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + SHARE_THING, sr);
        try {
            if (response.getEntity() != null) {
                response.getEntity().consumeContent();
            }
        } catch (IOException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Gets the JSON mapper.
     * 
     * @return The Jackson ObjectMapper.
     */
    public ObjectMapper getJsonMapper() {
        return loginClient.getJsonMapper();
    }

}
