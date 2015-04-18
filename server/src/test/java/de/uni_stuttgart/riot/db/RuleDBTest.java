package de.uni_stuttgart.riot.db;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql", "/schema/schema_rules.sql", "/testdata/testdata_rules.sql" })
public class RuleDBTest extends BaseDatabaseTest {

    /**
     * The SUT is the DAO.
     */
    RuleConfigurationDAO dao = new RuleConfigurationDAO();

    @Test
    public void shouldInsertRule() throws DatasourceInsertException, DatasourceFindException {

        // Insert a new rule.
        RuleConfiguration config = new RuleConfiguration(null, "de.uni_stuttgart.riot.rule.test.TestAdditionRule", RuleStatus.FAILED, "UnitTestRule", 11L, null);
        config.set("intAdd", 55);
        config.set("inputThing", 100L);
        config.set("outputThing", 101L);
        config.setId(11L);
        dao.insert(config);

        // When there was an ID already, it must be reused.
        assertThat(config.getId(), is(11L));

        // Check if it was stored correctly by reloading it.
        RuleConfiguration restoredConfig = dao.findBy(config.getId());
        assertThat(restoredConfig, equalTo(config));

        // Insert another rule without ID. The ID must be created.
        RuleConfiguration config2 = new RuleConfiguration(null, "de.uni_stuttgart.riot.rule.test.TestAdditionRule", RuleStatus.FAILED, "UnitTestRule", 11L, null);
        dao.insert(config2);
        assertThat(config2.getId(), notNullValue());
        assertThat(config2.getId(), not(0));
    }

    @Test
    public void shouldLoadExistingRule() throws DatasourceFindException {
        RuleConfiguration config = dao.findBy(1);
        assertThat(config.get("inputThing", Long.class), is(1L));
        assertThat(config.get("outputThing", Long.class), is(2L));
        assertThat(config.get("intAdd", Integer.class), is(42));
    }

    @Test
    public void shouldDeleteRule() throws DatasourceFindException, DatasourceDeleteException {
        dao.delete(dao.findBy(1));
        try {
            dao.findBy(1); // Should fail
            fail();
        } catch (DatasourceFindException e) {
            // Expected
        }
    }

    @Test
    public void shouldDeleteRuleById() throws DatasourceFindException, DatasourceDeleteException {
        dao.findBy(1); // Should work
        dao.delete(1);
        try {
            dao.findBy(1); // Should fail
            fail();
        } catch (DatasourceFindException e) {
            // Expected
        }
    }

    @Test
    public void shouldUpdateRuleParameters() throws DatasourceFindException, DatasourceUpdateException {
        RuleConfiguration config = dao.findBy(1);
        assertThat(config.getName(), is("My Test Addition Rule"));
        assertThat(config.get("intAdd", Integer.class), is(42));

        config.setName("Another name");
        config.set("intAdd", 43);
        dao.update(config);

        RuleConfiguration restoredConfig = dao.findBy(1);
        assertThat(restoredConfig.getName(), is("Another name"));
        assertThat(config.get("intAdd", Integer.class), is(43));
    }

    @Test
    public void shouldFind() throws DatasourceInsertException, DatasourceFindException, DatasourceDeleteException {

        RuleConfiguration config1 = new RuleConfiguration(null, "de.uni_stuttgart.riot.rule.test.TestAdditionRule", RuleStatus.FAILED, "UnitTestRule1", 11L, null);
        RuleConfiguration config2 = new RuleConfiguration(null, "de.uni_stuttgart.riot.rule.test.TestAdditionRule", RuleStatus.FAILED, "UnitTestRule2", 11L, null);
        RuleConfiguration config3 = new RuleConfiguration(null, "de.uni_stuttgart.riot.rule.test.TestAdditionRule", RuleStatus.DEACTIVATED, "UnitTestRule3", 11L, null);
        dao.insert(config1);
        dao.insert(config2);
        dao.insert(config3);

        // Select the second two.
        Collection<SearchParameter> searchParams = new ArrayList<>();
        searchParams.add(new SearchParameter(SearchFields.RULESTATUS, RuleStatus.FAILED));
        assertThat(dao.findBy(searchParams, false), containsInAnyOrder(config1, config2));

        // Select all three with a FilteredRequest.
        FilteredRequest request = new FilteredRequest();
        request.setOffset(0);
        request.setLimit(10);
        request.setOrMode(false);
        request.getFilterAttributes().add(new FilterAttribute("type", FilterOperator.EQ, "de.uni_stuttgart.riot.rule.test.TestAdditionRule"));
        assertThat(dao.findAll(request), hasItems(config1, config2, config3));

        // Select the first one plus the one that pre-existed in the test data.
        RuleConfiguration config = dao.findBy(1);
        searchParams.clear();
        searchParams.add(new SearchParameter(SearchFields.RULESTATUS, RuleStatus.DEACTIVATED));
        assertThat(dao.findBy(searchParams, false), containsInAnyOrder(config, config3));

        // Delete the initial one, so that only our three remain.
        dao.delete(1);
        assertThat(dao.findAll(), containsInAnyOrder(config1, config2, config3));
        assertThat(dao.findAll(0, 10), containsInAnyOrder(config1, config2, config3));

        // Find the third rule by its name.
        SearchParameter searchParam = new SearchParameter(SearchFields.NAME, "UnitTestRule3");
        assertThat(dao.findByUniqueField(searchParam), is(config3));
    }

}
