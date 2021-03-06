package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;

import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.references.DelegatingReferenceResolver;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.ThingBehavior;

/**
 * A base class for thing behaviors that are on the client side, i.e., they require a server connection to work. The two most notable kinds
 * of ClientThingBehaviors are the {@link MirroringThingBehavior} that acts as a "master" thing on the local machine and manages all other
 * things by "mirroring", and the {@link MirroredThingBehavior} that is used for this that are mirrored locally.
 * 
 * @author Philipp Keck
 */
public abstract class ClientThingBehavior extends ThingBehavior {

    protected final ThingClient thingClient;

    /**
     * Creates a new behavior instance.
     * 
     * @param thingClient
     *            The {@link ThingClient} responsible for this behavior.
     */
    public ClientThingBehavior(ThingClient thingClient) {
        this.thingClient = thingClient;
    }

    /**
     * Creates a new behavior instance.
     * 
     * @param resolver
     *            The reference resolver used by this behavior.
     * @param thingClient
     *            The {@link ThingClient} responsible for this behavior.
     */
    public ClientThingBehavior(DelegatingReferenceResolver resolver, ThingClient thingClient) {
        super(resolver);
        this.thingClient = thingClient;
    }

    /**
     * Gets the client.
     * 
     * @return The {@link ThingClient} responsible for this behavior.
     */
    protected ThingClient getClient() {
        return thingClient;
    }

    /**
     * Submits the event instance to the server. This is for the event being fired, i.e., being transported away from the executing thing,
     * which is its source.
     * 
     * @param eventInstance
     *            The event instance.
     * @throws RequestException
     *             When sending to the server failed.
     * @throws IOException
     *             When a network error occured.
     */
    protected void sendToServer(EventInstance eventInstance) throws RequestException, IOException {
        thingClient.notifyEvent(eventInstance);
    }

    /**
     * Submits an action instance to the server. This is for the action being executed, i.e., being transported to the executing thing,
     * which is its target.
     * 
     * @param actionInstance
     *            The action instance.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When sending to the server failed.
     */
    protected void sendToServer(ActionInstance actionInstance) throws RequestException, IOException {
        thingClient.submitAction(actionInstance);
    }

    /**
     * This method should detach the thing from the server. This does not mean that the thing needs to be deleted form the server, but its
     * transient connections like registered events etc. should be removed. When overriding this method, do not forget to call the super
     * method!
     * 
     * @throws Exception
     *             When an error occurs.
     */
    protected abstract void shutdown() throws Exception;

}
