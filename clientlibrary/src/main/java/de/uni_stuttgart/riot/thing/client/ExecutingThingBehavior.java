package de.uni_stuttgart.riot.thing.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.PropertySetAction;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.WritableProperty;

/**
 * A base class for thing behaviors that actually execute the thing. This means that the thing actually runs on this computer in this JVM,
 * and it is not only there as a mirror. On the other hand, an executing thing keeps track of other things (which are usually executed on
 * other computers) by mirroring them locally.
 * 
 * @author Philipp Keck
 */
public abstract class ExecutingThingBehavior extends MirroringThingBehavior {

    private final Logger logger = LoggerFactory.getLogger(ExecutingThingBehavior.class);

    /**
     * Creates a new behavior instance.
     * 
     * @param thingClient
     *            The {@link ThingClient} responsible for this behavior.
     */
    public ExecutingThingBehavior(ThingClient thingClient) {
        super(thingClient);
    }

    /**
     * Actually sets the value of the property and fires its {@link PropertyChangeEvent}. This behavior is only possible on a thing that is
     * executed locally!
     * 
     * @param <V>
     *            The type of the property's values.
     * @param property
     *            The property to be changed.
     * @param newValue
     *            The new value for the property.
     */
    protected <V> void changePropertyValue(Property<V> property, V newValue) {
        // Note that the property itself will take care of setting the new value immediately before firing the event.
        executeEvent(new PropertyChangeEvent.Instance<V>(property.getChangeEvent(), property.getValue(), newValue));
    }

    @Override
    protected <V> void userModifiedProperty(WritableProperty<V> property, V newValue) {
        // We simply do this, because the property is hosted on this computer.
        // No need to fire a PropertySetAction.
        changePropertyValue(property, newValue);
    }

    @Override
    protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
        // We execute PropertySetActions directly and let the subclass implementation handle the rest.
        Action<A> action = getActionFromInstance(actionInstance);
        if (action instanceof PropertySetAction) {
            userFiredPropertySetAction((PropertySetAction<?>) action, (PropertySetAction.Instance<?>) actionInstance);
        } else {
            executeAction(action, actionInstance);
        }
    }

    /**
     * Helper method to nail down the generic parameter <tt>V</tt>. This method calls {@link #changePropertyValue(Property, Object)} with
     * the given parameters, which changes the property's values and fires the event. The caller <b>must</b> ensure that the
     * <tt>instance</tt> actually belongs to the <tt>action</tt>.
     * 
     * @param <V>
     *            The type of the property's values.
     * @param action
     *            The property set action.
     * @param instance
     *            The property set action instance.
     */
    private <V> void userFiredPropertySetAction(PropertySetAction<V> action, PropertySetAction.Instance<?> instance) {
        changePropertyValue(action.getProperty(), action.getInstanceType().cast(instance).getNewValue());
    }

    /**
     * Called when this thing behavior decided to fire an event. This should only be called by the thing or its behavior! The default
     * implementation notifies local observers and then transports the event to other computers that might be interested. <b>It is
     * important</b> that the local behavior happens first!
     * 
     * @param <E>
     *            The type of the event instance.
     * @param eventInstance
     *            The event instance.
     */
    protected <E extends EventInstance> void executeEvent(E eventInstance) {
        Event<E> event = getEventFromInstance(eventInstance);
        notifyListeners(event, eventInstance);
        try {
            sendToServer(eventInstance);
        } catch (RequestException e) {
            logger.error("Error when executing event {}", eventInstance, e);
        }
    }

    /**
     * This should be implemented by the executing behavior to actually execute the action.
     * 
     * @param <A>
     *            The type of the action instance.
     * @param action
     *            The action.
     * @param actionInstance
     *            The (matching) action instance.
     */
    protected abstract <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance);

    @Override
    public void shutdown() {
        try {
            super.shutdown();
        } catch (Exception e) {
            logger.error("Error during shutdown of thing " + getThing().getId(), e);
        }
    }

    /**
     * Deletes the Thing from the server and shuts down the local instance.
     * 
     * @throws RequestException
     *             When the unregistering fails.
     */
    public void unregisterAndShutdown() throws RequestException {
        getClient().unregisterThing(getThing().getId());
        shutdown();
    }

    /**
     * Fetches the current state of the thing from the server and returns it for use by the executing client.
     * 
     * @param <T>
     *            The expected type of the thing.
     * @param <B>
     *            The type of the behavior that the factory creates.
     * @param thingType
     *            The expected type of the thing.
     * @param thingClient
     *            The ThingClient for communication with the server.
     * @param id
     *            The id of the thing.
     * @param behaviorFactory
     *            The behavior factory that will generate the thing's behavior.
     * @return The thing's behavior (you can access the thing through {@link ThingBehavior#getThing()}.
     * @throws RequestException
     *             When querying the server fails.
     */
    public static <T extends Thing, B extends ExecutingThingBehavior> B launchExistingThing(Class<T> thingType, ThingClient thingClient, long id, ThingBehaviorFactory<B> behaviorFactory) throws RequestException {
        Thing thing = thingClient.getExistingThing(id, behaviorFactory);
        if (!thingType.isInstance(thing)) {
            throw new IllegalArgumentException("The returned thing is of type " + thing.getClass() + " instead of " + thingType);
        }

        @SuppressWarnings("unchecked")
        B behavior = (B) thing.getBehavior();
        return behavior;
    }

    /**
     * Registers a new thing with the server and launches it with the given behavior. The new thing will be returned with its ID set.
     * 
     * @param <T>
     *            The type of the new thing.
     * @param <B>
     *            The type of the behavior that the factory creates.
     * @param thingType
     *            The type of the new thing.
     * @param thingClient
     *            The ThingClient for communication with the server.
     * @param name
     *            The name of the new thing.
     * @param behaviorFactory
     *            The behavior factory that will generate the thing's behavior.
     * @return The thing's behavior (you can access the thing through {@link ThingBehavior#getThing()}.
     * @throws RequestException
     *             When querying the server fails.
     */
    public static <T extends Thing, B extends ExecutingThingBehavior> B launchNewThing(Class<T> thingType, ThingClient thingClient, String name, ThingBehaviorFactory<B> behaviorFactory) throws RequestException {
        Thing thing = thingClient.registerNewThing(name, thingType.getName(), null, behaviorFactory);
        if (!thingType.isInstance(thing)) {
            throw new IllegalArgumentException("The returned thing is of type " + thing.getClass() + " instead of " + thingType);
        }
        @SuppressWarnings("unchecked")
        B behavior = (B) thing.getBehavior();
        return behavior;
    }
}
