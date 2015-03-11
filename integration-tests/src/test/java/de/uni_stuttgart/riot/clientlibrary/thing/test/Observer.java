package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.references.DelegatingReferenceResolver;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * A {@Thing} that does not have any own properties, events or actions, but simply serves to register with the server for tests.
 */
public class Observer extends Thing {

    /**
     * Creates a new Observer.
     * 
     * @param name
     *            The name of the observer.
     * @param behavior
     *            The behavior of the observer. For testing purposes, just use {@link ObserverBehavior}.
     */
    public Observer(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

    /**
     * Creates a new observer for testing purposes.
     * 
     * @param client
     *            The ThingClient to communicate with the server.
     * @param name
     *            The name of the observer.
     * @return The behavior of the observer. Call {@link ObserverBehavior#getObserver()} to get the observer.
     * @throws RequestException
     *             When registering the thing with the server failed.
     * @throws IOException
     *             When a network error occured.
     */
    public static ObserverBehavior create(ThingClient client, String name) throws RequestException, IOException {
        @SuppressWarnings("unchecked")
        ThingBehaviorFactory<ObserverBehavior> behaviorFactory = mock(ThingBehaviorFactory.class);
        when(behaviorFactory.newBehavior(anyLong(), anyString(), any())).thenReturn(new ObserverBehavior(client));
        return ExecutingThingBehavior.launchNewThing(Observer.class, client, name, behaviorFactory);
    }

    /**
     * A behavior that exposes many things to unit tests. Note that this behavior would usually be a runnable behavior. For the purpose of
     * testing, however, the {@link #fetchUpdates()} method should be called manually.
     */
    public static class ObserverBehavior extends ExecutingThingBehavior {

        /**
         * Creates a new ObserverBehavior.
         * 
         * @param thingClient
         *            The client that handles the REST operations
         */
        public ObserverBehavior(ThingClient thingClient) {
            super(thingClient);
        }

        public Observer getObserver() {
            return (Observer) getThing();
        }

        public <T extends Thing> T observe(long id, Class<T> expectedType) throws NotFoundException, IOException {
            return super.getOtherThing(id, expectedType);
        }

        @Override
        public void startMonitoring(Thing otherThing) {
            super.startMonitoring(otherThing);
        }

        @Override
        public void stopMonitoring(Thing otherThing) {
            super.stopMonitoring(otherThing);
        }

        public void updateObserved(Thing observedThing) throws IOException, NotFoundException {
            super.updateThingState(observedThing);
        }

        @Override
        protected void fetchUpdates() throws IOException, NotFoundException {
            super.fetchUpdates();
        }

        @Override
        protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
            // The Observer does not have any actions, so nothing to do here.
        }

        @Override
        protected DelegatingReferenceResolver getDelegatingResolver() {
            return super.getDelegatingResolver();
        }

    }

}
