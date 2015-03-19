package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.commons.rest.usermanagement.data.User;
import de.uni_stuttgart.riot.references.DelegatingReferenceResolver;
import de.uni_stuttgart.riot.references.Reference;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.StaticReference;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.WritableReferenceProperty;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.client.TypedExecutingThingBehavior;

/**
 * A {@Thing} that represents a fridge. A fridge has a temperature that can only be read and a state. In addition, it has an action
 * "eat" that can be executed with the name of a food. This action will always trigger the event "out of food" for the given food.
 */
public class Fridge extends Thing {

    private final WritableProperty<Boolean> state = newWritableProperty("State", Boolean.class, false);
    private final Property<Integer> temp = newProperty("Temp", Integer.class, 4);
    private final Action<EatActionInstance> eatEverything = newAction("EatEverything", EatActionInstance.class);
    private final Event<OutOfFoodEventInstance> outOfFood = newEvent("OutOfFood", OutOfFoodEventInstance.class);
    private final WritableReferenceProperty<User> deliveryGuy = newWritableReferenceProperty("DeliveryGuy", User.class);
    private final Event<FiredDeliveryGuyEventInstance> firedDeliveryGuy = newEvent("firedDeliveryGuy", FiredDeliveryGuyEventInstance.class);
    private final Action<HireDeliveryGuyActionInstance> hireDeliveryGuy = newAction("hireDeliveryGuy", HireDeliveryGuyActionInstance.class);

    /**
     * Creates a new fridge.
     * 
     * @param behavior
     *            The behavior of the fridge. For testing purposes, just use {@link FridgeBehavior}.
     */
    public Fridge(ThingBehavior behavior) {
        super(behavior);
    }

    public boolean getState() {
        return state.get();
    }

    public void setState(boolean state) {
        this.state.set(state);
    }

    public WritableProperty<Boolean> getStateProperty() {
        return state;
    }

    public int getTemp() {
        return temp.get();
    }

    public Property<Integer> getTempProperty() {
        return temp;
    }

    public Action<EatActionInstance> getEatEverythingAction() {
        return eatEverything;
    }

    public void eatEverythingOf(String food) {
        eatEverything.fire(new EatActionInstance(eatEverything, food));
    }

    public Event<OutOfFoodEventInstance> getOutOfFoodEvent() {
        return outOfFood;
    }

    public User getDeliveryGuy() throws ResolveReferenceException {
        return deliveryGuy.getTarget();
    }

    public void setDeliveryGuy(User newGuy) {
        deliveryGuy.setTarget(newGuy);
    }

    public Long getDeliveryGuyId() {
        return deliveryGuy.get();
    }

    public void setDeliveryGuyId(Long newId) {
        deliveryGuy.set(newId);
    }

    public WritableReferenceProperty<User> getDeliveryGuyProperty() {
        return deliveryGuy;
    }

    public Event<FiredDeliveryGuyEventInstance> getFiredDeliveryGuyEvent() {
        return firedDeliveryGuy;
    }

    public void hireDeliveryGuy(User newGuy) {
        hireDeliveryGuy.fire(new HireDeliveryGuyActionInstance(hireDeliveryGuy, newGuy));
    }

    public Action<HireDeliveryGuyActionInstance> getHireDeliveryGuyAction() {
        return hireDeliveryGuy;
    }

    /**
     * Creates a new fridge for testing purposes.
     * 
     * @param client
     *            The ThingClient to communicate with the server.
     * @param name
     *            The name of the fridge.
     * @return The behavior of the fridge. Call {@link FridgeBehavior#getFridge()} to get the fridge.
     * @throws RequestException
     *             When registering the thing with the server failed.
     * @throws IOException
     *             When a network error occured.
     */
    public static FridgeBehavior create(ThingClient client, String name) throws RequestException, IOException {
        @SuppressWarnings("unchecked")
        ThingBehaviorFactory<FridgeBehavior> behaviorFactory = mock(ThingBehaviorFactory.class);
        when(behaviorFactory.newBehavior(any())).thenReturn(new FridgeBehavior(client));
        return ExecutingThingBehavior.launchNewThing(Fridge.class, client, name, behaviorFactory);
    }

    /**
     * A behavior for using the fridge as a standalone thing in tests. Note that this behavior would usually be a runnable behavior. For the
     * purpose of testing, however, the {@link #fetchUpdates()} method should be called manually.
     */
    public static class FridgeBehavior extends TypedExecutingThingBehavior<Fridge> {

        /**
         * Creates a new FridgeBehavior.
         * 
         * @param thingClient
         *            The client that handles the REST operations
         */
        public FridgeBehavior(ThingClient thingClient) {
            super(thingClient, Fridge.class);
        }

        public void simulateTemperatureChange(int newTemperature) {
            changePropertyValue(getThing().temp, newTemperature);
        }

        @Override
        protected void fetchUpdates() throws IOException, NotFoundException {
            super.fetchUpdates();
        }

