package de.uni_stuttgart.riot.thing.client;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.rest.RegisterRequest;

/**
 * The behavior of a thing that is being mirrored locally. It should only be used with the {@link MirroringThingBehavior} instance that
 * created it.
 */
public class MirroredThingBehavior extends ClientThingBehavior {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(MirroredThingBehavior.class);

    /**
     * The mirroring behavior that mirrors this mirrored behavior.
     */
    private final MirroringThingBehavior mirroringBehavior;

    /**
     * The events that this mirrored thing has registered to on the server.
     */
    private final Set<String> registeredEvents = new HashSet<String>();

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
        try {
            getClient().submitAction(actionInstance);
        } catch (RequestException e) {
            logger.error("Could not send action instance {} for mirrored thing {}", actionInstance, getThing().getId(), e);
        } catch (IOException e) {
            logger.error("Could not send action instance {} for mirrored thing {}", actionInstance, getThing().getId(), e);
        }
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
            RegisterRequest request = new RegisterRequest(getThing().getId(), event.getName());
            try {
                getClient().registerToEvent(mirroringBehavior.getThing().getId(), request);
                registeredEvents.add(event.getName());
            } catch (RequestException e) {
                logger.error("Could not register to event " + event.getName() + " of thing " + getThing().getId());
            } catch (IOException e) {
                logger.error("Could not register to event " + event.getName() + " of thing " + getThing().getId());
            }
        }
    }

    @Override
    protected <E extends EventInstance> void listenerRemoved(Event<E> event, EventListener<? super E> listener, boolean wasLast) {
        if (wasLast) {
            if (event.getThing() != getThing()) {
                throw new IllegalArgumentException("Called with wrong event that does not belong to this thing!");
            }
            RegisterRequest request = new RegisterRequest(getThing().getId(), event.getName());
            try {
                getClient().unregisterFromEvent(mirroringBehavior.getThing().getId(), request);
                registeredEvents.remove(event.getName());
            } catch (RequestException e) {
                logger.error("Could not unregister from event " + event.getName() + " of thing " + getThing().getId());
            } catch (IOException e) {
                logger.error("Could not unregister from event " + event.getName() + " of thing " + getThing().getId());
            }
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
     * {@link MirroringThingBehavior#fetchUpdates()} is called, these properties will be updated locally, too.
     */
    void startMonitoring() {
        if (monitored) {
            return;
        }

        Set<String> eventNames = new HashSet<String>();
        Collection<RegisterRequest> requests = new ArrayList<RegisterRequest>();
        for (Property<?> property : getProperties().values()) {
            eventNames.add(property.getChangeEvent().getName());
            requests.add(new RegisterRequest(getThing().getId(), property.getChangeEvent().getName()));
        }

        try {
            getClient().registerToEvents(mirroringBehavior.getThing().getId(), requests);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        registeredEvents.addAll(eventNames);
    }

    /**
     * Deactivates the monitoring. See {@link #startMonitoring()}.
     */
    void stopMonitoring() {
        if (!monitored) {
            return;
        }

        Set<String> eventNames = new HashSet<String>();
        Collection<RegisterRequest> requests = new ArrayList<RegisterRequest>();
        for (Property<?> property : getProperties().values()) {
            eventNames.add(property.getChangeEvent().getName());
            requests.add(new RegisterRequest(getThing().getId(), property.getChangeEvent().getName()));
        }

        try {
            getClient().unregisterFromEvents(mirroringBehavior.getThing().getId(), requests);
        } catch (RequestException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        registeredEvents.removeAll(eventNames);
    }

    @Override
    protected void shutdown() throws Exception {
        unregisterAllEvents();
        monitored = false;
    }

    /**
     * Unregisters all registered events for this thing.
     * 
     * @throws RequestException
     *             When the request failed.
     * @throws IOException
     *             When a network error occured.
     */
    private void unregisterAllEvents() throws RequestException, IOException {
        if (registeredEvents.isEmpty()) {
            return;
        }

        Collection<RegisterRequest> requests = new ArrayList<RegisterRequest>(registeredEvents.size());
        for (String eventName : registeredEvents) {
            requests.add(new RegisterRequest(getThing().getId(), eventName));
        }
        getClient().unregisterFromEvents(mirroringBehavior.getThing().getId(), requests);
    }

}
