package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.clientlibrary.BaseClient;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
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
public class ThingClient extends BaseClient {

    private static final String THINGS_PREFIX = "things/";

    private static final String POST_ADD_THING = "things";
    private static final String POST_SUBMIT_ACTION = "/action";
    private static final String POST_NOTIFY_EVENT = "/notify";

    private static final String GET_THING = THINGS_PREFIX;
    private static final String GET_UPDATES_SUFFIX = "/updates";
    private static final String GET_STATE_SUFFIX = "/state";

    private static final String DELETE_THING = THINGS_PREFIX;

    private static final String POST_UNREGISTER_EVENT_SUFFIX = "/unregister";
    private static final String POST_REGISTER_EVENT_SUFFIX = "/register";
    private static final String POST_UNREGISTER_EVENTS_SUFFIX = "/unregisterMultiple";
    private static final String POST_REGISTER_EVENTS_SUFFIX = "/registerMultiple";

    private static final String GET_LAST_ONLINE_SUFFIX = "/online";

    private static final String POST_SHARE_SUFFIX = "/share";
    private static final String POST_UNSHARE_SUFFIX = "/unshare";
    private static final String GET_PERMISSIONS_SUFFIX = "/sharedWith";

    private static final long TEN_MIN = 1000 * 60 * 10;
    private static final long FIVE_MIN = 1000 * 60 * 5;

