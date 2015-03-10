package de.uni_stuttgart.riot.thing.client;

import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent.Instance;
import de.uni_stuttgart.riot.thing.rest.RegisterEventRequest;

/**
 * The behavior of a thing that is being mirrored locally. It should only be used with the {@link MirroringThingBehavior} instance that
 * created it.
 */
public class MirroredThingBehavior extends ClientThingBehavior {

    /**
     * The mirroring behavior that mirrors this mirrored behavior.
     */
    private final MirroringThingBehavior mirroringBehavior;

    /**
     * This dummy listener is registered to property change events of this monitored thing to mark them as "needed". The events will
     * register themselves with the server to fetch updates as long as there is a listener present.
     */
    private final EventListener<PropertyChangeEvent.Instance<?>> dummyListener = new EventListener<PropertyChangeEvent.Instance<?>>() {
        public void onFired(Event<? extends Instance<?>> event, Instance<?> eventInstance) {
            // We do not need to do anything here, since PropertyChangeEvents are automatically evaluated by the Property anyway.
        }
    };

    /**
     * If true, the monitoring is active. This means that all property changes will regularly be fetched from the thing.
     */
    private boolean monitored;

    /**
     * Creates a new behavior instance. Each mirrored behavior is attached to a mirroring behavior and can only be used with that one.
     * 
     * @param mirroringBehavior
     *            The mirroring behavior that mirrors the new mirrored behavior.
     */
    public MirroredThingBehavior(MirroringThingBehavior mirroringBehavior) {
        super(mirroringBehavior.getDelegatingResolver(), mirroringBehavior.getClient());
        this.mirroringBehavior = mirroringBehavior;
    }

    @Override
    protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
        // We delegate this to the mirroring thing behavior so that it can decide what precisely needs to be done.
        mirroringBehavior.userFiredMirroredAction(actionInstance);
    }

    /**
     * Fires the given event locally, which will mostly call the listeners. Note that for {@link PropertyChangeEvent}s this will also change
     * the property's value.
     * 
     * @param <E>
     *            The type of the event instance.
     * @param eventInstance
     *            The event instance.
     */
    <E extends EventInstance> void fireEvent(E eventInstance) {
        notifyListeners(getEventFromInstance(eventInstance), eventInstance);
    }

    @Override
    protected <E extends EventInstance> void listenerAdded(Event<E> event, EventListener<? super E> listener, boolean wasFirst) {
        if (wasFirst) {
            if (event.getThing() != getThing()) {
                throw new IllegalArgumentException("Called with wrong event that does not belong to this thing!");
            }
            mirroringBehavior.registerToEvent(new RegisterEventRequest(getThing().getId(), event.getName()));
        }
    }

    @Override
    protected <E extends EventInstance> void listenerRemoved(Event<E> event, EventListener<? super E> listener, boolean wasLast) {
        if (wasLast) {
            if (event.getThing() != getThing()) {
                throw new IllegalArgumentException("Called with wrong event that does not belong to this thing!");
            }
            mirroringBehavior.unregisterToEvent(new RegisterEventRequest(getThing().getId(), event.getName()));
        }
    }

    /**
     * Determines if the monitoring is active. See {@link #startMonitoring()}.
     * 
     * @return <tt>true</tt> if the monitoring is active.
     */
    public boolean isMonitored() {
        return monitored;
    }

    /**
     * Activates the monitoring. This means that the change-events of all properties of the thing will be registered and whenever
     * {@link MirroringThingBehavior#fetchUpdates()} is called, these properties will be updated locally, too. The registration is done by
     * registering the {@link #dummyListener} with the events.
     */
    synchronized void startMonitoring() {
        if (monitored) {
            return;
        }

        boolean startedMultiRequest = mirroringBehavior.startMultipleEventsRequest();
        try {
            for (Property<?> property : getProperties().values()) {
                property.getChangeEvent().register(dummyListener);
            }
        } finally {
            if (startedMultiRequest) {
                mirroringBehavior.finishMultipleEventsRequest();
            }
        }

        monitored = true;
    }

    /**
     * Deactivates the monitoring. See {@link #startMonitoring()}.
     */
    synchronized void stopMonitoring() {
        if (!monitored) {
            return;
        }

        boolean startedMultiRequest = mirroringBehavior.startMultipleEventsRequest();
        try {
            for (Property<?> property : getProperties().values()) {
                property.getChangeEvent().unregister(dummyListener);
            }
        } finally {
            if (startedMultiRequest) {
                mirroringBehavior.finishMultipleEventsRequest();
            }
        }
    }

    @Override
    protected void shutdown() throws Exception {
        stopMonitoring();
    }

}
