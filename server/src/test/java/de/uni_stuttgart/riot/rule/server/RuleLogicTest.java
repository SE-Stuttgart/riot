package de.uni_stuttgart.riot.rule.server;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.after;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

import org.hamcrest.CustomMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.reference.ServerReferenceResolver;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;
import de.uni_stuttgart.riot.rule.RuleDescriptions;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.rule.test.TestAdditionRule;
import de.uni_stuttgart.riot.rule.test.TestSchedulingRule;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.thing.PropertyListener;
import de.uni_stuttgart.riot.thing.ThingTestUtils;
import de.uni_stuttgart.riot.thing.test.TestThing;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql", "/schema/schema_rules.sql", "/testdata/testdata_rules.sql" })
public class RuleLogicTest extends BaseDatabaseTest {

    RuleLogic logic;

    @Before
    public void initializeRuleLogic() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // We don't use the static instance because we need a fresh one every time.
        // Note that each of these RuleLogics will use the static ThingLogic instance!
        Constructor<RuleLogic> constructor = RuleLogic.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        logic = constructor.newInstance();
    }

    @Test
    public void shouldLoadExistingRulesFromDatabase() {
        assertThat(logic.getAllRules(1L), hasSize(1));
        RuleConfiguration config = logic.getRuleConfiguration(1);
        assertThat(config.getType(), is(TestAdditionRule.class.getName()));
        assertThat(config.getName(), is("My Test Addition Rule"));
        assertThat(config.getStatus(), is(RuleStatus.DEACTIVATED));
        assertThat(config.get("intAdd", Integer.class), is(42));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddAndRemove() throws DatasourceInsertException, DatasourceDeleteException, ResolveReferenceException, InterruptedException {

        // We prepare the thing so that we notice that the rule actually was launched.
        TestThing thing = ServerReferenceResolver.getInstance().resolve(1L, TestThing.class);
        thing.setInt(500);
        thing.setLong(2000);
        thing.setPercent(0.0);
        ThingTestUtils.flushAllActions(thing);

        // Now we add (and thus launch) the rule.
        RuleConfiguration existingConfig = logic.getRuleConfiguration(1);
        RuleConfiguration config = new RuleConfiguration(null, TestSchedulingRule.class.getName(), RuleStatus.ACTIVE, "TestRule", 0L, null);
        config.set("thing", 1L);

        logic.addNewRule(config, 1);
        assertThat(config.getId(), notNullValue());
        assertThat(config.getId(), not(0));
        assertThat(config.getId(), not(1));
        assertThat(config.getOwnerId(), is(1L));
        assertThat(logic.getAllRules(1L), containsInAnyOrder(existingConfig, config));

        // And check that the rule was actually launched.
        ThingTestUtils.flushAllActions(thing);
        assertThat(thing.getPercent(), is(0.25)); // 500 / 2000 = 25%

        // Check that is running now.
        PropertyListener<Double> percentListener = mock(PropertyListener.class);
        thing.getPercentProperty().register(percentListener);
        thing.setInt(200);
        ThingTestUtils.flushAllActions(thing);
        Thread.sleep(50);
        ThingTestUtils.flushAllActions(thing);
        verify(percentListener, timeout(50).times(1)).onFired(Matchers.any(), Matchers.any());
        assertThat(thing.getPercent(), is(0.1)); // 200 / 2000 = 10%

        // Shut it down and verify that it worked.
        reset(percentListener);
        config.setStatus(RuleStatus.DEACTIVATED);
        logic.deleteRule(config.getId());
        assertThat(logic.getAllRules(1L), contains(existingConfig));
        thing.setInt(100);
        ThingTestUtils.flushAllActions(thing);
        verify(percentListener, after(50).never()).onFired(Matchers.any(), Matchers.any());
        thing.getPercentProperty().unregister(percentListener);

    }

    @Test
    public void shouldUpdateCorrectly() throws ResolveReferenceException, DatasourceUpdateException {

        // The test rule in the DB is deactivated. It uses Things 1 and to for: int2 = int1 + 42
        // We prepare the things so that we notice that the rule actually was launched.
        TestThing thing1 = ServerReferenceResolver.getInstance().resolve(1L, TestThing.class);
        TestThing thing2 = ServerReferenceResolver.getInstance().resolve(2L, TestThing.class);
        thing1.setInt(-10);
        thing2.setInt(-20);
        ThingTestUtils.flushAllActions(thing1, thing2);

        // We launch the rule.
        RuleConfiguration config = logic.getRuleConfiguration(1);
        config.set("intAdd", 50);
        config.setStatus(RuleStatus.ACTIVE);
        logic.updateRuleConfiguration(config);
        ThingTestUtils.flushAllActions(thing2);

        // Check that it was actually launched and is working now.
        assertThat(thing2.getInt(), is(40)); // -10 + 50 = 40
        thing1.setInt(11);
        ThingTestUtils.flushAllActions(thing1, thing2);
        assertThat(thing2.getInt(), is(61)); // 11 + 50 = 61

        // Check that relaunch with modified configuration works.
        config.set("inputThing", 2L);
        config.set("outputThing", 1L);
        config.set("intAdd", 24);
        logic.updateRuleConfiguration(config);
        ThingTestUtils.flushAllActions(thing1, thing2);
        assertThat(thing2.getInt(), is(61)); // Unchanged
        assertThat(thing1.getInt(), is(85)); // 61 + 24 = 85
        thing2.setInt(53);
        ThingTestUtils.flushAllActions(thing2, thing1);
        assertThat(thing1.getInt(), is(77)); // 53 + 24 = 77

        // Stop the rule by updating and check that it stopped.
        config.setStatus(RuleStatus.DEACTIVATED);
        logic.updateRuleConfiguration(config);
        thing2.setInt(111);
        ThingTestUtils.flushAllActions(thing2, thing1);
        assertThat(thing1.getInt(), is(77)); // Unchanged

        // Tidy up.
        config.set("inputThing", 1L);
        config.set("outputThing", 2L);
        config.set("intAdd", 42);
        config.setStatus(RuleStatus.DEACTIVATED);
        logic.updateRuleConfiguration(config);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldDiscoverRules() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field descriptionsField = RuleDescriptions.class.getDeclaredField("DESCRIPTIONS");
        descriptionsField.setAccessible(true);
        ((Map<Class<? extends Rule>, RuleDescription>) descriptionsField.get(null)).clear();

        Field scannedField = RuleDescriptions.class.getDeclaredField("discoveredClasspath");
        scannedField.setAccessible(true);
        scannedField.set(null, false);

        Collection<RuleDescription> descriptions = RuleDescriptions.getAll(true);
        assertThat(descriptions, hasItem(new CustomMatcher<RuleDescription>("A TestAdditionRule") {
            public boolean matches(Object item) {
                return item instanceof RuleDescription && ((RuleDescription) item).getType().equals(TestAdditionRule.class.getName());
            }
        }));
        assertThat(descriptions, hasItem(new CustomMatcher<RuleDescription>("A TestSchedulingRule") {
            public boolean matches(Object item) {
                return item instanceof RuleDescription && ((RuleDescription) item).getType().equals(TestSchedulingRule.class.getName());
            }
        }));
    }
}
