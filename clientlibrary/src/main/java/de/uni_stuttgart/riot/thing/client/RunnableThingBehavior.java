package de.uni_stuttgart.riot.thing.client;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A {@link ThingBehavior} that simulates the actual thing by running a thread that regularly polls the server for updates.
 */
public abstract class RunnableThingBehavior extends ExecutingThingBehavior implements Runnable {

    /** The delay between update polling requests. */
    private final long delay;

    /** The thread started flag. */
    private volatile boolean started;

    /**
     * Instantiates a new RunnableThingBehavior.
     *
     * @param thingClient
     *            The client that handles the REST operations
     * @param delay
     *            The delay between get action instances requests
     */
    public RunnableThingBehavior(ThingClient thingClient, long delay) {
        super(thingClient);
        this.delay = delay;
    }

    /**
     * Starts the Thing Thread.
     */
    public void start() {
        this.started = true;
        new Thread(this).start();
    }

    /**
     * Stops the Thing Thread.
     */
    public void stop() {
        this.started = false;
    }

    @Override
    public void run() {
        try {
            while (started) {
                try {
                    fetchUpdates();
                } catch (RequestException e) {
                    // FIXME handle exception, stop thread or continue trying send requests?

                }
                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
            started = false;
        }
        try {
            shutdown();
        } catch (Exception e) {
            // FIXME What to do with this exception?
        }
    }

}
