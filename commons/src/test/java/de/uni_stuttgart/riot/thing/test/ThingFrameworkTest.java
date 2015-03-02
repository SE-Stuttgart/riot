package de.uni_stuttgart.riot.thing.test;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.PropertySetAction;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.PropertySetAction.Instance;
import de.uni_stuttgart.riot.thing.ThingFactory;

/**
 * Tests the Java-only part of the Thing framework.
 * 
 * @author Philipp Keck
 */
public class ThingFrameworkTest {

    @Test
    public void testBehaviorRegistration() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, "Thing", behavior);
        assertThat(thing, sameInstance(behavior.getThing()));
        assertThat(behavior, sameInstance(thing.getBehavior()));
    }

    @Test
    public void testSpecialGetters() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, "Thing", behavior);
        assertThat(thing.getIntProperty(), sameInstance(thing.getWritableProperty("int", Integer.class)));
        assertThat(thing.getIntProperty(), sameInstance(thing.getProperty("int", Integer.class)));
        assertThat(thing.getWritableProperty("int") == thing.getIntProperty(), is(true));
        assertThat(thing.getProperty("int") == thing.getIntProperty(), is(true));

        assertThat(thing.getReadonlyStringProperty(), sameInstance(thing.getProperty("readonlyString", String.class)));
        assertThat(thing.getReadonlyString(), is(thing.getProperty("readonlyString", String.class).getValue()));

        assertThat(thing.getSimpleAction(), sameInstance(thing.getAction("simpleAction", ActionInstance.class)));
        assertThat(thing.getSimpleAction() == thing.getAction("simpleAction"), is(true));
        assertThat(thing.getParameterizedAction(), sameInstance(thing.getAction("parameterizedAction", TestActionInstance.class)));

        assertThat(thing.getSimpleEvent(), sameInstance(thing.getEvent("simpleEvent", EventInstance.class)));
        assertThat(thing.getSimpleEvent() == thing.getEvent("simpleEvent"), is(true));
        assertThat(thing.getParameterizedEvent(), sameInstance(thing.getEvent("parameterizedEvent", TestEventInstance.class)));

        assertThat(thing.getIntProperty().getChangeEvent() == thing.getEvent("int_change"), is(true));
        assertThat(thing.getIntProperty().getSetAction() == thing.getAction("int_set"), is(true));
    }

    @Test
    public void testThingEquals() {

        // Identically created things should be equal
        TestThingBehavior behavior1 = new TestThingBehavior();
        TestThingBehavior behavior2 = new TestThingBehavior();
        TestThing thing1 = ThingFactory.create(TestThing.class, 100, "Thing", behavior1);
        TestThing thing2 = ThingFactory.create(TestThing.class, 100, "Thing", behavior2);
        assertThat(thing1.equals(thing2), is(true));
        assertThat(thing2.equals(thing1), is(true));
        assertThat(thing1.hashCode(), is(thing2.hashCode()));

        // Changing their names should make them unequal
        thing1.setName("ThingX");
        assertThat(thing1.equals(thing2), is(false));
        assertThat(thing2.equals(thing1), is(false));
        thing2.setName("ThingX");
        assertThat(thing1.equals(thing2), is(true));
        assertThat(thing2.equals(thing1), is(true));

        // Changing a property value should make them unequal
        behavior1.setThingProperty(thing1.getIntProperty(), thing1.getInt() + 1);
        assertThat(thing1.equals(thing2), is(false));
        assertThat(thing2.equals(thing1), is(false));
        behavior2.setThingProperty(thing2.getIntProperty(), thing1.getInt());
        assertThat(thing1.equals(thing2), is(true));
        assertThat(thing2.equals(thing1), is(true));

        // Same for the string property
        behavior1.setThingProperty(thing1.getReadonlyStringProperty(), "SomeOtherString");
        assertThat(thing1.equals(thing2), is(false));
        assertThat(thing2.equals(thing1), is(false));
        behavior2.setThingProperty(thing2.getReadonlyStringProperty(), "SomeOtherString");
        assertThat(thing1.equals(thing2), is(true));
        assertThat(thing2.equals(thing1), is(true));
        assertThat(thing1.hashCode(), is(thing2.hashCode()));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testEvents() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, "Thing", behavior);

        EventListener<EventInstance> listener = (EventListener<EventInstance>) mock(EventListener.class);
        thing.getSimpleEvent().register(listener);

        EventInstance eventInstance = new EventInstance(thing.getSimpleEvent());
        behavior.notifyListeners(thing.getSimpleEvent(), eventInstance);
        verify(listener, times(1)).onFired(thing.getSimpleEvent(), eventInstance);

        reset(listener);
        thing.getSimpleEvent().unregister(listener);
        EventInstance eventInstance2 = new EventInstance(thing.getSimpleEvent());
        behavior.notifyListeners(thing.getSimpleEvent(), eventInstance2);
        verify(listener, never()).onFired(Matchers.same(thing.getSimpleEvent()), Matchers.any(EventInstance.class));
    }

    @Test
    public void testActions() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, "Thing", behavior);

        ActionInstance actionInstance = new ActionInstance(thing.getSimpleAction());
        thing.getSimpleAction().fire(actionInstance);
        verify(behavior.actionInterceptor, times(1)).fired(actionInstance);

        reset(behavior.actionInterceptor);
        thing.setInt(1001);
        ArgumentCaptor<ActionInstance> actionCaptor = ArgumentCaptor.forClass(ActionInstance.class);
        verify(behavior.actionInterceptor, times(1)).fired(actionCaptor.capture());
        assertThat(actionCaptor.getValue(), instanceOf(PropertySetAction.Instance.class));

        @SuppressWarnings("unchecked")
        PropertySetAction.Instance<Integer> setActionInstance = (Instance<Integer>) actionCaptor.getValue();
        assertThat(setActionInstance.getName(), is("int_set"));
        assertThat(setActionInstance.getThingId(), is(42L));
        assertThat(setActionInstance.getNewValue(), is(1001));
    }

    @Test
    public void testThingState() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {

        // Create a TestThing that will hold its initial state.
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, "Thing", behavior);

        // Check that ThingState reports this initial state correctly.
        ThingState state = ThingState.create(thing);
        assertThat(state.isEmpty(), is(false));
        assertThat((Integer) state.get("int"), is(42));
        assertThat((Long) state.get("long"), is(4242L));
        assertThat((String) state.get("readonlyString"), is("InitialString"));

        // Serialize and deserialize the state, check if stays the same.
        ObjectMapper mapper = new ObjectMapper();
        ThingState reserializedState = mapper.readValue(mapper.writeValueAsString(state), ThingState.class);
        assertThat(reserializedState, equalTo(state));

        // Do the same with a null value inside.
        state.set("readonlyString", null);
        reserializedState = mapper.readValue(mapper.writeValueAsString(state), ThingState.class);
        assertThat(reserializedState, equalTo(state));
        assertThat(reserializedState.get("readonlyString"), is(nullValue()));

        // Change the ThingState, apply it to the thing and check if that actually happened.
        reserializedState.set("int", 43);
        reserializedState.set("long", 4343L);
        reserializedState.apply(thing);
        assertThat(thing.getInt(), is(43));
        assertThat(thing.getLong(), is(4343L));
        assertThat(thing.getReadonlyString(), is(nullValue()));

    }
}