    /**
     * Creates a new ThingClient.
     * 
     * @param connector
     *            The {@link ServerConnector} to be used.
     */
    public ThingClient(ServerConnector connector) {
        super(connector);
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
     *             If the Thing could not be added.
     * @throws IOException
     *             When a network error occured or the result format could not be read.
     */
    public Thing registerNewThing(String name, String thingType, ThingState initialState, ThingBehaviorFactory<?> behaviorFactory) throws RequestException, IOException {
        RegisterThingRequest request = new RegisterThingRequest();
        request.setName(name);
        request.setType(thingType);
        request.setInitialState(initialState);
        JsonNode node = getConnector().doPOST(POST_ADD_THING, request, JsonNode.class);
        return readThing(node, behaviorFactory);
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
     * @throws NotFoundException
     *             When the thing does not exist.
     * @throws IOException
     *             When a network error occured or the result format could not be read.
     */
    public Thing getExistingThing(long id, ThingBehaviorFactory<?> behaviorFactory) throws RequestException, IOException, NotFoundException {
        JsonNode node = getConnector().doGET(GET_THING + id, JsonNode.class);
        return readThing(node, behaviorFactory);
    }

    /**
     * Returns all Things that are assigned to the logged in user.
     * 
     * @param behaviorFactory
     *            The behavior factory for the thing.
     * @return The Thing
     * @throws RequestException
     *             When the request to the server failed.
     * @throws IOException
     *             When a network error occured or the result format could not be read.
     */
    public Collection<Thing> getThings(ThingBehaviorFactory<?> behaviorFactory) throws RequestException, IOException {
        try {
            JsonNode node = getConnector().doGET(THINGS_PREFIX, JsonNode.class);
            return readThings(node, behaviorFactory);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Reads all of the things just like {@link #readThing(JsonNode, ThingBehaviorFactory)} does.
     * 
     * @param node
     *            The JSON node to read from.
     * @param behaviorFactory
     *            The behavior factory.
     * @return A list of the things that were read.
     * @throws JsonProcessingException
     */
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
     * @return The thing.
     * @throws JsonProcessingException
     *             When parsing the JSON fails.
     */
    private Thing readThing(JsonNode node, ThingBehaviorFactory<?> behaviorFactory) throws JsonProcessingException {
        if (!node.isObject()) {
            throw new JsonMappingException("Expected a JSON node!");
        }

        // Create the thing itself.
        Thing thing = ThingFactory.create(node.get("type").asText(), node.get("id").asLong(), node.get("name").asText(), behaviorFactory).getThing();

        // Restore its state from the JSON object.
        // TODO We should be able to remove this part and replace it by ThingState?
        Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String propertyName = fieldNames.next();
            Property<?> property = thing.getProperty(propertyName);
            if (property != null) {
                Object value = getJsonMapper().treeToValue(node.get(propertyName), property.getValueType());
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
     * @throws IOException
     *             When a network error occured.
     */
    public void unregisterThing(long thingID) throws RequestException, IOException {
        getConnector().doDELETE(DELETE_THING + thingID);
    }

    /**
     * Reads the state of the thing as returned by the ThingConverter.
     * 
     * @param id
     *            The ID of the thing.
     * @return The current state of the thing (according to the server, at least).
     * @throws RequestException
     *             When retrieving the state fails.
     * @throws NotFoundException
     *             When the thing does not exist.
     * @throws IOException
     *             When a network error occured.
     */
    public ThingState getThingState(long id) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(THINGS_PREFIX + id + GET_STATE_SUFFIX, ThingState.class);
    }

    /**
     * Submits an {@link ActionInstance} to the server. Hint: If you get 500 "Request failed" exceptions from this method, check if the
     * respective subclass of {@link ActionInstance} provides a Jackson-compatible constructor!
     * 
     * @param actionInstance
     *            the action to be submitted
     * @throws RequestException
     *             if Action instance could not be submitted.
     * @throws IOException
     *             When a network error occured.
     */
    public void submitAction(ActionInstance actionInstance) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + actionInstance.getThingId() + POST_SUBMIT_ACTION, actionInstance);
    }

    /**
     * Notifies the server about occurred event. Hint: If you get 500 "Request failed" exceptions from this method, check if the respective
     * subclass of {@link EventInstance} provides a Jackson-compatible constructor!
     * 
     * @param eventInstance
     *            the eventInstance
     * @throws RequestException
     *             if Thing could not be added.
     * @throws IOException
     *             When a network error occured.
     */
    public void notifyEvent(EventInstance eventInstance) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + eventInstance.getThingId() + POST_NOTIFY_EVENT, eventInstance);
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
     * @throws IOException
     *             When a network error occured.
     */
    public void registerToEvent(long observerId, RegisterRequest request) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + observerId + POST_REGISTER_EVENT_SUFFIX, request);
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
     * @throws IOException
     *             When a network error occured.
     */
    public void registerToEvents(long observerId, Collection<RegisterRequest> requests) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + observerId + POST_REGISTER_EVENTS_SUFFIX, requests);
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
     * @throws IOException
     *             When a network error occured.
     */
    public void unregisterFromEvent(long observerId, RegisterRequest request) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + observerId + POST_UNREGISTER_EVENT_SUFFIX, request);
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
     * @throws IOException
     *             When a network error occured.
     */
    public void unregisterFromEvents(long observerId, Collection<RegisterRequest> requests) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + observerId + POST_UNREGISTER_EVENTS_SUFFIX, requests);
    }

    /**
     * Gets all updates from server for the Thing with id thingID.
     * 
     * @param thingID
     *            thing id
     * @return All updates that happened since the last call.
     * @throws RequestException
     *             if it could not get the action instances.
     * @throws NotFoundException
     *             When the thing does not exist.
     * @throws IOException
     *             When a network error occured.
     */
    public ThingUpdatesResponse getUpdates(long thingID) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(THINGS_PREFIX + thingID + GET_UPDATES_SUFFIX, ThingUpdatesResponse.class);
    }

    /**
     * Gets the last time the Thing with thingID was online.
     *
     * @param thingID
     *            the thing id
     * @return the last online
     * @throws RequestException
     *             the request exception
     * @throws NotFoundException
     *             When the thing does not exist.
     * @throws IOException
     *             When a network error occured.
     */
    public Date getLastOnline(long thingID) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(THINGS_PREFIX + thingID + GET_LAST_ONLINE_SUFFIX, Date.class);
    }

    /**
     * Returns the current online state for the given thing.
     * 
     * @param thingID
     *            the thing id
     * @return the current online state
     * @throws NotFoundException
     *             When the thing does not exist.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the online state could not be fetched.
     */
    public OnlineState getOnlineState(long thingID) throws RequestException, IOException, NotFoundException {
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
     * @throws IOException
     *             When a network error occured.
     */
    public void share(long thingID, long userID, ThingPermission permission) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + thingID + POST_SHARE_SUFFIX, new ShareRequest(userID, permission));
    }

    /**
     * Unshare a thing with another user.
     *
     * @param thingID
     *            the id of the thing to be shared
     * @param userID
     *            the id of the user to share with
     * @param permission
     *            the permission
     * @throws RequestException
     *             the request exception
     * @throws IOException
     *             When a network error occured.
     */
    public void unshare(long thingID, long userID, ThingPermission permission) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + thingID + POST_UNSHARE_SUFFIX, new ShareRequest(userID, permission));
    }

    /**
     * Gets all the users and their permissions for a given thing.
     * 
     * @param thingID
     *            The id of the thing.
     * @return A map where the keys are the users' ids and the values are sets of permissions that the respective user has.
     * @throws RequestException
     *             the request exception
     * @throws NotFoundException
     *             When the thing does not exist.
     * @throws IOException
     *             When a network error occured.
     */
    public Map<Long, Set<ThingPermission>> getThingUserPermissions(long thingID) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(THINGS_PREFIX + thingID + GET_PERMISSIONS_SUFFIX, new TypeReference<Map<Long, Set<ThingPermission>>>() {
        });
    }

    /**
     * Gets the JSON mapper.
     * 
     * @return The Jackson ObjectMapper.
     */
    public ObjectMapper getJsonMapper() {
        return getConnector().getJsonMapper();
    }

}
