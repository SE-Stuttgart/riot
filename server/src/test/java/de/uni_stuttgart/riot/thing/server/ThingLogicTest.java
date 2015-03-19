package de.uni_stuttgart.riot.thing.server;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hamcrest.CustomMatcher;
import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute;
import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.FilterAttribute.FilterOperator;
import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.reference.ServerReferenceResolver;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.references.SimpleResolver;
import de.uni_stuttgart.riot.references.TypedReferenceResolver;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.rest.ThingInformation;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.test.TestEventInstance;
import de.uni_stuttgart.riot.thing.test.TestThing;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql" })
public class ThingLogicTest extends BaseDatabaseTest {

    ThingLogic logic;

    @Before
    public void resetThingLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field instanceField = ThingLogic.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
        logic = ThingLogic.getThingLogic();
    }

    @Test
    public void shouldLoadExistingThingFromDatabase() {
        assertThat(logic.getAllThings(null, null).collect(Collectors.toList()), hasSize(2));
        TestThing thing = (TestThing) logic.getThing(1);
        assertThat(thing.getInt(), is(42));
        assertThat(thing.getReadonlyString(), is("String from Database"));
        assertThat(thing.getAction("simpleAction"), notNullValue());
        assertThat(thing.getEvent("simpleEvent"), notNullValue());
    }

    @Test
    public void shouldRegisterAndUnregister() throws DatasourceInsertException, DatasourceDeleteException {
        Thing thing1 = logic.getThing(1);
        Thing thing2 = logic.getThing(2);

        TestThing thing3 = (TestThing) logic.registerThing(TestThing.class.getName(), null);
        assertThat(thing3.getId(), notNullValue());
        assertThat(thing3.getId(), not(0));

        Collection<Thing> things = logic.getAllThings(null, null).collect(Collectors.toList());
        assertThat(things, containsInAnyOrder(thing1, thing2, thing3));

        logic.unregisterThing(thing3.getId());
        things = logic.getAllThings(null, null).collect(Collectors.toList());
        assertThat(things, containsInAnyOrder(thing1, thing2));
    }

    @Test
    public void shouldFilterCorrectly() throws DatasourceInsertException {
        Thing thing1 = logic.getThing(1);
        Thing thing2 = logic.getThing(2);
        Thing thing3 = logic.registerThing(TestThing.class.getName(), null);
        Thing thing4 = logic.registerThing(TestThing.class.getName(), null);
        Thing thing5 = logic.registerThing(TestThing.class.getName(), null);

        // Note: ThingLogic internally sorts by ID.
        assertThat(logic.getAllThings(null, null).collect(Collectors.toList()), contains(thing1, thing2, thing3, thing4, thing5));
        assertThat(logic.findThings(0, 10, null, null).collect(Collectors.toList()), contains(thing1, thing2, thing3, thing4, thing5));
        assertThat(logic.findThings(0, 2, null, null).collect(Collectors.toList()), contains(thing1, thing2));
        assertThat(logic.findThings(2, 2, null, null).collect(Collectors.toList()), contains(thing3, thing4));

        // id > thing1 (OR id == thing3)
        FilteredRequest request = new FilteredRequest();
        request.setOrMode(true);
        List<FilterAttribute> filterAttributes = new ArrayList<>();
        filterAttributes.add(new FilterAttribute("id", FilterOperator.GT, thing1.getId()));
        filterAttributes.add(new FilterAttribute("thingID", FilterOperator.EQ, thing4.getId()));
        request.setFilterAttributes(filterAttributes);
        assertThat(logic.findThings(request, null, null).collect(Collectors.toList()), contains(thing2, thing3, thing4, thing5));

        // id == thing3 (AND id > thing1)
        request.setOrMode(false);
        assertThat(logic.findThings(request, null, null).collect(Collectors.toList()), contains(thing4));

        // id == thing3 (AND id > thing1 AND type == TestThing)
        filterAttributes.add(new FilterAttribute("type", FilterOperator.EQ, TestThing.class.getName()));
        assertThat(logic.findThings(request, null, null).collect(Collectors.toList()), contains(thing4));

        // type = TestThing (OR id == thing3 OR id > thing1)
        request.setOrMode(true);
        assertThat(logic.findThings(request, null, null).collect(Collectors.toList()), contains(thing1, thing2, thing3, thing4, thing5));

        // Select all, but paginated
        request.setLimit(2);
        assertThat(logic.findThings(request, null, null).collect(Collectors.toList()), contains(thing1, thing2));
        request.setOffset(2);
        assertThat(logic.findThings(request, null, null).collect(Collectors.toList()), contains(thing3, thing4));
    }

    @Test
    public void shouldPropagateEvents() throws DatasourceInsertException, DatasourceFindException {
        // thing1 registers to the parameterizedEvent of thing2
        TestThing thing1 = (TestThing) logic.getThing(1);
        TestThing thing2 = (TestThing) logic.registerThing(TestThing.class.getName(), null);
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
        assertThat(logic.canAccess(1L, 1L, ThingPermission.EXECUTE), is(true));
        assertThat(logic.canAccess(2L, 2L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(2L, 2L, ThingPermission.EXECUTE), is(true));
        assertThat(logic.canAccess(1L, 2L, ThingPermission.READ), is(false));
        assertThat(logic.canAccess(1L, 2L, ThingPermission.EXECUTE), is(false));
        assertThat(logic.canAccess(2L, 1L, ThingPermission.READ), is(false));
        assertThat(logic.canAccess(2L, 1L, ThingPermission.EXECUTE), is(false));

        // Check the shares.
        Collection<ThingShare> shares = logic.getThingShares(1);
        assertThat(shares, hasSize(1));
        ThingShare share = shares.iterator().next();
        assertThat(share.getUserId(), is(1L));
        assertThat(share.getPermissions(), equalTo(EnumSet.allOf(ThingPermission.class)));

        // Share the thing 1 with user 2 additionally
        logic.addOrUpdateShare(1, new ThingShare(2, EnumSet.of(ThingPermission.READ, ThingPermission.EXECUTE)));
        assertThat(logic.canAccess(1, 2L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(1, 2L, ThingPermission.EXECUTE), is(true));
        assertThat(logic.canAccess(1, 2L, ThingPermission.CONTROL), is(false));

        // Check the shares again.
        Map<Long, ThingShare> sharesMap = logic.getThingShares(1).stream().collect(Collectors.toMap(ThingShare::getUserId, Function.identity()));
        assertThat(sharesMap.get(1L).getPermissions(), equalTo(EnumSet.allOf(ThingPermission.class)));
        assertThat(sharesMap.get(2L).getPermissions(), equalTo(EnumSet.of(ThingPermission.READ, ThingPermission.EXECUTE)));

        // Change the share.
        logic.addOrUpdateShare(1, new ThingShare(2, EnumSet.of(ThingPermission.READ, ThingPermission.CONTROL)));
        assertThat(logic.canAccess(1, 2L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(1, 2L, ThingPermission.EXECUTE), is(false));
        assertThat(logic.canAccess(1, 2L, ThingPermission.CONTROL), is(true));

        // Unshare.
        logic.unshare(1, 2);
        assertThat(logic.canAccess(1, 2L, ThingPermission.READ), is(false));
        assertThat(logic.canAccess(1, 2L, ThingPermission.EXECUTE), is(false));
        assertThat(logic.canAccess(1, 2L, ThingPermission.CONTROL), is(false));

        // Check the permissions map again.
        shares = logic.getThingShares(1);
        assertThat(shares, hasSize(1));
        share = shares.iterator().next();
        assertThat(share.getUserId(), is(1L));
        assertThat(share.getPermissions(), equalTo(EnumSet.allOf(ThingPermission.class)));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateParentHierarchies() throws DatasourceInsertException, DatasourceUpdateException, ResolveReferenceException, DatasourceFindException {
        Thing thing1 = logic.getThing(1);
        Thing thing2 = logic.getThing(2);
        Thing thing3 = logic.registerThing(TestThing.class.getName(), null);
        Thing thing4 = logic.registerThing(TestThing.class.getName(), null);
        Thing thing5 = logic.registerThing(TestThing.class.getName(), null);
        TypedReferenceResolver<Thing> resolver = SimpleResolver.create(thing1, thing2, thing3, thing4, thing5);
        ServerReferenceResolver.getInstance().addResolver(Thing.class, resolver);

        // Hierarchy: thing1 ( thing2 (thing3, thing4), thing5 )
        logic.setParent(thing2, thing1);
        logic.setParent(thing3, thing2);
        logic.setParent(thing4, thing2);
        logic.setParent(thing5, thing1);

        // Check that loops fail.
        try {
            logic.setParent(thing1, thing2);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        assertThat(thing1.hasParent(), is(false));
        assertThat(thing2.hasParent(), is(true));
        assertThat(thing2.hasAncestor(thing1), is(true));
        assertThat(thing4.hasAncestor(thing1), is(true));
        assertThat(thing4.hasAncestor(thing2), is(true));
        assertThat(thing4.hasAncestor(thing5), is(false));
        assertThat(thing5.hasAncestor(thing4), is(false));
        assertThat(thing5.hasAncestor(thing1), is(true));

        // Check that permissions are inherited (user i can access thing i by default).
        assertThat(logic.canAccess(thing1.getId(), 1L, ThingPermission.READ), is(true)); // Normal.
        assertThat(logic.canAccess(thing1.getId(), 2L, ThingPermission.READ), is(false)); // User 2 cannot access thing 1.
        assertThat(logic.canAccess(thing2.getId(), 1L, ThingPermission.READ), is(true)); // But this is inherited.
        assertThat(logic.canAccess(thing3.getId(), 1L, ThingPermission.READ), is(true)); // And these, too.
        assertThat(logic.canAccess(thing3.getId(), 2L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(thing5.getId(), 1L, ThingPermission.READ), is(true));
        assertThat(logic.canAccess(thing5.getId(), 2L, ThingPermission.READ), is(false)); // User 2 cannot access thing 5.

        // Check the childrens collections.
        assertThat(logic.getChildren(thing1), containsInAnyOrder(thing2, thing5));
        assertThat(logic.getChildren(thing2), containsInAnyOrder(thing3, thing4));
        assertThat(logic.getChildren(thing3), is(empty()));
        assertThat(logic.getChildren(thing4), is(empty()));
        assertThat(logic.getChildren(thing5), is(empty()));

        // Check the hierarchy in the ThingInformation
        ThingInformation info = logic.map(1L, thing1, EnumSet.of(ThingInformation.Field.ALLCHILDREN));
        assertThat(info.getChildren(), hasSize(2));
        assertThat(info.getChildren(), contains(new CustomMatcher<ThingInformation>("thing2 info") {
            @Override
            public boolean matches(Object item) {
                ThingInformation info = (ThingInformation) item;
                if (info.getId() != 2) {
                    return false;
                }
                assertThat(info.getChildren(), hasSize(2));
                return true;
            }
        }, new CustomMatcher<ThingInformation>("thing5 info") {
            @Override
            public boolean matches(Object item) {
                ThingInformation info = (ThingInformation) item;
                if (info.getId() != 5) {
                    return false;
                }
                assertThat(info.getChildren(), is(empty()));
                return true;
            }
        }));

    }

    @Test(expected = NullPointerException.class)
    public void shouldFailOnNullChild() throws DatasourceUpdateException {
        logic.setParent(null, logic.getThing(1));
    }

}
