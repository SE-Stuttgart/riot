package de.uni_stuttgart.riot.thing.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.AndroidThing;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingDescription;

/**
 * Behavior for Devices (Things that can monitor and control other things).
 *
 */
public class DeviceBehavior extends ExecutingThingBehavior {

    private Collection<ThingDescription> thingDescriptions;

    /**
     * Constructor.
     * 
     * @param thingClient
     *            loginClient to be used
     */
    public DeviceBehavior(ThingClient thingClient) {
        super(thingClient);
    }

    /**
     * Creates a new {@link AndroidThing} with {@link DeviceBehavior} and returns the behavior. The Thing will be registered at the server.
     * 
     * @param name
     *            name of the Device
     * @param thingClient
     *            client to be used
     * @return the things behavior
     * @throws RequestException
     *             on service call error
     * @throws IOException
     *             on network error
     */
    public static DeviceBehavior create(String name, final ThingClient thingClient) throws RequestException, IOException {
        return DeviceBehavior.launchNewThing(AndroidThing.class, thingClient, name, new ThingBehaviorFactory<DeviceBehavior>() {
            @Override
            public DeviceBehavior newBehavior(long thingID, String thingName, Class<? extends Thing> thingType) {
                return new DeviceBehavior(thingClient);
            }

            @Override
            public void onThingCreated(Thing thing, DeviceBehavior behavior) {
            }
        });
    }

    /**
     * Retrieves a Thing by its {@link ThingDescription}.
     * 
     * @param thingDescription
     *            the corresponding {@link ThingDescription}
     * @return the Thing
     * @throws NotFoundException
     *             if its not found
     * @throws IOException
     *             if a network error occurs
     */
    public Thing getThingByDiscription(ThingDescription thingDescription) throws NotFoundException, IOException {
        return this.getOtherThing(thingDescription.getThingId());
    }

    /**
     * Reloads the accessable Things. To be called if a new thing was added.
     * 
     * @throws RequestException
     *             error on calling the Service.
     * @throws IOException
     *             error with network connection
     */
    public void updateThings() throws RequestException, IOException {
        Collection<Thing> things = thingClient.getThings(this.behaviorFactory);
        ArrayList<ThingDescription> descriptions = new ArrayList<ThingDescription>();
        for (Thing thing : things) {
            descriptions.add(ThingDescription.create(thing));
            this.startMonitoring(thing);
        }
        this.thingDescriptions = descriptions;
    }

    public Collection<ThingDescription> getDescriptions() {
        return thingDescriptions;
    }

    /**
     * Updates the Thing with its values form the server.
     * 
     * @param thing
     *            thing to be updated
     * @throws NotFoundException
     *             thing not found
     * @throws IOException
     *             network error
     * @see MirroringThingBehavior#updateThingState(Thing).
     */
    public void updateThingState(Thing thing) throws IOException, NotFoundException {
        super.updateThingState(thing);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
    }
}
