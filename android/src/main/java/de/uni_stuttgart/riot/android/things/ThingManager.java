package de.uni_stuttgart.riot.android.things;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.util.LongSparseArray;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.thing.Device;
import de.uni_stuttgart.riot.thing.SingleUseThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * This class should be used as the main entry point for accessing things in the Android App. The thing manager keeps track of all things
 * that are known to the application. In particular, the thing manager uses a {@link Device} instance to register with the server. Thus, the
 * Android device will be present at the server as such a device. The thing manager then provides instances of other (mirrored) things to
 * the Android application.<br>
 * Note that most of the methods may only be called from a background thread, i.e., not from the Android UI Thread.
 * 
 * @author Philipp Keck
 */
public class ThingManager {

    private static final int POLLING_INTERVAL = 500;
    private static final String THINGMANAGER_PREFERENCES = "ThingManager";
    private static final String PREFS_KEY_DEVICE_ID = "deviceID";
    private static final String PREFS_KEY_DEVICE_NAME = "deviceName";
    private static final String TAG = "ThingManager";

    /**
     * Singleton instance.
     */
    private static final ThingManager INSTANCE = new ThingManager();

    /**
     * The scheduler that runs the polling requests, if required.
     */
    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    /**
     * The runnable that is scheduled on the {@link #scheduler} for fetching updates from the server.
     */
    private final Runnable pollingRunnable = new Runnable() {
        public void run() {
            doPollingRequest();
        }
    };

    /**
     * The handle returned by the {@link #scheduler} for the polling task.
     */
    private ScheduledFuture<?> pollingHandle;

    /**
     * This map keeps track of monitors (observers) that want to monitor a certain thing. The goal is to ensure that
     * {@link AndroidDeviceBehavior#startMonitoring(Thing)} is called whenever any part of the Android app wants to monitor the thing, and
     * that {@link AndroidDeviceBehavior#stopMonitoring(Thing)} is called when the last monitoring observer has unregistered.
     */
    private final LongSparseArray<IdentityHashMap<Object, Boolean>> registeredMonitors = new LongSparseArray<IdentityHashMap<Object, Boolean>>();

    /**
     * The behavior of the registered device, or <tt>null</tt> if it is not yet registered.
     */
    private AndroidDeviceBehavior behavior;

    /**
     * The registered device, or <tt>null</tt> if it is not yet registered.
     */
    private Device device;

    /**
     * Singleton constructor.
     */
    private ThingManager() {
    }

    /**
     * Gets the singleton instance.
     * 
     * @return The instance.
     */
    public static ThingManager getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the registered device.
     * 
     * @param context
     *            Required to initialize the server connection, if necessary.
     * @return The registered device that represents this Android phone, never <tt>null</tt>.
     * @throws IOException
     *             When a network error occurs.
     */
    public Device getRegisteredDevice(Context context) throws IOException {
        ensureDeviceRegistered(context);
        return device;
    }

    /**
     * Gets the registered device behavior.
     * 
     * @param context
     *            Required to initialize the server connection, if necessary.
     * @return The AndroidDeviceBehavior responsible for this Android phone that will provide access to mirrored things, never <tt>null</tt>
     * @throws IOException
     *             When a network error occurs.
     */
    private AndroidDeviceBehavior getRegisteredBehavior(Context context) throws IOException {
        ensureDeviceRegistered(context);
        return behavior;
    }

    /**
     * Gets the device behavior that must have been registered previously by providing a context to some of the other methods. This method
     * should only be called if it is certain that another method must have been called before. This method will throw an
     * {@link IllegalStateException} if this is not the case.
     * 
     * @return The behavior.
     */
    private AndroidDeviceBehavior getRegisteredBehavior() {
        if (behavior == null) {
            throw new IllegalStateException("ThingManager has not been initialized!");
        }
        return behavior;
    }

