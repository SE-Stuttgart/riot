package de.uni_stuttgart.riot.rule.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;

import de.uni_stuttgart.riot.reference.ServerReferenceResolver;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.TargetNotFoundException;
import de.uni_stuttgart.riot.references.TypedReferenceResolver;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;
import de.uni_stuttgart.riot.rule.RuleDescriptions;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.test.commons.DeactivateLoggingRule;
import de.uni_stuttgart.riot.thing.PropertyListener;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.test.TestThing;
import de.uni_stuttgart.riot.thing.test.TestThingBehavior;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Tests the Java-only part of the rule framework.
 * 
 * @author Philipp Keck
 */
public class RuleFrameworkTest {

    @ClassRule
    public static DeactivateLoggingRule deactivateRuleLogging = new DeactivateLoggingRule(Rule.class);

    @Test
    public void testAdditionRule() throws ResolveReferenceException {

        // Setup test things for the rule.
        TestThingBehavior behavior1 = new TestThingBehavior();
        TestThingBehavior behavior2 = new TestThingBehavior();
        TestThing thing1 = ThingFactory.create(TestThing.class, 100, behavior1);
        TestThing thing2 = ThingFactory.create(TestThing.class, 100, behavior2);
        behavior1.executePropertyChangesDirectly = true;
        behavior2.executePropertyChangesDirectly = true;
        thing1.setInt(100);
        thing2.setInt(100);

        // Register them in the resolver. Since we don't use a DB for this test, this is done by mocking the resolver.
        @SuppressWarnings("unchecked")
        TypedReferenceResolver<Thing> resolver = mock(TypedReferenceResolver.class);
        ServerReferenceResolver.getInstance().addResolver(Thing.class, resolver);
        when(resolver.resolve(1)).thenReturn(thing1);
        when(resolver.resolve(2)).thenReturn(thing2);

        // Create a configuration for the rule.
        RuleConfiguration configuration = new RuleConfiguration(TestAdditionRule.class.getName());
        configuration.setName("Test Rule");
        configuration.setId(100L);
        configuration.setOwnerId(1);
        configuration.setStatus(RuleStatus.ACTIVE);
        configuration.set("intAdd", 42);
        configuration.set("inputThing", 1L);
        configuration.set("outputThing", 2L);

        // Launch the rule.
        TestAdditionRule rule = new TestAdditionRule();
        rule.setConfiguration(configuration);
        rule.startExecution();
        assertThat(thing2.getInt(), is(142)); // 100 + 42

        // Test dynamic changes.
        thing1.setInt(400);
        assertThat(thing2.getInt(), is(442)); // 400 + 42
        thing1.setInt(-100);
        assertThat(thing2.getInt(), is(-58)); // -100 + 42

        // Stop the rule.
        rule.stopExecution();
        thing1.setInt(1000);
        assertThat(thing2.getInt(), is(-58)); // unchanged

        // And start it again.
        rule.startExecution();
        thing1.setInt(300);
        assertThat(thing2.getInt(), is(342)); // 300 + 42

        // Tidy up.
        rule.stopExecution();
    }

