package de.uni_stuttgart.riot.thing.client;

import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.Event;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;

/**
 * Base class for all Thing classes.
 * 
 * It requests from server periodically the action instances, that the Thing shall execute. Action Instances are handled at the subclasses.
 */
public abstract class ClientThing extends Thing implements Runnable {

    /** The delay between get action instances requests. */
    private final long delay;

    /** The thread started flag. */
    private boolean started;

    /** The client to handles REST operations for Things. */
    private final ThingClient thingClient;

    /**
     * Instantiates a new client thing.
     *
     * @param thingClient
     *            the client that handles the REST operations
     * @param name
     *            the name of the Thing
     * @param delay
     *            the delay between get action instances requests
     */
    public ClientThing(ThingClient thingClient, String name, long delay) {
        super(name);
        this.delay = delay;
        this.thingClient = thingClient;
    }

    /**
     * This method should handle the action instances.
     *
     * @param actionInstance
     *            the action instance
     */
    protected abstract void handleActionInstances(ActionInstance actionInstance);

    /**
     * This method initializes the Thing with its supported events.
     *
     * @return the collection of supported events.
     */
    protected abstract Collection<Event> initEvents();

    /**
     * This method initializes the Thing with its supported actions.
     *
     * @return the collection of supported actions.
     */
    protected abstract Collection<Action> initActions();

    /**
     * This method initializes the Thing with its properties.
     *
     * @return the map
     */
    protected abstract Map<String, Property> initProperties();

    /**
     * Registers this Thing on the server.
     *
     * @throws RequestException
     *             the request exception
     */
    public void register() throws RequestException {
        RemoteThing r = new RemoteThing(this.getName(), 0);
        Collection<Action> actions = this.initActions();
        r.setActions(actions);
        this.setActions(actions);
        Collection<Event> events = this.initEvents();
        r.setEvents(events);
        this.setEvents(events);
        Map<String, Property> properties = this.initProperties();
        r.setProperties(properties);
        this.setProperties(properties);
        r = this.thingClient.registerThing(r);
        this.setId(r.getId());
    }

    /**
     * unregisters this Thing from server.
     *
     * @throws RequestException
     *             the request exception
     */
    public void deRegister() throws RequestException {
        this.thingClient.deregisterThing(this.getId());
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
                    for (ActionInstance actionInstance : this.getActionInstances()) {
                        this.handleActionInstances(actionInstance);
                    }
                } catch (RequestException e) {
                    // FIXME handle exception, stop thread or continue trying send requests?

                }
                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
            started = false;
        }
    }

    /**
     * Gets the action instances from server.
     *
     * @return the action instances
     * @throws RequestException
     *             the request exception
     */
    private Queue<ActionInstance> getActionInstances() throws RequestException {
        return this.thingClient.getActionInstances(this.getId());
    }

    /**
     * Notifies the server about the occurred event.
     *
     * @param instance
     *            the event instance representing the occurred event.
     * @throws RequestException
     *             the request exception
     */
    protected void notifyEvent(EventInstance instance) throws RequestException {
        this.thingClient.notifyEvent(instance);
    }

}
