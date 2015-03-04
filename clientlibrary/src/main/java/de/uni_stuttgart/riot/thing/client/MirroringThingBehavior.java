package de.uni_stuttgart.riot.thing.client;

import java.util.HashMap;
import java.util.Map;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.rest.ThingUpdatesResponse;

/**
 * A base class for thing behaviors that mirror other things. Mirroring means that a thing is "copied" from the server to the local machine.
 * All changes (and actions) that are made locally to the thing are automatically sent to the server. All changes (and events) that occur
 * remotely are automatically fetched from the server and reflected locally. Therefore, it is important that the method {@link
 * fetchUpdates()} is called regularly to poll for these changes.
 * 
 * @author Philipp Keck
 */
public abstract class MirroringThingBehavior extends ClientThingBehavior {

    /**
     * This behavior factory will create instances of {@link MirroredThingBehavior} that are attached to this {@link MirroringThingBehavior}
     * and add them to the list of {@link #knownOtherThings}.
     */
    protected final ThingBehaviorFactory<MirroredThingBehavior> behaviorFactory = new ThingBehaviorFactory<MirroredThingBehavior>() {

        @Override
        public MirroredThingBehavior newBehavior(long thingID, String thingName, Class<? extends Thing> thingType) {
            return new MirroredThingBehavior(MirroringThingBehavior.this);
        }

        @Override
        public void onThingCreated(Thing thing, MirroredThingBehavior behavior) {
            knownOtherThings.put(thing.getId(), behavior);
        }

    };

    /**
     * The list of known things, i.e. the ones that are "cached" and used locally.
     */
    private final Map<Long, MirroredThingBehavior> knownOtherThings = new HashMap<Long, MirroredThingBehavior>();

    /**
     * Creates a new behavior instance.
     * 
     * @param thingClient
     *            The {@link ThingClient} responsible for this behavior.
     */
    public MirroringThingBehavior(ThingClient thingClient) {
        super(thingClient);
    }

    /**
     * Fetches the updates from the server and executes them locally.
     * 
     * @throws RequestException
     *             When querying the server failed.
     */
    protected void fetchUpdates() throws RequestException {
        applyUpdates(downloadUpdates());
    }

    /**
     * Downloads the updates from the server.
     * 
     * @return The updates.
     * @throws RequestException
     *             When querying the server fails.
     */
    protected ThingUpdatesResponse downloadUpdates() throws RequestException {
        return getClient().getUpdates(getThing().getId());
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
     * Tries to resolve another thing by its ID.
     * 
     * @param id
     *            The ID of the other thing.
     * @return The other thing.
     * @throws ThingNotFoundException
     *             When the thing could not be resolved.
     */
    protected Thing getOtherThing(long id) throws ThingNotFoundException {
        MirroredThingBehavior behavior = knownOtherThings.get(id);
        if (behavior == null) {
            try {
                getClient().getExistingThing(id, behaviorFactory);
            } catch (RequestException e) {
                throw new ThingNotFoundException("Thing " + id + " could not be resolved!", e);
            }
            behavior = knownOtherThings.get(id);
            if (behavior == null) {
                throw new ThingNotFoundException("Thing " + id + " could not be resolved!");
            }
        }
        return behavior.getThing();
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
     * @throws ThingNotFoundException
     *             When the thing could not be resolved.
     */
    protected <T extends Thing> T getOtherThing(long id, Class<T> expectedType) throws ThingNotFoundException {
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
     * Retrieves the current state of the given thing from the server. This means that its properties will be up-to-date after the call.
     * 
     * @param otherThing
     *            The thing to update.
     */
    protected void updateThingState(Thing otherThing) {
        assertOwnOtherThing(otherThing);
        try {
            ThingState state = getClient().getThingState(otherThing.getId());
            state.apply(otherThing);
        } catch (RequestException e) {
            throw new RuntimeException(e);
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
        for (MirroredThingBehavior otherBehavior : knownOtherThings.values()) {
            try {
                otherBehavior.shutdown();
            } catch (Exception e) {
                lastException = e;
            }
        }
        knownOtherThings.clear();
        if (lastException != null) {
            throw new Exception("At least one shutdown failed, last exception was", lastException);
        }
    }

}
