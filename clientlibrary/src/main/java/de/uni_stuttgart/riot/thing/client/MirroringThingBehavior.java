package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.references.DelegatingReferenceResolver;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.TargetNotFoundException;
import de.uni_stuttgart.riot.references.TypedReferenceResolver;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.rest.MultipleEventsRequest;
import de.uni_stuttgart.riot.thing.rest.RegisterEventRequest;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;

/**
 * A base class for thing behaviors that mirror other things. Mirroring means that a thing is "copied" from the server to the local machine.
 * All changes (and actions) that are made locally to the thing are automatically sent to the server. All changes (and events) that occur
 * remotely are automatically fetched from the server and reflected locally. Therefore, it is important that the method {@link
 * fetchUpdates()} is called regularly to poll for these changes.
 * 
 * @author Philipp Keck
 */
public abstract class MirroringThingBehavior extends ClientThingBehavior implements TypedReferenceResolver<Thing> {

    /**
     * This behavior factory will create instances of {@link MirroredThingBehavior} that are attached to this {@link MirroringThingBehavior}
     * and add them to the list of {@link #knownOtherThings}.
     */
    protected final ThingBehaviorFactory<MirroredThingBehavior> behaviorFactory = new ThingBehaviorFactory<MirroredThingBehavior>() {
        public MirroredThingBehavior newBehavior(Class<? extends Thing> thingType) {
            return new MirroredThingBehavior(MirroringThingBehavior.this);
        }

        public MirroredThingBehavior existingBehavior(long thingID) {
            return knownOtherThings.get(thingID);
        }

        public void onThingCreated(Thing thing, MirroredThingBehavior behavior) {
            knownOtherThings.put(thing.getId(), behavior);
        }
    };

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(MirroringThingBehavior.class);

    /**
     * The list of known things, i.e. the ones that are "cached" and used locally.
     */
    private final Map<Long, MirroredThingBehavior> knownOtherThings = new HashMap<Long, MirroredThingBehavior>();

    /**
     * This request is used to accumulate multiple event registrations. See
     */
    private MultipleEventsRequest registerMultipleEvents;

    /**
     * Creates a new behavior instance.
     * 
     * @param thingClient
     *            The {@link ThingClient} responsible for this behavior.
     */
    public MirroringThingBehavior(ThingClient thingClient) {
        super(thingClient);
        super.getDelegatingResolver().addResolver(Thing.class, this);
    }

    /**
     * Fetches the updates from the server and executes them locally.
     * 
     * @throws NotFoundException
     *             When the (mirroring) thing is not known to the server anymore.
     * @throws IOException
     *             When a network error occured.
     */
    protected void fetchUpdates() throws IOException, NotFoundException {
        applyUpdates(downloadUpdates());
    }