        @Override
        protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
            // The ExecutingThingBehavior will take care of the PropertySetActions for us.
            if (actionInstance instanceof EatActionInstance && action == getThing().eatEverything) {
                // After eating everything, there is nothing left:
                executeEvent(new OutOfFoodEventInstance(getThing().outOfFood, ((EatActionInstance) actionInstance).getFood()));
            } else if (actionInstance instanceof HireDeliveryGuyActionInstance && action == getThing().hireDeliveryGuy) {
                try {
                    User newGuy = resolve(((HireDeliveryGuyActionInstance) actionInstance).getNewGuy(), User.class);

                    // First fire the old guy, if necessary
                    if (getThing().getDeliveryGuy() != null) {
                        String reason = getThing().getDeliveryGuy().getUsername() + " was too slow, we now hired " + newGuy.getUsername();
                        executeEvent(new FiredDeliveryGuyEventInstance(getThing().firedDeliveryGuy, getThing().getDeliveryGuy(), reason));
                    }

                    // Then put in the new guy.
                    getThing().getDeliveryGuyProperty().setTarget(newGuy);
                } catch (ResolveReferenceException e) {
                    // Ignore
                }
            }
        }

        @Override
        protected DelegatingReferenceResolver getDelegatingResolver() {
            return super.getDelegatingResolver();
        }

    }

    /**
     * A eat action that represents the eating of food - usually as long until there is nothing left.
     */
    public static class EatActionInstance extends ActionInstance {

        private final String food;

        /**
         * Instantiates a new action instance. The time is set to now.
         *
         * @param action
         *            The action that was fired.
         * @param food
         *            The food that was eaten.
         */
        public EatActionInstance(Action<? extends ActionInstance> action, String food) {
            super(action);
            this.food = food;
        }

        /**
         * Creates a new instance from JSON.
         * 
         * @param node
         *            The JSON node.
         */
        @JsonCreator
        public EatActionInstance(JsonNode node) {
            super(node);
            this.food = node.get("food").asText();
        }

        /**
         * Gets the food that was eaten during this action.
         * 
         * @return The food.
         */
        public String getFood() {
            return food;
        }

    }

    /**
     * An event that is raised when the fridge runs out of a particular type of food. This is especially the case after the food has been
     * eaten {@link EatActionInstance}.
     */
    public static class OutOfFoodEventInstance extends EventInstance {

        private final String food;

        /**
         * Instantiates a new event instance. The time is set to now.
         *
         * @param event
         *            The event that was fired.
         * @param food
         *            The food that we ran out of.
         */
        public OutOfFoodEventInstance(Event<? extends EventInstance> event, String food) {
            super(event);
            this.food = food;
        }

        /**
         * Creates a new instance from JSON.
         * 
         * @param node
         *            The JSON node.
         */
        @JsonCreator
        public OutOfFoodEventInstance(JsonNode node) {
            super(node);
            this.food = node.get("food").asText();
        }

        /**
         * Gets the food that we ran out of.
         * 
         * @return The food.
         */
        public String getFood() {
            return food;
        }

    }

    /**
     * An event that is raised when a delivery guy for the fridge was fired, probably because he didn't do a good job. In particular, those
     * guys get fired when a {@link HireDeliveryGuyActionInstance} is executed.
     */
    public static class FiredDeliveryGuyEventInstance extends EventInstance {

        private final Reference<User> poorGuy;
        private final String reason;

        /**
         * Instantiates a new event instance. The time is set to now.
         *
         * @param event
         *            The event that was fired.
         * @param poorGuy
         *            The poor guy who was fired.
         * @param reason
         *            The reason for firing him.
         */
        public FiredDeliveryGuyEventInstance(Event<? extends EventInstance> event, User poorGuy, String reason) {
            super(event);
            this.poorGuy = StaticReference.create(poorGuy);
            this.reason = reason;
        }

        /**
         * Creates a new instance from JSON.
         * 
         * @param node
         *            The JSON node.
         */
        @JsonCreator
        public FiredDeliveryGuyEventInstance(JsonNode node) {
            super(node);
            this.poorGuy = StaticReference.create(node.get("poorGuy"));
            this.reason = node.get("reason").asText();
        }

        /**
         * Gets the poor guy who was fired.
         * 
         * @return The old delivery guy.
         */
        public Reference<User> getPoorGuy() {
            return poorGuy;
        }

        /**
         * Gets the reason for firing the guy.
         * 
         * @return The reason.
         */
        public String getReason() {
            return reason;
        }

    }

    /**
     * An action that will fire the old delivery guy and employ the new one.
     */
    public static class HireDeliveryGuyActionInstance extends ActionInstance {

        private final Reference<User> newGuy;

        /**
         * Instantiates a new action instance. The time is set to now.
         *
         * @param action
         *            The action that is being fired.
         * @param newGuy
         *            The new delivery guy (we will just fire the old one).
         */
        public HireDeliveryGuyActionInstance(Action<? extends ActionInstance> action, User newGuy) {
            super(action);
            this.newGuy = StaticReference.create(newGuy);
        }

        /**
         * Creates a new instance from JSON.
         * 
         * @param node
         *            The JSON node.
         */
        @JsonCreator
        public HireDeliveryGuyActionInstance(JsonNode node) {
            super(node);
            this.newGuy = StaticReference.create(node.get("newGuy"));
        }

        /**
         * Gets the new delivery guy.
         * 
         * @return The new guy.
         */
        public Reference<User> getNewGuy() {
            return newGuy;
        }

    }

}
