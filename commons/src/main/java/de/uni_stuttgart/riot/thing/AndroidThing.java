package de.uni_stuttgart.riot.thing.client;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingDescription;

/**
 * Thing for a Android Device that is used to monitor all Things a user has access to.
 *
 */
public class AndroidThing extends Thing {

    /**
     * Constructor.
     * 
     * @param name
     *            name of the Device
     * @param behavior
     *            its behavior (normaly {@link DeviceBehavior})
     */
    public AndroidThing(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

    /**
     * Behavior for Devices (Things that can monitor and control other things).
     *
     */
    public static class DeviceBehavior extends ExecutingThingBehavior {

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
         * Retrieves a Thing by its {@link ThingDescription}.
         * 
         * @param thingDescription
         *            the corresponding {@link ThingDescription}
         * @return the Thing
         * @throws ThingNotFoundException
         *             if its not found
         */
        public Thing getThingByDiscription(ThingDescription thingDescription) throws ThingNotFoundException {
            return this.getOtherThing(thingDescription.getThingId());
        }

        /**
         * Reloads the accessable Things. To be called if a new thing was added.
         * 
         * @throws RequestException
         *             error on calling the Service.
         */
        public void updateThings() throws RequestException {
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
         * @see MirroringThingBehavior#updateThingState(Thing).
         */
        public void updateThingState(Thing thing) {
            super.updateThingState(thing);
        }

        @Override
        protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        }
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
     */
    public static DeviceBehavior create(String name, final ThingClient thingClient) throws RequestException {
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
}