    /**
     * Downloads the updates from the server.
     * 
     * @return The updates.
     * @throws NotFoundException
     *             When the (mirroring) thing is not known to the server anymore.
     * @throws IOException
     *             When a network error occured.
     */
    protected ThingUpdatesResponse downloadUpdates() throws IOException, NotFoundException {
        try {
            return getClient().getUpdates(getThing().getId());
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes the updates locally.
     * 
     * @param updates
     *            The updates.
     */
    protected void applyUpdates(ThingUpdatesResponse updates) {
        for (EventInstance eventInstance : updates.getOccuredEvents()) {
            MirroredThingBehavior behavior = knownOtherThings.get(eventInstance.getThingId());
            if (behavior != null) {
                behavior.fireEvent(eventInstance);
            }
        }
        for (ActionInstance action : updates.getOutstandingActions()) {
            if (action.getThingId() != getThing().getId()) {
                throw new IllegalArgumentException("The action " + action + " belongs to " + action.getThingId() + " and not to this thing " + getThing().getId());
            }
            userFiredAction(action);
        }
    }

    /**
     * Called when the user (i.e. some part of the local code) fired an action of one of the mirrored things that is managed by this
     * MirroringThingBehavior. In particular, this includes all intended changes to properties.
     * 
     * @param <A>
     *            The type of the action instance.
     * @param actionInstance
     *            The action instance.
     */
    protected <A extends ActionInstance> void userFiredMirroredAction(A actionInstance) {
        try {
            getClient().submitAction(actionInstance);
        } catch (RequestException e) {
            logger.error("Could not send action instance {} for mirrored thing {}", actionInstance, actionInstance.getThingId(), e);
        } catch (IOException e) {
            logger.error("Could not send action instance {} for mirrored thing {}", actionInstance, actionInstance.getThingId(), e);
        }
    }

    /**
     * Tries to resolve another thing by its ID.
     * 
     * @param id
     *            The ID of the other thing.
     * @return The other thing.
     * @throws NotFoundException
     *             When the other thing could not be resolved.
     * @throws IOException
     *             When a network error occured.
     */
    protected Thing getOtherThing(long id) throws NotFoundException, IOException {
        MirroredThingBehavior behavior = knownOtherThings.get(id);
        if (behavior == null) {
            try {
                getClient().getExistingThing(id, behaviorFactory);
            } catch (NotFoundException e) {
                throw new NotFoundException("Thing " + id + " could not be resolved!", e);
            } catch (RequestException e) {
                throw new RuntimeException(e);
            }
            behavior = knownOtherThings.get(id);
            if (behavior == null) {
                throw new NotFoundException("Thing " + id + " could not be resolved!");
            }
        }
        return behavior.getThing();
    }

    /**
     * Retrieves a mirrored thing from the cache by its ID.
     * 
     * @param id
     *            The ID of the thing.
     * @return The thing or <tt>null</tt> if it is not in the cache.
     */
    protected Thing getCachedThing(long id) {
        return knownOtherThings.get(id).getThing();
    }

    /**
     * Tries to resolve another thing by its ID and casts it to the given type before returning it. A {@link ClassCastException} will occur
     * if the type does not match.
     * 
     * @param <T>
     *            The type of the other thing.
     * @param id
     *            The ID of the other thing.
     * @param expectedType
     *            The type of the other thing.
     * @return The other thing.
     * @throws IOException
     *             When a network error occured.
     * @throws NotFoundException
     *             When the other thing could not be resolved.
     */
    protected <T extends Thing> T getOtherThing(long id, Class<T> expectedType) throws NotFoundException, IOException {
        return expectedType.cast(getOtherThing(id));
    }

    /**
     * Verifies that the given <tt>otherThing</tt> has been created by and is managed by this MirroringThingBehavior, i.e., it has been
     * returned from {@link #getOtherThing(long)}. If not, a {@link IllegalArgumentException} is thrown.
     * 
     * @param otherThing
     *            The other thing to check on.
     */
    private void assertOwnOtherThing(Thing otherThing) {
        MirroredThingBehavior otherBehavior = knownOtherThings.get(otherThing.getId());
        if (otherBehavior == null || otherBehavior.getThing() != otherThing) {
            throw new IllegalArgumentException("The given otherThing " + otherThing + " is not managed by this MirroringThing!");
        }
    }

    /**
     * Starts a new multiple events request. All event registrations that are made until {@link #finishMultipleEventsRequest()} is called
     * will not be sent to the server directly. Instead, they will be collected in {@link #registerMultipleEvents} and sent all at once.
     * 
     * @return True if the multiple events request was actually started. If this returns false, there was already one active and the caller
     *         should not call {@link #finishMultipleEventsRequest()}!
     */
    public synchronized boolean startMultipleEventsRequest() {
        if (registerMultipleEvents == null) {
            registerMultipleEvents = new MultipleEventsRequest();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finishes the multiple events request.
     */
    public synchronized void finishMultipleEventsRequest() {
        if (registerMultipleEvents != null) {
            MultipleEventsRequest request = registerMultipleEvents;
            registerMultipleEvents = null;

            if ((request.getRegisterTo() == null || request.getRegisterTo().isEmpty()) && (request.getUnregisterFrom() == null || request.getUnregisterFrom().isEmpty())) {
                // Nothing to do.
                return;
            }

            try {
                getClient().multipleEvents(getThing().getId(), request);
            } catch (RequestException e) {
                logger.error("Could not execute multiple events registration for thing " + getThing().getId());
            } catch (IOException e) {
                logger.error("Could not execute multiple events registration for thing " + getThing().getId());
            }
        }
    }

    /**
     * Registers to an event of another thing.
     * 
     * @param request
     *            The event registration request identifying the other thing and its event.
     */
    synchronized void registerToEvent(RegisterEventRequest request) {
        if (registerMultipleEvents == null) {
            try {
                getClient().registerToEvent(getThing().getId(), request);
            } catch (RequestException e) {
                logger.error("Could not register to event " + request.getTargetEventName() + " of thing " + request.getTargetThingID());
            } catch (IOException e) {
                logger.error("Could not register to event " + request.getTargetEventName() + " of thing " + request.getTargetThingID());
            }
        } else {
            registerMultipleEvents.getRegisterTo().add(request);
        }
    }

    /**
     * Unregisters from an event of another thing.
     * 
     * @param request
     *            The event registration request identifying the other thing and its event.
     */
    synchronized void unregisterToEvent(RegisterEventRequest request) {
        if (registerMultipleEvents == null) {
            try {
                getClient().unregisterFromEvent(getThing().getId(), request);
            } catch (RequestException e) {
                logger.error("Could not unregister from event " + request.getTargetEventName() + " of thing " + request.getTargetThingID());
            } catch (IOException e) {
                logger.error("Could not unregister from event " + request.getTargetEventName() + " of thing " + request.getTargetThingID());
            }
        } else {
            registerMultipleEvents.getUnregisterFrom().add(request);
        }
    }

    /**
     * Retrieves the current state of the given thing from the server. This means that its properties will be up-to-date after the call.
     * 
     * @param otherThing
     *            The thing to update.
     * @throws NotFoundException
     *             When the (mirrored) thing does not exist anymore.
     * @throws IOException
     *             When a network error occured.
     */
    protected void updateThingState(Thing otherThing) throws IOException, NotFoundException {
        assertOwnOtherThing(otherThing);
        try {
            ThingState state = getClient().getThingState(otherThing.getId());
            state.apply(otherThing);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        } catch (NotFoundException e) {
            knownOtherThings.remove(otherThing.getId());
            throw e;
        }
    }

    /**
     * Starts the monitoring for the given thing. See {@link MirroredThingBehavior#startMonitoring()}. Call {@link #fetchUpdates()} to
     * update the thing in the future.
     * 
     * @param otherThing
     *            The thing to be monitored.
     */
    protected void startMonitoring(Thing otherThing) {
        knownOtherThings.get(otherThing.getId()).startMonitoring();
    }

    /**
     * Stops the monitoring for the given thing. See {@link MirroredThingBehavior#stopMonitoring()}.
     * 
     * @param otherThing
     *            The thing to not be monitored anymore.
     */
    protected void stopMonitoring(Thing otherThing) {
        knownOtherThings.get(otherThing.getId()).stopMonitoring();
    }

    @Override
    protected void shutdown() throws Exception {
        Exception lastException = null;
        startMultipleEventsRequest();
        for (MirroredThingBehavior otherBehavior : knownOtherThings.values()) {
            try {
                otherBehavior.shutdown();
            } catch (Exception e) {
                lastException = e;
            }
        }
        knownOtherThings.clear();
        finishMultipleEventsRequest();
        if (lastException != null) {
            throw new Exception("At least one shutdown failed, last exception was", lastException);
        }
    }

    @Override
    public Thing resolve(long targetId) throws ResolveReferenceException {
        if (targetId == getThing().getId()) {
            return getThing();
        } else {
            try {
                return getOtherThing(targetId);
            } catch (NotFoundException e) {
                throw new TargetNotFoundException("The thing with ID " + targetId + " does not exist", e);
            } catch (IOException e) {
                throw new ResolveReferenceException(e);
            }
        }
    }

    // This method is here to expose it to the MirroredThingBehavior.
    @Override
    protected DelegatingReferenceResolver getDelegatingResolver() {
        return super.getDelegatingResolver();
    }

}
