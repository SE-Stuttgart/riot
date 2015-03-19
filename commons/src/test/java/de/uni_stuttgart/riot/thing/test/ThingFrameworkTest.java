package de.uni_stuttgart.riot.thing.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.SimpleResolver;
import de.uni_stuttgart.riot.references.TestReferenceable;
import de.uni_stuttgart.riot.references.TypedReferenceResolver;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.BaseInstanceDescription;
import de.uni_stuttgart.riot.thing.BasePropertyListener;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.PropertySetAction;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.PropertySetAction.Instance;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Tests the Java-only part of the Thing framework.
 * 
 * @author Philipp Keck
 */
public class ThingFrameworkTest {

    @Test
    public void testBehaviorRegistration() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        assertThat(thing, sameInstance(behavior.getThing()));
        assertThat(behavior, sameInstance(thing.getBehavior()));
    }

    @Test
    public void testSpecialGetters() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        assertThat(thing.getIntProperty(), sameInstance(thing.getWritableProperty("int", Integer.class)));
        assertThat(thing.getIntProperty(), sameInstance(thing.getProperty("int", Integer.class)));
        assertThat(thing.getWritableProperty("int") == thing.getIntProperty(), is(true));
        assertThat(thing.getProperty("int") == thing.getIntProperty(), is(true));

        assertThat(thing.getReadonlyStringProperty(), sameInstance(thing.getProperty("readonlyString", String.class)));
        assertThat(thing.getReadonlyString(), is(thing.getProperty("readonlyString", String.class).get()));

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
        TestThing thing1 = ThingFactory.create(TestThing.class, 100, behavior1);
        TestThing thing2 = ThingFactory.create(TestThing.class, 100, behavior2);
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
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);

        // Register a listener.
        EventListener<EventInstance> listener = (EventListener<EventInstance>) mock(EventListener.class);
        thing.getSimpleEvent().register(listener);

        // Check that it is fired.
        EventInstance eventInstance = new EventInstance(thing.getSimpleEvent());
        behavior.notifyListeners(thing.getSimpleEvent(), eventInstance);
        verify(listener, times(1)).onFired(thing.getSimpleEvent(), eventInstance);

        // Check that is not fired when unregistered.
        reset(listener);
        thing.getSimpleEvent().unregister(listener);
        EventInstance eventInstance2 = new EventInstance(thing.getSimpleEvent());
        behavior.notifyListeners(thing.getSimpleEvent(), eventInstance2);
        verify(listener, never()).onFired(Matchers.same(thing.getSimpleEvent()), Matchers.any(EventInstance.class));
    }

    @Test
    public void testActions() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);

        // Fire an action, check that is transported to the behavior.
        ActionInstance actionInstance = new ActionInstance(thing.getSimpleAction());
        thing.getSimpleAction().fire(actionInstance);
        verify(behavior.actionInterceptor, times(1)).fired(actionInstance);

        // Do the same for PropertySetActions fired indirectly.
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
    public void testPropertyListeners() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        behavior.executePropertyChangesDirectly = true;
        thing.setInt(100);
        thing.setLong(200);

        // Register a property listener (which is for Objects on purpose, to be generic).
        @SuppressWarnings("unchecked")
        BasePropertyListener<Object> listener = mock(BasePropertyListener.class);
        thing.getIntProperty().register(listener);
        thing.getLongProperty().register(listener);

        // Check that it works.
        thing.setInt(101);
        verify(listener, times(1)).onChange(thing.getIntProperty(), 100, 101);
        thing.setLong(201);
        verify(listener, times(1)).onChange(thing.getLongProperty(), 200L, 201L);
    }

    @Test
    public void testReferences() throws ResolveReferenceException {

        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        TestReferenceable referenceable = new TestReferenceable(10L);

        // Check the types reported by the property.
        assertThat(thing.getRefProperty().getTarget(), nullValue());
        assertThat(thing.getRefProperty().get(), nullValue());
        assertThat(thing.getRefProperty().getTargetType(), isClass(TestReferenceable.class));
        assertThat(thing.getRefProperty().getValueType(), isClass(Long.class));

        // Set a concrete value that has an ID.
        ThingState.silentSetThingProperty(thing.getRefProperty(), referenceable);
        assertThat(thing.getRefProperty().get(), is(10L));

        // Try the Getter, which will call the resolver.
        @SuppressWarnings("unchecked")
        TypedReferenceResolver<TestReferenceable> testResolver = mock(TypedReferenceResolver.class);
        when(testResolver.resolve(10L)).thenReturn(referenceable);
        behavior.getDelegatingResolver().addResolver(TestReferenceable.class, testResolver);
        assertThat(thing.getRefProperty().getTarget(), sameInstance(referenceable));
        verify(testResolver, times(1)).resolve(10L);

        // Fire an action with a referenced value inside.
        TestRefActionInstance actionInstance = new TestRefActionInstance(thing.getRefAction(), referenceable);
        thing.getRefAction().fire(actionInstance);
        verify(behavior.actionInterceptor, times(1)).fired(actionInstance);
        assertThat(actionInstance.getParameter().getId(), is(10L));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForUnpersistedReferences() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        new TestRefActionInstance(thing.getRefAction(), new TestReferenceable(null));
    }

    @SuppressWarnings("unchecked")
    @Test(expected = ResolveReferenceException.class)
    public void shouldFailForUnresolvableReference() throws ResolveReferenceException {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        TypedReferenceResolver<TestReferenceable> testResolver = mock(TypedReferenceResolver.class);
        when(testResolver.resolve(10L)).thenThrow(ResolveReferenceException.class);
        behavior.getDelegatingResolver().addResolver(TestReferenceable.class, testResolver);
        ThingState.silentSetThingProperty(thing.getRefProperty(), 10L);
        thing.getRefProperty().getTarget();
    }

    @Test
    public void testThingState() throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {

        // Create a TestThing that will hold its initial state.
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);

        // Check that ThingState reports this initial state correctly.
        ThingState state = ThingState.create(thing);
        assertThat(state.isEmpty(), is(false));
        assertThat((Integer) state.get("int"), is(42));
        assertThat((Long) state.get("long"), is(4242L));
        assertThat((String) state.get("readonlyString"), is("InitialString"));
        assertThat(state.get("ref"), nullValue());

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
        reserializedState.set("ref", 42L);
        reserializedState.apply(thing);
        assertThat(thing.getInt(), is(43));
        assertThat(thing.getLong(), is(4343L));
        assertThat(thing.getReadonlyString(), is(nullValue()));
        assertThat(thing.getRefProperty().getId(), is(42L));

    }

    // CHECKSTYLE: NCSS OFF
    @Test
    public void testThingDescription() throws IOException {

        // Create a TestThing that we will describe.
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);

        // Check the main Thing description.
        ThingDescription description = ThingDescription.create(thing);
        assertThat(description.getType(), isClass(TestThing.class));

        // Ensure that the description is serializable by Jackson without exceptions.
        ObjectMapper mapper = new ObjectMapper();
        description = mapper.readValue(mapper.writeValueAsString(description), ThingDescription.class);

        // Check the events.
        assertThat(description.getEvents(), hasSize(2));
        assertThat(description.getEventByName("simpleEvent").getInstanceDescription().getInstanceType() == EventInstance.class, is(true));
        assertThat(description.getEventByName("simpleEvent").getInstanceDescription().getParameters().isEmpty(), is(true));
        BaseInstanceDescription parEventInstance = description.getEventByName("parameterizedEvent").getInstanceDescription();
        assertThat(parEventInstance.getInstanceType(), isClass(TestEventInstance.class));
        assertThat(parEventInstance.getParameters().size(), is(1));
        assertThat(parEventInstance.getParameters().get(0).getName(), is("parameter"));
        assertThat(parEventInstance.getParameters().get(0).getValueType(), isClass(Integer.class));

        // Check the actions.
        assertThat(description.getActions(), hasSize(3));
        assertThat(description.getActionByName("simpleAction").getInstanceDescription().getInstanceType() == ActionInstance.class, is(true));
        assertThat(description.getActionByName("simpleAction").getInstanceDescription().getParameters().isEmpty(), is(true));
        BaseInstanceDescription parActionInstance = description.getActionByName("parameterizedAction").getInstanceDescription();
        assertThat(parActionInstance.getInstanceType(), isClass(TestActionInstance.class));
        assertThat(parActionInstance.getParameters().size(), is(1));
        assertThat(parActionInstance.getParameters().get(0).getName(), is("parameter"));
        assertThat(parActionInstance.getParameters().get(0).getValueType(), isClass(Integer.class));
        assertThat(parActionInstance.getParameters().get(0).isReference(), is(false));
        BaseInstanceDescription refActionInstance = description.getActionByName("refAction").getInstanceDescription();
        assertThat(refActionInstance.getInstanceType(), isClass(TestRefActionInstance.class));
        assertThat(refActionInstance.getParameters().size(), is(1));
        assertThat(refActionInstance.getParameters().get(0).getName(), is("parameter"));
        assertThat(refActionInstance.getParameters().get(0).getValueType(), isClass(TestReferenceable.class));
        assertThat(refActionInstance.getParameters().get(0).isReference(), is(true));

        // Check the properties.
        assertThat(description.getProperties(), hasSize(5));
        assertThat(description.getPropertyByName("int").getValueType(), isClass(Integer.class));
        assertThat(description.getPropertyByName("long").getValueType(), isClass(Long.class));
        assertThat(description.getPropertyByName("percent").getValueType(), isClass(Double.class));
        assertThat(description.getPropertyByName("readonlyString").getValueType(), isClass(String.class));
        assertThat(description.getPropertyByName("readonlyString").isReference(), is(false));
        assertThat(description.getPropertyByName("ref").isReference(), is(true));
        assertThat(description.getPropertyByName("ref").getValueType(), isClass(TestReferenceable.class));

        // Check the UI hints
        assertThat(parEventInstance.getParameters().get(0).getUiHint(), instanceOf(UIHint.EditNumber.class));
        UIHint.IntegralSlider actionParamHint = (UIHint.IntegralSlider) parActionInstance.getParameters().get(0).getUiHint();
        assertThat(actionParamHint.min, is(0L));
        assertThat(actionParamHint.max, is(10000L));

        UIHint.IntegralSlider intPropertyHint = (UIHint.IntegralSlider) description.getPropertyByName("int").getUiHint();
        assertThat(intPropertyHint.min, is(0L));
        assertThat(intPropertyHint.max, is(10000L));

        assertThat(description.getPropertyByName("long").getUiHint(), instanceOf(UIHint.EditNumber.class));
        assertThat(description.getPropertyByName("percent").getUiHint(), instanceOf(UIHint.PercentageSlider.class));

        UIHint.ReferenceDropDown refHint = (UIHint.ReferenceDropDown) description.getPropertyByName("ref").getUiHint();
        assertThat(refHint.targetType, isClass(TestReferenceable.class));
        UIHint.ReferenceDropDown refActionHint = (UIHint.ReferenceDropDown) refActionInstance.getParameters().get(0).getUiHint();
        assertThat(refActionHint.targetType, isClass(TestReferenceable.class));
    }

    @Test
    public void testParentHierarchy() throws ResolveReferenceException {

        // Create test things.
        final TestThingBehavior behavior1 = new TestThingBehavior();
        final TestThing grandpa = ThingFactory.create(TestThing.class, 52, behavior1);
        final TestThingBehavior behavior2 = new TestThingBehavior();
        final TestThing dad = ThingFactory.create(TestThing.class, 53, behavior2);
        final TestThingBehavior behavior3 = new TestThingBehavior();
        final TestThing son = ThingFactory.create(TestThing.class, 54, behavior3);

        // Register them with their resolvers.
        TypedReferenceResolver<Thing> thingResolver = SimpleResolver.create(grandpa, dad, son);
        behavior1.getDelegatingResolver().addResolver(Thing.class, thingResolver);
        behavior2.getDelegatingResolver().addResolver(Thing.class, thingResolver);
        behavior3.getDelegatingResolver().addResolver(Thing.class, thingResolver);

        // Establish reasonable relations.
        son.setParent(dad);
        dad.setParent(grandpa);
        assertThat(son.getParent(), sameInstance((Thing) dad));
        assertThat(dad.getParent(), sameInstance((Thing) grandpa));
        assertThat(son.hasAncestor(son), is(false));
        assertThat(son.hasAncestor(dad), is(true));
        assertThat(son.hasAncestor(grandpa), is(true));
        assertThat(dad.hasAncestor(son), is(false));
        assertThat(dad.hasAncestor(dad), is(false));
        assertThat(dad.hasAncestor(grandpa), is(true));
        assertThat(grandpa.hasAncestor(son), is(false));
        assertThat(grandpa.hasAncestor(dad), is(false));
        assertThat(grandpa.hasAncestor(grandpa), is(false));

        // Try to introduce a loop.
        try {
            grandpa.setParent(son);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            grandpa.setParent(dad);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            grandpa.setParent(grandpa);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            son.setParent(son);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected.
        }

        // Undo one of the relations.
        dad.setParent(null);
        assertThat(dad.hasAncestor(grandpa), is(false));
        assertThat(son.hasAncestor(grandpa), is(false));
        assertThat(son.hasAncestor(dad), is(true));
    }

    private static Matcher<Class<?>> isClass(Class<?> clazz) {
        return CoreMatchers.<Class<?>> sameInstance(clazz);
    }

}
