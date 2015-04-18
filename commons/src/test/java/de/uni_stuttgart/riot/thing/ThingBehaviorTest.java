package de.uni_stuttgart.riot.thing;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.uni_stuttgart.riot.thing.test.TestThing;

/**
 * Tests the framework base-class {@link ThingBehavior} and especially the detection of wrong uses of the class.
 * 
 * @author Philipp Keck
 */
public class ThingBehaviorTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static class FakeBehavior extends ThingBehavior {
        protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
        }
    }

    ThingBehavior behavior = new FakeBehavior();
    TestThing thing = new TestThing(behavior);

    @Before
    public void setupThing() {
        thing.setId(1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnNullResolver() {
        new ThingBehavior(null) {
            protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
            }
        };
    }

    @Test
    public void shouldFailOnDoubleRegister() {
        thrown.expect(IllegalStateException.class);
        behavior.register(thing);
    }

    @Test
    public void shouldReturnEventsAndActions() {
        assertThat(behavior.getActions().values(), containsInAnyOrder(thing.getActions().toArray()));
        assertThat(behavior.getEvents().values(), containsInAnyOrder(thing.getEvents().toArray()));
    }

    @Test
    public void shouldGetActionFromInstance() {
        ActionInstance instance = new ActionInstance(thing.getSimpleAction());
        assertThat(behavior.getActionFromInstance(instance), sameInstance(thing.getSimpleAction()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnNullActionInstance() {
        behavior.getActionFromInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnForeignActionInstance() {
        TestThing thing2 = new TestThing(new FakeBehavior());
        thing2.setId(2L);
        behavior.getActionFromInstance(new ActionInstance(thing2.getSimpleAction()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnUnknownAction() {
        Action<ActionInstance> action = new Action<ActionInstance>(thing, "doesNotExist", ActionInstance.class);
        behavior.getActionFromInstance(new ActionInstance(action));
    }

    @Test
    public void shouldGetEventFromInstance() {
        EventInstance instance = new EventInstance(thing.getSimpleEvent());
        assertThat(behavior.getEventFromInstance(instance), sameInstance(thing.getSimpleEvent()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnNullEventInstance() {
        behavior.getEventFromInstance(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnForeignEventInstance() {
        TestThing thing2 = new TestThing(new FakeBehavior());
        thing2.setId(2L);
        behavior.getEventFromInstance(new EventInstance(thing2.getSimpleEvent()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnUnknownEvent() {
        Event<EventInstance> event = new Event<EventInstance>(thing, "doesNotExist", EventInstance.class);
        behavior.getEventFromInstance(new EventInstance(event));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyActionName() {
        behavior.newAction("", ActionInstance.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnNullActionType() {
        behavior.newAction("anotherTestType", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectDuplicateAction() {
        behavior.newAction("simpleAction", ActionInstance.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyEventName() {
        behavior.newEvent(null, EventInstance.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnNullEventType() {
        behavior.newEvent("anotherTestType", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectDuplicateEvent() {
        behavior.newEvent("simpleEvent", EventInstance.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEmptyPropertyName() {
        behavior.newProperty("", Long.class, 1L, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnDuplicatePropertyName() {
        behavior.newProperty("ref", Long.class, 1L, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnNullPropertyType() {
        behavior.newProperty("anotherTestProperty", null, 1L, null);
    }

    @Test
    public void shouldDetectPropertyChangeEventCollision() {
        behavior.newEvent("testProperty_change", EventInstance.class);
        thrown.expect(IllegalArgumentException.class);
        behavior.newProperty("testProperty", Long.class, 1L, null);
    }

    @Test
    public void shouldNotCreateSetActionForReadonly() {
        behavior.newAction("testProperty_set", ActionInstance.class);
        behavior.newProperty("testProperty", Long.class, 1L, null); // Should work just fine.
    }

    @Test
    public void shouldDetectPropertySetActionCollision() {
        behavior.newAction("testProperty_set", ActionInstance.class);
        thrown.expect(IllegalArgumentException.class);
        behavior.newWritableProperty("testProperty", Long.class, 1L, null);
    }

}
