package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A {@link ThingBehavior} that simulates the actual thing by running a thread that regularly polls the server for updates.
 */
public abstract class RunnableThingBehavior extends ExecutingThingBehavior implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(RunnableThingBehavior.class);

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
                } catch (IOException e) {
                    started = false;
                    logger.error("Error when polling the server", e);
                } catch (NotFoundException e) {
                    started = false;
                    logger.error("Error when polling the server", e);
                }
                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
            started = false;
        }
        try {
            shutdown();
        } catch (Exception e) {
            logger.error("Error during shutdown", e);
        }
    }

}