    /**
     * Ensures that the device has been registered and that {@link #behavior} and {@link #device} are available. If necessary, the device is
     * registered with the server.
     * 
     * @param context
     *            Required to initialize the server connection, if necessary. If this parameter is <tt>null</tt>, the expectation is that
     *            the initialization must have happened before in any possible case. Thus, this method will throw an
     *            {@link IllegalStateException} if it isn't.
     * 
     * @throws IOException
     *             When a network error occurs.
     */
    private synchronized void ensureDeviceRegistered(Context context) throws IOException {
        if (device != null && behavior != null) {
            // Registered already.
            return;
        } else if (context == null) {
            throw new IllegalStateException("ThingManager has not been initialized!");
        }

        final ThingClient thingClient = new ThingClient(AndroidConnectionProvider.getConnector(context));
        ThingBehaviorFactory<AndroidDeviceBehavior> behaviorFactory = new SingleUseThingBehaviorFactory<AndroidDeviceBehavior>(new AndroidDeviceBehavior(thingClient));

        SharedPreferences prefs = context.getSharedPreferences(THINGMANAGER_PREFERENCES, 0);
        long deviceID = prefs.getLong(PREFS_KEY_DEVICE_ID, 0);
        if (deviceID == 0) {
            String deviceName = prefs.getString(PREFS_KEY_DEVICE_NAME, Build.MODEL);
            try {
                behavior = ExecutingThingBehavior.launchNewThing(Device.class, thingClient, deviceName, behaviorFactory);
                prefs.edit().putLong(PREFS_KEY_DEVICE_ID, behavior.getThing().getId()).commit();
            } catch (RequestException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                behavior = ExecutingThingBehavior.launchExistingThing(Device.class, thingClient, deviceID, behaviorFactory);
            } catch (NotFoundException e) {
                // The server does not know the device anymore. Remove the local information and retry.
                prefs.edit().remove(PREFS_KEY_DEVICE_ID).commit();
                ensureDeviceRegistered(context);
                return;
            } catch (RequestException e) {
                throw new RuntimeException(e);
            }
        }
        device = behavior.getThing();
    }

    /**
     * Gets a thing by its ID.
     * 
     * @param context
     *            Required to initialize the server connection, if necessary.
     * @param id
     *            The ID of the thing
     * @return The thing, never <tt>null</tt>.
     * @throws IOException
     *             When a network error occurs.
     * @throws NotFoundException
     *             When the thing was not found.
     */
    public Thing getThing(Context context, long id) throws IOException, NotFoundException {
        return getRegisteredBehavior(context).getOtherThing(id);
    }

    /**
     * Gets a typed thing by its ID.
     * 
     * @param <T>
     *            The expected type of the thing.
     * @param context
     *            Required to initialize the server connection, if necessary.
     * @param id
     *            The ID of the thing.
     * @param expectedType
     *            The expected type of the thing. A {@link ClassCastException} will be thrown if the type does not match.
     * @return The thing, never <tt>null</tt>.
     * @throws IOException
     *             When a network error occurs.
     * @throws NotFoundException
     *             When the thing was not found.
     */
    public <T extends Thing> T getThing(Context context, long id, Class<T> expectedType) throws IOException, NotFoundException {
        return getRegisteredBehavior(context).getOtherThing(id, expectedType);
    }

    /**
     * Retrieves all things that the user can access.
     * 
     * @param context
     *            Required to initialize the server connection, if necessary.
     * @return A collection of all things.
     * @throws IOException
     *             When a network error occured.
     */
    public Collection<Thing> getAllThings(Context context) throws IOException {
        return getRegisteredBehavior(context).getAllThings();
    }

    /**
     * Updates the contents of the properties of a thing.
     * 
     * @param thing
     *            The thing to be updated.
     * @throws IOException
     *             When a network error occured.
     * @throws NotFoundException
     *             When the thing could not be found anymore on the server.
     */
    public void updateThingState(Thing thing) throws IOException, NotFoundException {
        getRegisteredBehavior().updateThingState(thing);
    }

