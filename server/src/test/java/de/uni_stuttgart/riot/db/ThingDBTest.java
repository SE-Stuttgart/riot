package de.uni_stuttgart.riot.db;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.db.thing.ThingDAO;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.ThingFactory;
import de.uni_stuttgart.riot.thing.ThingState;
import de.uni_stuttgart.riot.thing.remote.ServerThingBehavior;
import de.uni_stuttgart.riot.thing.test.TestThing;
import de.uni_stuttgart.riot.thing.test.TestThingBehavior;

@TestData({ "/schema/schema_things.sql", "/testdata/testdata_things.sql" })
public class ThingDBTest extends BaseDatabaseTest {

    /**
     * We need a stub for the behavior factory.
     */
    ThingBehaviorFactory<ServerThingBehavior> behaviorFactory = new ThingBehaviorFactory<ServerThingBehavior>() {
        @Override
        public void onThingCreated(Thing thing, ServerThingBehavior behavior) {
        }

        @Override
        public ServerThingBehavior newBehavior(long thingID, String thingName, Class<? extends Thing> thingType) {
            return new ServerThingBehavior(); // Could also use a Mock here.
        }
    };

    /**
     * The SUT is the DAO.
     */
    ThingDAO dao = new ThingDAO(behaviorFactory);

    @Test
    public void shouldInsertThing() throws DatasourceInsertException, DatasourceFindException {

        // Insert a new thing.
        TestThing thing = ThingFactory.create(TestThing.class, 11, "Inserted Thing", new ServerThingBehavior());
        dao.insert(thing);

        // When there was an ID already, it must be reused.
        assertThat(thing.getId(), is(11L));

        // Check if it was stored correctly by reloading it.
        TestThing restoredThing = (TestThing) dao.findBy(thing.getId());
        assertThat(restoredThing, equalTo(thing));

        // Insert another thing without ID. The ID must be created.
        TestThing thing2 = ThingFactory.create(TestThing.class, 0, "Inserted Thing 2", new ServerThingBehavior());
        dao.insert(thing2);
        assertThat(thing2.getId(), notNullValue());
        assertThat(thing2.getId(), not(0));
    }

    @Test
    public void shouldLoadExistingThing() throws DatasourceFindException {
        TestThing thing = (TestThing) dao.findBy(1);
        assertThat(thing.getInt(), is(42));
        assertThat(thing.getReadonlyString(), is("String from Database"));
    }

    @Test
    public void shouldDeleteThing() throws DatasourceFindException, DatasourceDeleteException {
        dao.delete(dao.findBy(1));
        try {
            dao.findBy(1); // Should fail
            fail();
        } catch (DatasourceFindException e) {
            // Expected
        }
    }

    @Test
    public void shouldDeleteThingById() throws DatasourceFindException, DatasourceDeleteException {
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
    public void shouldUpdateThingProperties() throws DatasourceFindException, DatasourceUpdateException {
        TestThing thing = (TestThing) dao.findBy(1);
        assertThat(thing.getName(), is("My Test Thing"));
        assertThat(thing.getInt(), is(42));

        thing.setName("Another name");
        ThingState.silentSetThingProperty(thing.getIntProperty(), 43);
        dao.update(thing);

        TestThing restoredThing = (TestThing) dao.findBy(1);
        assertThat(restoredThing.getName(), is("Another name"));
        assertThat(restoredThing.getInt(), is(43));
    }

    @Test
    public void shouldFindByName() throws DatasourceInsertException, DatasourceFindException, DatasourceDeleteException {

        TestThing thing1 = ThingFactory.create(TestThing.class, 11, "TestThing1", new TestThingBehavior());
        TestThing thing2 = ThingFactory.create(TestThing.class, 12, "TestThing2", new TestThingBehavior());
        TestThing thing3 = ThingFactory.create(TestThing.class, 13, "TestThing3", new TestThingBehavior());
        dao.insert(thing1);
        dao.insert(thing2);
        dao.insert(thing3);

        assertThat(dao.findByUniqueField(new SearchParameter(SearchFields.NAME, "TestThing3")), equalTo(thing3));

        Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
        searchParams.add(new SearchParameter(SearchFields.NAME, "TestThing1"));
        searchParams.add(new SearchParameter(SearchFields.TABLEPK, 13));
        Collection<Thing> restoredThings = dao.findBy(searchParams, true);
        assertThat(restoredThings, containsInAnyOrder(thing1, thing3));

        // Delete the initial one, so that only our three remain
        dao.delete(1);
        dao.delete(2);
        restoredThings = dao.findAll();
        assertThat(restoredThings, containsInAnyOrder(thing1, thing2, thing3));
    }

}
