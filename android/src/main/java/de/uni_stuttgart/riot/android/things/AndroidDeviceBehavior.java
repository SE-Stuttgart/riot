package de.uni_stuttgart.riot.android.things;

import java.io.IOException;
import java.util.Collection;

import android.os.Looper;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Device;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.client.MirroringThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.client.TypedExecutingThingBehavior;

/**
 * The behavior for the {@link Device}s that are executed as an Android device. On the one hand, the device executed by this behavior is the
 * one that represents this Android phone on the server. Since {@link Device}s don't have many real functions of their own, the main purpose
 * of this behavior is to expose the interfaces of the {@link MirroringThingBehavior} to the rest of the Android application and thus allow
 * the Android application to access other devices by listing them or by accessing them through their ID.
 * 
 * @author Philipp Keck
 */
public class AndroidDeviceBehavior extends TypedExecutingThingBehavior<Device> {

    /**
     * Creates a new AndroidDeviceBehavior.
     * 
     * @param thingClient
     *            The ThingClient to communicate through.
     */
    public AndroidDeviceBehavior(ThingClient thingClient) {
        super(thingClient, Device.class);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        // A device does not have any actions, so there is nothing to do here.
    }

    @Override
    protected <A extends ActionInstance> void userFiredMirroredAction(final A actionInstance) {
        // This action will be transported to the server in order to be executed. We need to do this on a background thread.
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ThingManager.getInstance().executeInBackground(new Runnable() {
                public void run() {
                    AndroidDeviceBehavior.super.userFiredMirroredAction(actionInstance);
                }
            });
        } else {
            super.userFiredMirroredAction(actionInstance);
        }
    }

    @Override
    protected void startMonitoring(Thing otherThing) {
        super.startMonitoring(otherThing);
    }

    @Override
    protected void stopMonitoring(Thing otherThing) {
        super.stopMonitoring(otherThing);
    }

    /**
     * Stops the monitoring for the given things. Unknown IDs or un-monitored things will be ignored.
     * 
     * @param thingIDs
     *            The thing IDs.
     */
    protected void stopMonitoring(Collection<Long> thingIDs) {
        startMultipleEventsRequest();
        try {
            for (long thingID : thingIDs) {
                Thing thing = getCachedThing(thingID);
                if (thing != null) {
                    stopMonitoring(thing);
                }
            }
        } finally {
            finishMultipleEventsRequest();
        }
    }

    @Override
    protected Thing getOtherThing(long id) throws NotFoundException, IOException {
        return super.getOtherThing(id);
    }

    @Override
    protected <T extends Thing> T getOtherThing(long id, Class<T> expectedType) throws NotFoundException, IOException {
        return super.getOtherThing(id, expectedType);
    }

    @Override
    protected void updateThingState(Thing otherThing) throws IOException, NotFoundException {
        super.updateThingState(otherThing);
    }

    /**
     * Fetches all things from the server and returns them. Note that things and behaviors will be reused if already present in the
     * MirroringThingBehavior.
     * 
     * @return A list of all things that the current user can access.
     * @throws IOException
     *             When a network error occured.
     */
    protected Collection<Thing> getAllThings() throws IOException {
        try {
            return getClient().getThings(behaviorFactory);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void fetchUpdates() throws IOException, NotFoundException {
        super.fetchUpdates();
    }
}