    @Test
    public void testSchedulingRule() throws ResolveReferenceException {

        // Setup test thing for the rule.
        TestThingBehavior behavior = new TestThingBehavior();
        TestThing thing = ThingFactory.create(TestThing.class, 100, behavior);
        behavior.executePropertyChangesDirectly = true;
        thing.setInt(22);
        thing.setLong(44);

        // Register it in the resolver. Since we don't use a DB for this test, this is done by mocking the resolver.
        @SuppressWarnings("unchecked")
        TypedReferenceResolver<Thing> resolver = mock(TypedReferenceResolver.class);
        ServerReferenceResolver.getInstance().addResolver(Thing.class, resolver);
        when(resolver.resolve(1)).thenReturn(thing);

        // Create a configuration for the rule.
        RuleConfiguration configuration = new RuleConfiguration(TestSchedulingRule.class.getName());
        configuration.setName("Test Rule 2");
        configuration.setId(101L);
        configuration.setStatus(RuleStatus.ACTIVE);
        configuration.setOwnerId(1);
        configuration.set("thing", 1L);

        // Launch the rule.
        TestSchedulingRule rule = new TestSchedulingRule();
        rule.setConfiguration(configuration);
        rule.startExecution();
        assertThat(thing.getPercent(), is(0.5)); // 22 / 44 = 50%

        // Register a listener to the percent property so that we receive the changes dynamically.
        @SuppressWarnings("unchecked")
        PropertyListener<Double> listener = mock(PropertyListener.class);
        thing.getPercentProperty().register(listener);

        // Test dynamic changes.
        thing.setInt(33);
        assertThat(thing.getPercent(), is(0.5)); // Old value! Because change is delayed by 10ms.
        verify(listener, timeout(50)).onFired(Matchers.any(), Matchers.any());
        assertThat(thing.getPercent(), is(0.75)); // 33 / 44 = 75%

        thing.setInt(-11);
        verify(listener, timeout(50).times(2)).onFired(Matchers.any(), Matchers.any());
        assertThat(thing.getPercent(), is(-0.25)); // -11 / 44 = -25%

        // Stop the rule.
        rule.stopExecution();

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectUnpersistedConfiguration() {
        RuleConfiguration configuration = new RuleConfiguration(TestSchedulingRule.class.getName());
        // Don't set a rule ID.
        TestSchedulingRule rule = new TestSchedulingRule();
        rule.setConfiguration(configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectMismatchingConfiguration() {
        RuleConfiguration configuration = new RuleConfiguration(TestAdditionRule.class.getName());
        configuration.setId(20L);
        TestSchedulingRule rule = new TestSchedulingRule();
        rule.setConfiguration(configuration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDetectMissingOwner() {
        RuleConfiguration configuration = new RuleConfiguration(TestSchedulingRule.class.getName());
        configuration.setId(20L);
        TestSchedulingRule rule = new TestSchedulingRule();
        rule.setConfiguration(configuration);
    }

    @Test
    public void shouldFailSilentlyOnMissingReferences() throws ResolveReferenceException {
        // Use a resolver that does not actually have things, but always throws an exception.
        @SuppressWarnings("unchecked")
        TypedReferenceResolver<Thing> resolver = mock(TypedReferenceResolver.class);
        ServerReferenceResolver.getInstance().addResolver(Thing.class, resolver);
        when(resolver.resolve(15L)).thenThrow(new TargetNotFoundException("TestMessage"));

        // Create a configuration for the rule.
        RuleConfiguration configuration = new RuleConfiguration(TestSchedulingRule.class.getName());
        configuration.setName("Test Rule With Nonexisting Thing Reference");
        configuration.setId(102L);
        configuration.setStatus(RuleStatus.ACTIVE);
        configuration.setOwnerId(1);
        configuration.set("thing", 15L);

        // Launch the rule.
        TestSchedulingRule rule = new TestSchedulingRule();
        rule.setConfiguration(configuration);
        rule.startExecution();
        assertThat(rule.isRunning(), is(false));
        assertThat(rule.getConfiguration().getStatus(), is(RuleStatus.FAILED_REFERENCES));
    }

    @Test
    public void shouldFailSilentlyOnMissingPermissions() throws ResolveReferenceException {

        // Create a test thing that nobody has permissions on.
        TestThingBehavior behavior = spy(new TestThingBehavior());
        TestThing thing = ThingFactory.create(TestThing.class, 100, behavior);
        stub(behavior.canAccess(Matchers.anyLong(), Matchers.any())).toReturn(false);

        // Register it in the resolver. Since we don't use a DB for this test, this is done by mocking the resolver.
        @SuppressWarnings("unchecked")
        TypedReferenceResolver<Thing> resolver = mock(TypedReferenceResolver.class);
        ServerReferenceResolver.getInstance().addResolver(Thing.class, resolver);
        when(resolver.resolve(15L)).thenReturn(thing);

        // Create a configuration for the rule.
        RuleConfiguration configuration = new RuleConfiguration(TestSchedulingRule.class.getName());
        configuration.setName("Test Rule With Nonexisting Thing Reference");
        configuration.setId(102L);
        configuration.setStatus(RuleStatus.ACTIVE);
        configuration.setOwnerId(1);
        configuration.set("thing", 15L);

        // Launch the rule.
        TestSchedulingRule rule = new TestSchedulingRule();
        rule.setConfiguration(configuration);
        rule.startExecution();
        assertThat(rule.isRunning(), is(false));
        assertThat(rule.getConfiguration().getStatus(), is(RuleStatus.FAILED_PERMISSIONS));
    }

    @Test(expected = RuntimeException.class)
    public void shouldFailThroughOnActualException() throws ResolveReferenceException {
        // Use a resolver that does not actually have things, but always throws an exception.
        @SuppressWarnings("unchecked")
        TypedReferenceResolver<Thing> resolver = mock(TypedReferenceResolver.class);
        ServerReferenceResolver.getInstance().addResolver(Thing.class, resolver);
        when(resolver.resolve(16L)).thenThrow(new RuntimeException("This simulates a failure in the rule"));

        // Create a configuration for the rule.
        RuleConfiguration configuration = new RuleConfiguration(TestSchedulingRule.class.getName());
        configuration.setName("Test Rule With Nonexisting Thing Reference");
        configuration.setId(102L);
        configuration.setStatus(RuleStatus.ACTIVE);
        configuration.setOwnerId(1);
        configuration.set("thing", 16L);

        // Launch the rule.
        TestSchedulingRule rule = new TestSchedulingRule();
        rule.setConfiguration(configuration);
        rule.startExecution();
    }

    @Test
    public void testDescription() throws ClassNotFoundException {
        RuleDescription description = RuleDescriptions.get(TestAdditionRule.class.getName());
        assertThat(description.getType(), is(TestAdditionRule.class.getName()));
        assertThat(description.getParameters(), hasSize(3));
        assertThat(description.getParameterByName("intAdd").getName(), is("intAdd"));
        assertThat(description.getParameterByName("intAdd").getValueType(), isClass(Integer.class));
        assertThat(description.getParameterByName("intAdd").getInternalValueType(), isClass(Integer.class));
        assertThat(description.getParameterByName("intAdd").getUiHint(), instanceOf(UIHint.EditNumber.class));
        assertThat(description.getParameterByName("inputThing").getValueType(), isClass(TestThing.class));
        assertThat(description.getParameterByName("inputThing").getInternalValueType(), isClass(Long.class));
    }

    private static Matcher<Class<?>> isClass(Class<?> clazz) {
        return CoreMatchers.<Class<?>> sameInstance(clazz);
    }

}
