package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.clientlibrary.BaseClient;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.clientlibrary.URIBuilder;
import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.rest.MultipleEventsRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterEventRequest;
import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingInformation.Field;
import de.uni_stuttgart.riot.thing.rest.ThingMetainfo;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
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
    private static final String GET_FIND = THINGS_PREFIX + "find";
    private static final String GET_UPDATES_SUFFIX = "/updates";

    private static final String DELETE_THING = THINGS_PREFIX;

    private static final String POST_UNREGISTER_EVENT_SUFFIX = "/unregister";
    private static final String POST_REGISTER_EVENT_SUFFIX = "/register";
    private static final String POST_MULTIPLE_EVENTS_SUFFIX = "/multipleEvents";

    private static final String GET_LAST_ONLINE_SUFFIX = "/lastconnection";

    private static final String SHARES_PATH = "/shares/";

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
        ThingMetainfo metainfo = new ThingMetainfo();
        metainfo.setName(name);
        ThingInformation info = new ThingInformation();
        info.setMetainfo(metainfo);
        info.setType(thingType);
        info.setState(initialState);

        ThingInformation resultInfo = getConnector().doPOST(POST_ADD_THING, info, ThingInformation.class);
        return ThingFactory.create(resultInfo, behaviorFactory).getThing();
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
        ThingInformation info = getThingInformation(id, EnumSet.of(Field.METAINFO, Field.STATE));
        return ThingFactory.create(info, behaviorFactory).getThing();
    }

    /**
     * Returns all Things that are assigned to the logged-in user.
     * 
     * @param behaviorFactory
     *            The behavior factory for the thing.
     * @return The Things
     * @throws RequestException
     *             When the request to the server failed.
     * @throws IOException
     *             When a network error occured or the result format could not be read.
     */
    public Collection<Thing> getThings(ThingBehaviorFactory<?> behaviorFactory) throws RequestException, IOException {
        Collection<ThingInformation> infos = getThingInformations(EnumSet.of(Field.METAINFO, Field.STATE));
        ArrayList<Thing> result = new ArrayList<Thing>(infos.size());
        for (ThingInformation info : infos) {
            result.add(ThingFactory.create(info, behaviorFactory).getThing());
        }
        return result;
    }

    /**
     * Gets all things of the given type that the current user has the given permissions on.
     * 
     * @param type
     *            The type of the wanted things (fully qualified Java class name). If <tt>null</tt>, all types will be returned.
     * @param requiredPermissions
     *            Note that the actual paramter name is <tt>requiresPermission</tt>. The permissions that are (all) required on the returned
     *            things. Note that the {@link ThingPermission#READ} permission is always required.
     * @return The matching things (will only have the meta-info field set).
     * @throws RequestException
     *             When executing the request fails.
     * @throws IOException
     *             When a network error occurs.
     */
    public Collection<ThingInformation> findThings(String type, Collection<ThingPermission> requiredPermissions) throws IOException, RequestException {
        URIBuilder parameters = new URIBuilder();
        parameters.addParameter("type", type);
        for (ThingPermission requiredPermission : requiredPermissions) {
            parameters.addParameter("requiresPermission", requiredPermission.name());
        }
        parameters.addParameter("return", Field.METAINFO.name());
        try {
            return getConnector().doGETCollection(GET_FIND + parameters.toString(), ThingInformation.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Gets the information of a thing.
     * 
     * @param id
     *            The id of the thing.
     * @param fields
     *            The fields of {@link ThingInformation} to be returned.
     * @return The thing information.
     * @throws RequestException
     *             When the request to the server failed.
     * @throws IOException
     *             When a network error occured or the result format could not be read.
     * @throws NotFoundException
     *             If the thing does not exist.
     */
    public ThingInformation getThingInformation(long id, Set<Field> fields) throws RequestException, IOException, NotFoundException {
        String uri = GET_THING + id + "?" + fieldsToQueryString(fields);
        return getConnector().doGET(uri, ThingInformation.class);
    }

    /**
     * Gets the informations of all things accessible to the user.
     * 
     * @param fields
     *            The fields of {@link ThingInformation} to be returned.
     * @return The things that the user can access.
     * @throws RequestException
     *             When the request to the server failed.
     * @throws IOException
     *             When a network error occured or the result format could not be read.
     */
    public Collection<ThingInformation> getThingInformations(Set<Field> fields) throws RequestException, IOException {
        try {
            String uri = THINGS_PREFIX + "?" + fieldsToQueryString(fields);
            return getConnector().doGETCollection(uri, ThingInformation.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

    /**
     * Concatenates the fields to a query string.
     * 
     * @param fields
     *            The fields.
     * @return The query string.
     */
    private static String fieldsToQueryString(Set<Field> fields) {
        StringBuilder builder = new StringBuilder();
        for (Field field : fields) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append("return=");
            builder.append(field.name());
        }
        return builder.toString();
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
     * Reads the state of the thing.
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
        return getThingInformation(id, EnumSet.of(Field.STATE)).getState();
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
    public void registerToEvent(long observerId, RegisterEventRequest request) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + observerId + POST_REGISTER_EVENT_SUFFIX, request);
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
    public void unregisterFromEvent(long observerId, RegisterEventRequest request) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + observerId + POST_UNREGISTER_EVENT_SUFFIX, request);
    }

    /**
     * Registers to or unregisters from multiple events.
     *
     * @param observerId
     *            The ID of the thing that wants to register to or unregister from the given event.
     * @param requests
     *            the (un)register requests
     * @throws RequestException
     *             the request exception
     * @throws IOException
     *             When a network error occured.
     */
    public void multipleEvents(long observerId, MultipleEventsRequest requests) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + observerId + POST_MULTIPLE_EVENTS_SUFFIX, requests);
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
        if (lastOnline == null || lastOnline.before(new Date(now - TEN_MIN))) {
            return OnlineState.STATUS_OFFLINE;
        } else if (lastOnline.before(new Date(now - FIVE_MIN))) {
            return OnlineState.STATUS_AWAY;
        } else {
            return OnlineState.STATUS_ONLINE;
        }
    }

    /**
     * Share a thing with another user. Note that this will unshare the thing if <tt>permissions</tt> is epmty.
     *
     * @param thingID
     *            the id of the thing to be shared
     * @param userID
     *            the id of the user to share with
     * @param permissions
     *            the permissions
     * @throws RequestException
     *             the request exception
     * @throws IOException
     *             When a network error occured.
     */
    public void share(long thingID, long userID, Set<ThingPermission> permissions) throws RequestException, IOException {
        share(thingID, new ThingShare(userID, permissions));
    }

    /**
     * Share a thing with another user.
     *
     * @param thingID
     *            the id of the thing to be shared
     * @param share
     *            The sharing data.
     * @throws RequestException
     *             the request exception
     * @throws IOException
     *             When a network error occured.
     */
    public void share(long thingID, ThingShare share) throws RequestException, IOException {
        getConnector().doPOST(THINGS_PREFIX + thingID + SHARES_PATH, share);
    }

    /**
     * Unshare a thing with another user.
     *
     * @param thingID
     *            the id of the thing to be shared
     * @param userID
     *            the id of the user to share with
     * @throws RequestException
     *             the request exception
     * @throws IOException
     *             When a network error occured.
     */
    public void unshare(long thingID, long userID) throws RequestException, IOException {
        getConnector().doDELETE(THINGS_PREFIX + thingID + SHARES_PATH + userID);
    }

    /**
     * Gets all the users and their permissions for a given thing.
     * 
     * @param thingID
     *            The id of the thing.
     * @return A collection of all shares of the thing.
     * @throws RequestException
     *             the request exception
     * @throws NotFoundException
     *             When the thing does not exist.
     * @throws IOException
     *             When a network error occured.
     */
    public Collection<ThingShare> getThingShares(long thingID) throws RequestException, IOException, NotFoundException {
        return getConnector().doGETCollection(THINGS_PREFIX + thingID + SHARES_PATH, ThingShare.class);
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
