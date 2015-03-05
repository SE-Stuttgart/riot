package de.uni_stuttgart.riot.clientlibrary.thing.test;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.clientlibrary.thing.test.AndroidThing.DeviceBehavior;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.client.ThingNotFoundException;

public class AndroidThing extends Thing {

    public AndroidThing(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

    public static class DeviceBehavior extends ExecutingThingBehavior {

        private Collection<ThingDescription> thingDescriptions;

        public DeviceBehavior(ThingClient thingClient) throws RequestException {
            super(thingClient);
        }
        
        public Thing getThingByDiscription(ThingDescription thingDescription) throws ThingNotFoundException {
            return this.getOtherThing(thingDescription.getThingId());
        }
        
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
        
        public void updateThingState(Thing thing) {
            super.updateThingState(thing);
        }


        @Override
        protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        }
    }

    public static DeviceBehavior create(String name, final ThingClient thingClient) throws RequestException {
        return DeviceBehavior.launchNewThing(AndroidThing.class, thingClient, name, new ThingBehaviorFactory<DeviceBehavior>() {
            @Override
            public DeviceBehavior newBehavior(long thingID, String thingName, Class<? extends Thing> thingType) {
                try {
                    return new DeviceBehavior(thingClient);
                } catch (RequestException e) {
                    return null; // FIXME
                }
            }
            @Override
            public void onThingCreated(Thing thing, DeviceBehavior behavior) {
            }
        });
    }
}
