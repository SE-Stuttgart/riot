package de.uni_stuttgart.riot.clientlibrary.thing.test;

import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

/**
 * An executing behavior for testing things. This behavior exposes most of the capabilities of a behavior so that unit tests can access
 * these features.
 * 
 * @author Philipp Keck
 */
public class TestExecutingThingBehavior extends ExecutingThingBehavior {

    public interface ActionInterceptor {
        <A extends ActionInstance> void fired(Action<A> action, A instance);
    }

    public ActionInterceptor actionInterceptor = Mockito.mock(ActionInterceptor.class);

    public TestExecutingThingBehavior(ThingClient thingClient) {
        super(thingClient);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        actionInterceptor.fired(action, actionInstance);
    }

    public <V> void setThingProperty(Property<V> property, V value) {
        ThingBehavior.silentSetThingProperty(property, value);
    }

    public <V> void setThingProperty(String propertyName, V value) {
        if (value == null) {
            throw new NullPointerException("This implementation does not support null values!");
        }
        @SuppressWarnings("unchecked")
        Property<V> property = (Property<V>) getThing().getProperty(propertyName, value.getClass());
        ThingBehavior.silentSetThingProperty(property, value);
    }

    @Override
    public <E extends EventInstance> void notifyListeners(Event<E> event, E eventInstance) {
        super.notifyListeners(event, eventInstance);
    }

    @SuppressWarnings("unchecked")
    public static ThingBehaviorFactory<TestExecutingThingBehavior> getMockFactory(ThingClient thingClient) {
        ThingBehaviorFactory<TestExecutingThingBehavior> mockBehaviorFactory = mock(ThingBehaviorFactory.class);
        when(mockBehaviorFactory.newBehavior(any(Class.class))).thenAnswer(new Answer<TestExecutingThingBehavior>() {
            public TestExecutingThingBehavior answer(InvocationOnMock invocation) throws Throwable {
                return new TestExecutingThingBehavior(thingClient);
            }
        });
        return mockBehaviorFactory;
    }

}
