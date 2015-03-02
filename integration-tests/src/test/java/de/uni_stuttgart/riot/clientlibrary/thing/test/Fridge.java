package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * A {@Thing} that represents a fridge. A fridge has a temperature that can only be read and a state. In addition, it has an action
 * "eat" that can be executed with the name of a food. This action will always trigger the event "out of food" for the given food.
 */
public class Fridge extends Thing {

    private final WritableProperty<Boolean> state = newWritableProperty("State", Boolean.class, false);
    private final Property<Integer> temp = newProperty("Temp", Integer.class, 4);
    private final Action<EatActionInstance> eatEverything = newAction("EatEverything", EatActionInstance.class);
    private final Event<OutOfFoodEventInstance> outOfFood = newEvent("OutOfFood", OutOfFoodEventInstance.class);

    /**
     * Creates a new fridge.
     * 
     * @param name
     *            The name of the fridge.
     * @param behavior
     *            The behavior of the fridge. For testing purposes, just use {@link FridgeBehavior}.
     */
    public Fridge(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

    public boolean getState() {
        return state.getValue();
    }

    public void setState(boolean state) {
        this.state.set(state);
    }

    public WritableProperty<Boolean> getStateProperty() {
        return state;
    }

    public int getTemp() {
        return temp.getValue();
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
     */
    public static FridgeBehavior create(ThingClient client, String name) throws RequestException {
        @SuppressWarnings("unchecked")
        ThingBehaviorFactory<FridgeBehavior> behaviorFactory = mock(ThingBehaviorFactory.class);
        when(behaviorFactory.newBehavior(anyLong(), anyString(), any())).thenReturn(new FridgeBehavior(client));
        return ExecutingThingBehavior.launchNewThing(Fridge.class, client, name, behaviorFactory);
    }

    /**
     * A behavior for using the fridge as a standalone thing in tests. Note that this behavior would usually be a runnable behavior. For the
     * purpose of testing, however, the {@link #fetchUpdates()} method should be called manually.
     */
    public static class FridgeBehavior extends ExecutingThingBehavior {

        /**
         * Creates a new FridgeBehavior.
         * 
         * @param thingClient
         *            The client that handles the REST operations
         */
        public FridgeBehavior(ThingClient thingClient) {
            super(thingClient);
        }

        public Fridge getFridge() {
            return (Fridge) getThing();
        }

        public void simulateTemperatureChange(int newTemperature) {
            changePropertyValue(getFridge().temp, newTemperature);
        }

        @Override
        protected void fetchUpdates() throws RequestException {
            super.fetchUpdates();
        }

        @Override
        protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
            // The ExecutingThingBehavior will take care of the PropertySetActions for us.
            if (actionInstance instanceof EatActionInstance) {
                if (action == getFridge().eatEverything) {
                    // After eating everything, there is nothing left:
                    executeEvent(new OutOfFoodEventInstance(getFridge().outOfFood, ((EatActionInstance) actionInstance).getFood()));
                }
            }
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

}