    /**
     * Starts monitoring the given thing. This means that it will be updated frequently so that all changes to the properties will be
     * reflected and all events will be fired. To keep track of who monitors which thing, the caller must pass a token object that is also
     * used for unregistering. This might be the current Activity that uses the thing, a dummy object created just for the purpose, etc. <br>
     * The monitoring will only work as long as the App is active and the Android device is turned on, so not when it is locked or in
     * standby. Calls to this method will be ignored if the monitoring is already active.<br>
     * Please do not forget to stop the monitoring if it is not needed anymore!
     * 
     * @param thing
     *            The thing to be monitored.
     * @param token
     *            The token object.
     */
    public void startMonitoring(Thing thing, Object token) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null!");
        }
        boolean wasFirstToken = false;
        boolean wasFirstMonitoredThing = (registeredMonitors.size() == 0);
        synchronized (registeredMonitors) {
            IdentityHashMap<Object, Boolean> currentTokens = registeredMonitors.get(thing.getId());
            if (currentTokens == null) {
                currentTokens = new IdentityHashMap<Object, Boolean>();
                registeredMonitors.put(thing.getId(), currentTokens);
                wasFirstToken = true;
            }
            currentTokens.put(token, Boolean.TRUE);
        }
        if (wasFirstToken) {
            getRegisteredBehavior().startMonitoring(thing);
            if (wasFirstMonitoredThing) {
                startPolling();
            }
        }
    }

    /**
     * Stops the monitoring, see {@link #startMonitoring(Thing, Object)}. Calls to this method will be ignored if the monitoring is not
     * active. Note that the monitoring is only actually stoped if no other monitor (with another token) is active for the thing.
     * 
     * @param thing
     *            The thing that was monitored.
     * @param token
     *            The token object.
     */
    public void stopMonitoring(Thing thing, Object token) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null!");
        }
        boolean wasLast = false;
        synchronized (registeredMonitors) {
            IdentityHashMap<Object, Boolean> currentTokens = registeredMonitors.get(thing.getId());
            if (currentTokens == null) {
                return;
            }
            currentTokens.remove(token);
            if (currentTokens.isEmpty()) {
                registeredMonitors.remove(thing.getId());
                wasLast = true;
            }
        }
        if (wasLast) {
            if (registeredMonitors.size() == 0) {
                stopPolling();
            }
            getRegisteredBehavior().stopMonitoring(thing);
        }
    }

    /**
     * Stops the monitoring on all things monitored by the given token.
     * 
     * @param token
     *            The token object.
     */
    public void stopMonitoring(Object token) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null!");
        }
        List<Long> wasLastFor = new ArrayList<Long>();
        synchronized (registeredMonitors) {
            int size = registeredMonitors.size();
            for (int i = 0; i < size; i++) {
                IdentityHashMap<Object, Boolean> currentTokens = registeredMonitors.valueAt(i);

                currentTokens.remove(token);
                if (currentTokens.isEmpty()) {
                    wasLastFor.add(registeredMonitors.keyAt(i));
                }
            }
            for (long thingID : wasLastFor) {
                registeredMonitors.remove(thingID);
            }
        }
        if (registeredMonitors.size() == 0) {
            stopPolling();
        }
        getRegisteredBehavior().stopMonitoring(wasLastFor);
    }

    /**
     * Starts the regular polling for thing updates from the server. Does nothing if the polling is already active.
     */
    private synchronized void startPolling() {
        if (pollingHandle == null) {
            pollingHandle = scheduler.scheduleWithFixedDelay(pollingRunnable, 0, POLLING_INTERVAL, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops the regular polling (see {@link #startPolling()}). Does nothing if the polling is not active.
     */
    private synchronized void stopPolling() {
        if (pollingHandle != null) {
            pollingHandle.cancel(false);
            pollingHandle = null;
        }
    }

    /**
     * Executes a polling requests. The results will show up in the properties of all monitored things and in fired events of those things.
     */
    private void doPollingRequest() {
        try {
            getRegisteredBehavior().fetchUpdates();
        } catch (IOException e) {
            Log.e(TAG, "Error during polling", e);
        } catch (NotFoundException e) {
            Log.e(TAG, "Error during polling", e);
        }
    }

    /**
     * Executes the given commands on the scheduler of the ThingManager. Note that this scheduler only has one thread, so the command will
     * not run until the last updates have been completed and there will not no polling requests as long as this command blocks. This method
     * is useful for interacting with things on the server and allows to avoid interfering polling updates.
     * 
     * @param command
     *            The command to run.
     */
    public void executeInBackground(Runnable command) {
        scheduler.execute(command);
    }

}
