package de.uni_stuttgart.riot.clientlibrary.rule.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.hamcrest.CustomMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.clientlibrary.BaseClientTest;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleDescription;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.rule.client.RuleClient;
import de.uni_stuttgart.riot.rule.house.AutoRefillRule;
import de.uni_stuttgart.riot.rule.test.TestAdditionRule;
import de.uni_stuttgart.riot.rule.test.TestSchedulingRule;
import de.uni_stuttgart.riot.server.test.ResetHelper;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", //
        "/schema/schema_things.sql", "/testdata/testdata_things.sql", //
        "/schema/schema_configuration.sql", "/data/testdata_configuration.sql", //
        "/schema/schema_rules.sql", "/testdata/testdata_rules.sql" //
})
public class RuleClientTest extends BaseClientTest {

    @Before
    public void clearRuleLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ResetHelper.resetThingLogic();
        ResetHelper.resetRuleLogic();
        ResetHelper.resetServerReferenceResolver();
    }

    public RuleClient getLoggedInRuleClient() {
        return new RuleClient(getLoggedInConnector());
    }

    @Test
    public void shouldGetDescriptions() throws IOException, RequestException, NotFoundException {
        RuleClient client = getLoggedInRuleClient();
        RuleDescription description = client.getRuleDescription(TestAdditionRule.class.getName());
        assertThat(description.getType(), is(TestAdditionRule.class.getName()));
        assertThat(description.getParameters(), hasSize(3));
        assertThat(description.getParameterByName("intAdd").getValueType(), isClass(Integer.class));

        Collection<RuleDescription> descriptions = client.getRuleDescriptions();
        assertThat(descriptions, not(empty()));
        // Note that the test rules are not here on purpose.
        assertThat(descriptions, hasItem(new CustomMatcher<RuleDescription>("The AutoRefillRule Description") {
            public boolean matches(Object item) {
                return item instanceof RuleDescription && ((RuleDescription) item).getType().equals(AutoRefillRule.class.getName());
            }
        }));
    }

    @Test
    public void shouldGetExistingRules() throws IOException, RequestException, NotFoundException {
        RuleClient client = getLoggedInRuleClient();
        RuleConfiguration config = client.getRule(1);
        assertThat(config.getName(), is("My Test Addition Rule"));
        assertThat(config.get("intAdd", Integer.class), is(42));
        assertThat(client.getRules(), hasItem(equalTo(config)));
    }

    @Test
    public void shouldAddUpdateDeleteRule() throws IOException, RequestException, NotFoundException {

        RuleClient client = getLoggedInRuleClient();

        int initialRuleCount = client.getRules().size();

        // Create a new rule.
        RuleConfiguration config = new RuleConfiguration(null, TestSchedulingRule.class.getName(), RuleStatus.DEACTIVATED, "SomeTestRuleInactive", null, null);
        config.set("thing", 3L);
        RuleConfiguration addedConfig = client.addNewRule(config);
        assertThat(addedConfig.getType(), is(config.getType()));
        assertThat(addedConfig.getName(), is(config.getName()));
        assertThat(addedConfig.getStatus(), is(config.getStatus()));
        assertThat(addedConfig.getOwnerId(), is(1L)); // Logged in as Yoda.
        assertThat(addedConfig.getId(), not(nullValue()));
        assertThat(addedConfig.getId(), not(0L));
        assertThat(client.getRules(), hasSize(initialRuleCount + 1));

        // Update the rule.
        addedConfig.setName("Another name");
        client.updateRuleConfiguration(addedConfig);
        RuleConfiguration updatedConfig = client.getRule(addedConfig.getId());
        assertThat(updatedConfig.getName(), is(addedConfig.getName()));

        // Delete the rule.
        client.deleteRule(addedConfig.getId());
        assertThat(client.getRules(), hasSize(initialRuleCount));
        try {
            client.getRule(addedConfig.getId());
            fail();
        } catch (NotFoundException e) {
            // Expected.
        }

    }

    @Test(expected = RequestException.class)
    public void shouldFailOnOwnerChange() throws IOException, RequestException, NotFoundException {
        RuleClient client = getLoggedInRuleClient();
        RuleConfiguration config = client.getRule(1);
        config.setOwnerId(config.getOwnerId() + 1);
        client.updateRuleConfiguration(config);
    }

    private static Matcher<Class<?>> isClass(Class<?> clazz) {
        return CoreMatchers.<Class<?>> sameInstance(clazz);
    }

}
