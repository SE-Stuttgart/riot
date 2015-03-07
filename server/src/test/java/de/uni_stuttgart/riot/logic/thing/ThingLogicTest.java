package de.uni_stuttgart.riot.logic.thing;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.commons.ThingPermission;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;
import de.uni_stuttgart.riot.thing.test.TestEventInstance;
import de.uni_stuttgart.riot.thing.test.TestThing;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql" })
public class ThingLogicTest extends BaseDatabaseTest {

    ThingLogic logic;

    @Before
    public void initializeThingLogic() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        // We don't use the static instance because we need a fresh one every time.
        Constructor<ThingLogic> constructor = ThingLogic.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        logic = constructor.newInstance();
    }

    @Test
    public void shouldLoadExistingThingFromDatabase() {
        assertThat(logic.getAllThings(null, null), hasSize(2));
        TestThing thing = (TestThing) logic.getThing(1);
        assertThat(thing.getInt(), is(42));
        assertThat(thing.getReadonlyString(), is("String from Database"));
        assertThat(thing.getAction("simpleAction"), notNullValue());
        assertThat(thing.getEvent("simpleEvent"), notNullValue());
    }

    @Test
    public void shouldRegisterAndUnregister() throws DatasourceInsertException, DatasourceDeleteException {
        TestThing thing1 = (TestThing) logic.getThing(1);
        TestThing thing2 = (TestThing) logic.getThing(2);

        TestThing thing3 = (TestThing) logic.registerThing(TestThing.class.getName(), "Second Test Thing", 0);
        assertThat(thing3.getId(), notNullValue());
        assertThat(thing3.getId(), not(0));

        Collection<Thing> things = logic.getAllThings(null, null);
        assertThat(things, containsInAnyOrder(thing1, thing2, thing3));

        logic.unregisterThing(thing3.getId());
        things = logic.getAllThings(null, null);
        assertThat(things, containsInAnyOrder(thing1, thing2));
    }

    @Test
    public void shouldFilterCorrectly() throws DatasourceInsertException {
        TestThing thing1 = (TestThing) logic.getThing(1);
        TestThing thing2 = (TestThing) logic.getThing(2);
        TestThing thing3 = (TestThing) logic.registerThing(TestThing.class.getName(), "Second Test Thing", 0);
        TestThing thing4 = (TestThing) logic.registerThing(TestThing.class.getName(), "Third Test Thing", 0);
        TestThing thing5 = (TestThing) logic.registerThing(TestThing.class.getName(), "Fourth Test Thing", 0);

        // Note: ThingLogic internally sorts by ID.
        assertThat(logic.getAllThings(null, null), contains(thing1, thing2, thing3, thing4, thing5));
        assertThat(logic.findThings(0, 10, null, null), contains(thing1, thing2, thing3, thing4, thing5));
        assertThat(logic.findThings(0, 2, null, null), contains(thing1, thing2));
        assertThat(logic.findThings(2, 2, null, null), contains(thing3, thing4));

        // id > thing1 (OR id == thing3)
        FilteredRequest request = new FilteredRequest();
        request.setOrMode(true);
        List<FilterAttribute> filterAttributes = new ArrayList<>();
        filterAttributes.add(new FilterAttribute("id", FilterOperator.GT, thing1.getId()));
        filterAttributes.add(new FilterAttribute("thingID", FilterOperator.EQ, thing4.getId()));
        request.setFilterAttributes(filterAttributes);
        assertThat(logic.findThings(request, null, null), contains(thing2, thing3, thing4, thing5));

        // id == thing3 (AND id > thing1)
        request.setOrMode(false);
        assertThat(logic.findThings(request, null, null), contains(thing4));

        // id == thing3 (AND id > thing1 AND type == TestThing)
        filterAttributes.add(new FilterAttribute("type", FilterOperator.EQ, TestThing.class.getName()));
        assertThat(logic.findThings(request, null, null), contains(thing4));

        // type = TestThing (OR id == thing3 OR id > thing1)
        request.setOrMode(true);
        assertThat(logic.findThings(request, null, null), contains(thing1, thing2, thing3, thing4, thing5));

        // Select all, but paginated
        request.setLimit(2);
        assertThat(logic.findThings(request, null, null), contains(thing1, thing2));
        request.setOffset(2);
        assertThat(logic.findThings(request, null, null), contains(thing3, thing4));
    }

    @Test
    public void shouldPropagateEvents() throws DatasourceInsertException, DatasourceFindException {

        // thing1 registers to the parameterizedEvent of thing2
        TestThing thing1 = (TestThing) logic.getThing(1);
        TestThing thing2 = (TestThing) logic.registerThing(TestThing.class.getName(), "Second Test Thing", 0);
        logic.registerToEvent(thing1.getId(), thing2.getId(), "parameterizedEvent");

        // So far, there should be no instances
        assertThat(logic.getThingUpdates(thing1.getId()).getOccuredEvents(), is(empty()));

        // Now thing2 fires the event twice (with different parameters)
        Event<?> event = thing2.getEvent("parameterizedEvent");
        TestEventInstance instance1 = new TestEventInstance(event, 23);
        TestEventInstance instance2 = new TestEventInstance(event, 24);
        logic.fireEvent(instance1);
        logic.fireEvent(instance2);

        // We should receive both of these.
        assertThat(logic.getThingUpdates(thing1.getId()).getOccuredEvents(), contains(instance1, instance2));

        // Calling again, they should be gone.
        assertThat(logic.getThingUpdates(thing1.getId()).getOccuredEvents(), is(empty()));

        // Unregister and try again - nothing should happen.
        logic.unregisterFromEvent(thing1.getId(), thing2.getId(), "parameterizedEvent");
        logic.fireEvent(new TestEventInstance(event, 25));
        assertThat(logic.getThingUpdates(thing1.getId()).getOccuredEvents(), is(empty()));
    }

    @Test
    public void shouldPropagateActions() throws DatasourceFindException {

        // The thing is freshly initialized, there should be nothing to get for it.
        TestThing thing1 = (TestThing) logic.getThing(1);
        assertThat(logic.getThingUpdates(thing1.getId()).getOutstandingActions(), is(empty()));

        // Now submit the action.
        ActionInstance instance = new ActionInstance(thing1.getSimpleAction());
        logic.submitAction(instance);

        // We should receive the event.
        assertThat(logic.getThingUpdates(thing1.getId()).getOutstandingActions(), contains(instance));

        // Calling again, it should be gone.
        assertThat(logic.getThingUpdates(thing1.getId()).getOutstandingActions(), is(empty()));
    }

    @Test
    public void shouldReportLastConnectionTime() throws DatasourceFindException {
        Date l1 = logic.getLastConnection(1);
        assertThat(l1, is(nullValue()));

        long timeBefore = System.currentTimeMillis();
        logic.getThingUpdates(1);
        Date l2 = logic.getLastConnection(1);
        assertThat(l2, notNullValue());
        assertThat(l2.getTime(), greaterThanOrEqualTo(timeBefore));
    }

    @Test
    public void shouldDoSharing() throws DatasourceFindException, DatasourceInsertException, DatasourceDeleteException {
        // For i={1,2}: User i has full access to thing i by testdata entries.
        assertThat(logic.canAccess(1L, 1L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(1L, 1L, ThingPermission.FULL), is(true));
        assertThat(logic.canAccess(2L, 2L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(2L, 2L, ThingPermission.FULL), is(true));
        assertThat(logic.canAccess(1L, 2L, ThingPermission.READ), is(false));
        assertThat(logic.canAccess(1L, 2L, ThingPermission.FULL), is(false));
        assertThat(logic.canAccess(2L, 1L, ThingPermission.READ), is(false));
        assertThat(logic.canAccess(2L, 1L, ThingPermission.FULL), is(false));

        // Check the permissions map.
        Map<Long, Set<ThingPermission>> permissions = logic.getThingUserPermissions(1);
        assertThat(permissions.get(1L), equalTo(EnumSet.of(ThingPermission.FULL)));
        assertThat(permissions.containsKey(2L), is(false));

        // Share the thing 1 with user 2 additionally
        logic.share(1, 2, ThingPermission.EXECUTE);
        logic.share(1, 2, ThingPermission.READ);
        assertThat(logic.canAccess(1, 2L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(1, 2L, ThingPermission.EXECUTE), is(true));
        assertThat(logic.canAccess(1, 2L, ThingPermission.FULL), is(false));

        // Check the permissions map again.
        permissions = logic.getThingUserPermissions(1);
        assertThat(permissions.get(1L), equalTo(EnumSet.of(ThingPermission.FULL)));
        assertThat(permissions.get(2L), equalTo(EnumSet.of(ThingPermission.READ, ThingPermission.EXECUTE)));

        // Unshare.
        logic.unshare(1, 2L, ThingPermission.EXECUTE);
        assertThat(logic.canAccess(1, 2L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(1, 2L, ThingPermission.EXECUTE), is(false));
        assertThat(logic.canAccess(1, 2L, ThingPermission.FULL), is(false));
        logic.unshare(1, 2L, ThingPermission.READ);
        assertThat(logic.canAccess(1, 2L, ThingPermission.READ), is(false));
        assertThat(logic.canAccess(1, 2L, ThingPermission.EXECUTE), is(false));
        assertThat(logic.canAccess(1, 2L, ThingPermission.FULL), is(false));

        // Check the permissions map again.
        permissions = logic.getThingUserPermissions(1);
        assertThat(permissions.get(1L), equalTo(EnumSet.of(ThingPermission.FULL)));
        assertThat(permissions.containsKey(2L), is(false));

    }

}
