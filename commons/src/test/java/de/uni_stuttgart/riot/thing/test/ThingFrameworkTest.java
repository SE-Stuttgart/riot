package de.uni_stuttgart.riot.thing.test;

import static org.mockito.Mockito.spy;
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
import java.util.ArrayList;
import java.util.Collections;

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
import de.uni_stuttgart.riot.thing.BaseInstanceDescriptions;
import de.uni_stuttgart.riot.thing.BasePropertyListener;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.ParameterDescription;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertySetAction;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingDescription;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.PropertySetAction.Instance;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
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

        assertThat(thing.getEvent("doesNotExist"), is(nullValue()));
        assertThat(thing.getAction("doesNotExist"), is(nullValue()));
        assertThat(thing.getProperty("doesNotExist"), is(nullValue()));

        assertThat(thing.getEvents(), hasItem(thing.getSimpleEvent()));
        assertThat(thing.getEvents(), hasItem(thing.getSimpleNotification()));
        assertThat(thing.getEvents(), hasItem(thing.getParameterizedEvent()));
        assertThat(thing.getActions(), hasItem(thing.getSimpleAction()));
        assertThat(thing.getActions(), hasItem(thing.getParameterizedAction()));
        assertThat(thing.getProperties(), hasItem(thing.getIntProperty()));
        assertThat(thing.getProperties(), hasItem(thing.getReadonlyStringProperty()));

        // These should just work, not fail, the exact return value is unimportant (only for debugging):
        thing.toString();
        thing.getSimpleAction().toString();
        thing.getSimpleEvent().toString();
        thing.getSimpleNotification().toString();
        thing.getIntProperty().toString();
        thing.getIntProperty().getChangeEvent().toString();
        thing.getIntProperty().getSetAction().toString();
        thing.getReadonlyStringProperty().toString();
        thing.getReadonlyRefProperty().toString();
        thing.getRefProperty().toString();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnEventMismatch() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        thing.getEvent("parameterizedEvent", EventInstance.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnActionMismatch() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        thing.getAction("parameterizedAction", ActionInstance.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnPropertyMismatch() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        thing.getProperty("int", Long.class);
    }

    @Test
    public void shouldFailOnReadonlyProperty() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        assertThat(thing.getProperty("readonlyString"), sameInstance((Object) thing.getReadonlyStringProperty()));
        assertThat(thing.getProperty("readonlyString", String.class), sameInstance(thing.getReadonlyStringProperty()));
        try {
            thing.getWritableProperty("readonlyString");
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            thing.getWritableProperty("readonlyString", String.class);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
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
        assertThat(thing1.hashCode(), is(not(thing2.hashCode())));
        thing2.setName("ThingX");
        assertThat(thing1.equals(thing2), is(true));
        assertThat(thing2.equals(thing1), is(true));

        // Changing a property value should make them unequal
        behavior1.setThingProperty(thing1.getIntProperty(), thing1.getInt() + 1);
        assertThat(thing1.equals(thing2), is(false));
        assertThat(thing2.equals(thing1), is(false));
        assertThat(thing1.hashCode(), is(not(thing2.hashCode())));
        behavior2.setThingProperty(thing2.getIntProperty(), thing1.getInt());
        assertThat(thing1.equals(thing2), is(true));
        assertThat(thing2.equals(thing1), is(true));

        // Changing the ID should make them unequal
        thing1.setId(101L);
        assertThat(thing1.equals(thing2), is(false));
        assertThat(thing2.equals(thing1), is(false));
        assertThat(thing1.hashCode(), is(not(thing2.hashCode())));
        thing1.setId(100L);
        assertThat(thing1.equals(thing2), is(true));
        assertThat(thing2.equals(thing1), is(true));

        // Same for the string property
        behavior1.setThingProperty(thing1.getReadonlyStringProperty(), "SomeOtherString");
        assertThat(thing1.equals(thing2), is(false));
        assertThat(thing2.equals(thing1), is(false));
        assertThat(thing1.hashCode(), is(not(thing2.hashCode())));
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
        TestThingBehavior behavior = spy(new TestThingBehavior());
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);

        // Fire an action, check that is transported to the behavior.
        ActionInstance actionInstance = new ActionInstance(thing.getSimpleAction());
        thing.getSimpleAction().fire(actionInstance);
        verify(behavior, times(1)).userFiredAction(actionInstance);

        // Try firing it for the wrong Thing.
        try {
            TestThing thing2 = ThingFactory.create(TestThing.class, 43, new TestThingBehavior());
            thing.getSimpleAction().fire(new ActionInstance(thing2.getSimpleAction()));
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Do the same for PropertySetActions fired indirectly.
        reset(behavior);
        thing.setInt(1001);
        ArgumentCaptor<ActionInstance> actionCaptor = ArgumentCaptor.forClass(ActionInstance.class);
        verify(behavior, times(1)).userFiredAction(actionCaptor.capture());
        assertThat(actionCaptor.getValue(), instanceOf(PropertySetAction.Instance.class));

        @SuppressWarnings("unchecked")
        PropertySetAction.Instance<Integer> setActionInstance = (Instance<Integer>) actionCaptor.getValue();
        assertThat(setActionInstance.getName(), is("int_set"));
        assertThat(setActionInstance.getThingId(), is(42L));

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

        TestThingBehavior behavior = spy(new TestThingBehavior());
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        TestReferenceable referenceable = new TestReferenceable(10L);

        // Check the types reported by the property.
        assertThat(thing.getRefProperty().getTarget(), nullValue());
        assertThat(thing.getRefProperty().get(), nullValue());
        assertThat(thing.getRefProperty().getTargetType(), isClass(TestReferenceable.class));
        assertThat(thing.getRefProperty().getValueType(), isClass(Long.class));
        assertThat(thing.getReadonlyRefProperty().getTarget(), nullValue());
        assertThat(thing.getReadonlyRefProperty().get(), nullValue());
        assertThat(thing.getReadonlyRefProperty().getTargetType(), isClass(TestReferenceable.class));
        assertThat(thing.getReadonlyRefProperty().getValueType(), isClass(Long.class));

        // Set a concrete value that has an ID.
        ThingState.silentSetThingProperty(thing.getRefProperty(), referenceable);
        ThingState.silentSetThingProperty(thing.getReadonlyRefProperty(), referenceable);
        assertThat(thing.getRefProperty().get(), is(10L));

        // Try the Getter, which will call the resolver.
        @SuppressWarnings("unchecked")
        TypedReferenceResolver<TestReferenceable> testResolver = mock(TypedReferenceResolver.class);
        when(testResolver.resolve(10L)).thenReturn(referenceable);
        behavior.getDelegatingResolver().addResolver(TestReferenceable.class, testResolver);
        assertThat(thing.getRefProperty().getTarget(), sameInstance(referenceable));
        assertThat(thing.getReadonlyRefProperty().getTarget(), sameInstance(referenceable));
        verify(testResolver, times(2)).resolve(10L);

        // Fire an action with a referenced value inside.
        TestRefActionInstance actionInstance = new TestRefActionInstance(thing.getRefAction(), referenceable, null);
        thing.getRefAction().fire(actionInstance);
        verify(behavior, times(1)).userFiredAction(actionInstance);
        assertThat(actionInstance.getParameter().getId(), is(10L));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailForUnpersistedReferences() {
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        new TestRefActionInstance(thing.getRefAction(), new TestReferenceable(null), null);
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
        state.toString(); // Should not fail.
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

    @Test
    public void testThingDescription() throws IOException {

        // Create a TestThing that we will describe.
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);

        // Check the main Thing description.
        ThingDescription description = ThingDescription.create(thing);
        description.toString(); // Should not fail.
        assertThat(description.getType(), isClass(TestThing.class));

        // Ensure that the description is serializable by Jackson without exceptions.
        ObjectMapper mapper = new ObjectMapper();
        description = mapper.readValue(mapper.writeValueAsString(description), ThingDescription.class);

        // Check the events.
        assertThat(description.getEvents(), hasSize(4));
        assertThat(description.getEventByName("simpleEvent").getInstanceDescription().getInstanceType() == EventInstance.class, is(true));
        assertThat(description.getEventByName("simpleEvent").getInstanceDescription().getParameters().isEmpty(), is(true));
        assertThat(description.getEventByName("parameterizedEvent").getInstanceDescription().getInstanceType(), isClass(TestEventInstance.class));
        assertThat(description.getEventByName(""), is(nullValue()));
        assertThat(description.getEventByName("doesNotExist"), is(nullValue()));

        // Check the actions.
        assertThat(description.getActions(), hasSize(3));
        assertThat(description.getActionByName("simpleAction").getInstanceDescription().getInstanceType() == ActionInstance.class, is(true));
        assertThat(description.getActionByName("simpleAction").getInstanceDescription().getParameters().isEmpty(), is(true));
        assertThat(description.getActionByName("parameterizedAction").getInstanceDescription().getInstanceType(), isClass(TestActionInstance.class));
        assertThat(description.getActionByName("refAction").getInstanceDescription().getInstanceType(), isClass(TestRefActionInstance.class));
        assertThat(description.getActionByName(""), is(nullValue()));
        assertThat(description.getActionByName("doesNotExist"), is(nullValue()));

        // Check the properties.
        assertThat(description.getPropertyByName("int").getValueType(), isClass(Integer.class));
        assertThat(description.getPropertyByName("long").getValueType(), isClass(Long.class));
        assertThat(description.getPropertyByName("percent").getValueType(), isClass(Double.class));
        assertThat(description.getPropertyByName("readonlyString").getValueType(), isClass(String.class));
        assertThat(description.getPropertyByName("readonlyString").isReference(), is(false));
        assertThat(description.getPropertyByName("ref").isReference(), is(true));
        assertThat(description.getPropertyByName("ref").getValueType(), isClass(TestReferenceable.class));
        assertThat(description.getPropertyByName(""), is(nullValue()));
        assertThat(description.getPropertyByName("doesNotExist"), is(nullValue()));

        // Check the UI hints
        UIHint.IntegralSlider intPropertyHint = (UIHint.IntegralSlider) description.getPropertyByName("int").getUiHint();
        assertThat(intPropertyHint.min, is(0L));
        assertThat(intPropertyHint.max, is(10000L));

        assertThat(description.getPropertyByName("long").getUiHint(), instanceOf(UIHint.EditNumber.class));
        assertThat(description.getPropertyByName("percent").getUiHint(), instanceOf(UIHint.PercentageSlider.class));

        UIHint.ReferenceDropDown refHint = (UIHint.ReferenceDropDown) description.getPropertyByName("ref").getUiHint();
        assertThat(refHint.targetType, isClass(TestReferenceable.class));

        ArrayList<Property<?>> sortedProperties = new ArrayList<Property<?>>(thing.getProperties());
        Collections.sort(sortedProperties, Property.groupComp());
        assertThat(sortedProperties.indexOf(thing.getReadonlyStringProperty()), is(lessThan(sortedProperties.indexOf(thing.getIntProperty()))));
        assertThat(Math.abs(sortedProperties.indexOf(thing.getIntProperty()) - sortedProperties.indexOf(thing.getLongProperty())), is(1));

        try {
            ThingDescription.create(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testBaseInstanceDescriptions() {
        try {
            BaseInstanceDescriptions.get(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Check the returned descriptions.
        BaseInstanceDescription parEventInstance = BaseInstanceDescriptions.get(TestEventInstance.class);
        assertThat(parEventInstance.getInstanceType(), isClass(TestEventInstance.class));
        assertThat(parEventInstance.getParameters().size(), is(1));
        assertThat(parEventInstance.getParameters().get(0).getName(), is("parameter"));
        assertThat(parEventInstance.getParameters().get(0).getValueType(), isClass(Integer.class));

        BaseInstanceDescription parActionInstance = BaseInstanceDescriptions.get(TestActionInstance.class);
        assertThat(parActionInstance.getInstanceType(), isClass(TestActionInstance.class));
        assertThat(parActionInstance.getParameters().size(), is(1));
        ParameterDescription actionParam = parActionInstance.getParameters().get(0);
        assertThat(actionParam.getName(), is("parameter"));
        assertThat(actionParam.getValueType(), isClass(Integer.class));
        assertThat(actionParam.isReference(), is(false));

        BaseInstanceDescription refActionInstance = BaseInstanceDescriptions.get(TestRefActionInstance.class);
        assertThat(refActionInstance.getInstanceType(), isClass(TestRefActionInstance.class));
        assertThat(refActionInstance.getParameters().size(), is(2));
        ParameterDescription refActionParam = refActionInstance.getParameters().get(0);
        assertThat(refActionParam.getName(), is("parameter"));
        assertThat(refActionParam.getValueType(), isClass(TestReferenceable.class));
        assertThat(refActionParam.isReference(), is(true));
        ParameterDescription thingParam = refActionInstance.getParameters().get(1);
        assertThat(thingParam.getName(), is("thingParameter"));
        assertThat(thingParam.getValueType(), isClass(TestThing.class));
        assertThat(thingParam.isReference(), is(true));

        // Check UI hints.
        assertThat(parEventInstance.getParameters().get(0).getUiHint(), instanceOf(UIHint.EditNumber.class));
        UIHint.IntegralSlider actionParamHint = (UIHint.IntegralSlider) parActionInstance.getParameters().get(0).getUiHint();
        assertThat(actionParamHint.min, is(0L));
        assertThat(actionParamHint.max, is(10000L));

        UIHint.ReferenceDropDown refActionHint = (UIHint.ReferenceDropDown) refActionInstance.getParameters().get(0).getUiHint();
        assertThat(refActionHint.targetType, isClass(TestReferenceable.class));

        assertThat(thingParam.getUiHint(), instanceOf(UIHint.ThingDropDown.class));
        UIHint.ThingDropDown thingHint = (UIHint.ThingDropDown) thingParam.getUiHint();
        assertThat(thingHint.targetType, isClass(TestThing.class));
        assertThat(thingHint.requiredPermissions, arrayContaining(ThingPermission.SHARE, ThingPermission.READ));
    }

    @Test
    public void testBaseInstanceParameters() {
        try {
            BaseInstanceDescriptions.getParameterValues(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            BaseInstanceDescriptions.getParameterValue(null, "someParameter", Integer.class);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 42, behavior);
        TestActionInstance instance = new TestActionInstance(thing.getParameterizedAction(), 55);
        assertThat(BaseInstanceDescriptions.getParameterValue(instance, "parameter", Integer.class), is(55));

        ParameterDescription parameterDescription = BaseInstanceDescriptions.get(TestActionInstance.class).getParameters().get(0);
        assertThat(BaseInstanceDescriptions.getParameterValue(instance, parameterDescription), is((Object) 55));

        try {
            BaseInstanceDescriptions.getParameterValue(instance, "doesNotExist", Integer.class);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            // The wrong type.
            BaseInstanceDescriptions.getParameterValue(instance, "parameter", Double.class);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected
        }
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
        assertThat(son.getParentReference().getId(), is(53L));
        assertThat(dad.getParentReference().getId(), is(52L));
        assertThat(grandpa.getParentReference().getId(), is(nullValue()));

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
