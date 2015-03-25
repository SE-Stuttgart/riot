package de.uni_stuttgart.riot.notification.server;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.NotificationBuilder;
import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.rule.RuleConfiguration;
import de.uni_stuttgart.riot.rule.RuleStatus;
import de.uni_stuttgart.riot.rule.server.RuleLogic;
import de.uni_stuttgart.riot.rule.test.TestNotificationRule;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.server.test.ResetHelper;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.ThingTestUtils;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.rest.ThingShare;
import de.uni_stuttgart.riot.thing.server.ThingLogic;
import de.uni_stuttgart.riot.thing.test.TestEventInstance;
import de.uni_stuttgart.riot.thing.test.TestThing;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql", "/testdata/testdata_things.sql", "/schema/schema_rules.sql", "/schema/schema_notifications.sql" })
public class NotificationLogicTest extends BaseDatabaseTest {

    NotificationLogic logic;
    ThingLogic thingLogic;
    RuleLogic ruleLogic;

    @Before
    public void resetNotificationLogic() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ResetHelper.resetNotificationLogic();
        logic = NotificationLogic.getNotificationLogic();
        ResetHelper.resetThingLogic();
        thingLogic = ThingLogic.getThingLogic();
        ResetHelper.resetRuleLogic();
        ruleLogic = RuleLogic.getRuleLogic();
    }

    @Test
    public void shouldGetNotificationFromEvent() throws DatasourceFindException, DatasourceDeleteException, DatasourceInsertException, DatasourceUpdateException {

        // Prepare thing.
        TestThing thing = (TestThing) thingLogic.getThing(1);

        // Precheck that notification queues are empty.
        assertThat(logic.getOutstandingNotifications(1L), is(empty()));
        assertThat(logic.getOutstandingNotifications(2L), is(empty()));

        // Fire the simple notification, which should so far only hit Yoda.
        thingLogic.fireEvent(new EventInstance(thing.getSimpleNotification()));
        List<Notification> yodaNotifs = new ArrayList<>(logic.getOutstandingNotifications(1L));
        assertThat(yodaNotifs, hasSize(1));
        assertThat(yodaNotifs.get(0).getMessage(), is("Simple message"));
        assertThat(logic.getOutstandingNotifications(2L), is(empty()));

        // Now share it with R2D2, who should then retrieve it, too.
        thingLogic.addOrUpdateShare(1L, new ThingShare(2L, EnumSet.of(ThingPermission.CONTROL)));
        logic.fireNotificationEvent(thing.getSimpleNotification(), new EventInstance(thing.getSimpleNotification()));
        assertThat(logic.getOutstandingNotifications(1L), hasSize(2));
        assertThat(logic.getOutstandingNotifications(2L), hasSize(1));

        // Now fire the parameterized event, which should only hit Yoda because it requires READ permission.
        logic.fireNotificationEvent(thing.getParameterizedNotification(), new TestEventInstance(thing.getParameterizedNotification(), 55));
        assertThat(logic.getOutstandingNotifications(2L), hasSize(1));
        yodaNotifs = new ArrayList<>(logic.getOutstandingNotifications(1L));
        assertThat(yodaNotifs, hasSize(3));
        assertThat(yodaNotifs.get(2).getMessage(), is("Simple message"));
        assertThat(yodaNotifs.get(1).getMessage(), is("Simple message"));
        assertThat(yodaNotifs.get(0).getMessage(), is("Param 55 message"));
        assertThat(yodaNotifs.get(0).getArguments().get("parameter"), is(55));

        // Mark the second and last as read (newest is first).
        Notification first = yodaNotifs.get(2);
        Notification secondYoda = yodaNotifs.get(1);
        Notification third = yodaNotifs.get(0);
        secondYoda.setDismissed(true);
        third.setDismissed(true);
        logic.updateNotification(secondYoda, 1L);
        logic.updateNotification(third, 1L);

        // Only the first should remain with Yoda.
        yodaNotifs = new ArrayList<>(logic.getOutstandingNotifications(1L));
        assertThat(yodaNotifs, hasSize(1));
        assertThat(yodaNotifs.get(0).getId(), is(first.getId()));

        // R2D2 should still have his instane of the second notification, because it is separate from Yoda's.
        List<Notification> r2d2Notifs = new ArrayList<>(logic.getOutstandingNotifications(2L));
        assertThat(r2d2Notifs, hasSize(1));
        r2d2Notifs.get(0).setDismissed(true);
        logic.updateNotification(r2d2Notifs.get(0), 2L);
        assertThat(logic.getOutstandingNotifications(2L), is(empty()));

        // Tidy up.
        thingLogic.unshare(1L, 2L);
    }

    @Test
    public void shouldGetNotificationsFromRules() throws DatasourceInsertException, DatasourceDeleteException, DatasourceFindException {

        // Prepare the thing.
        TestThing thing = (TestThing) thingLogic.getThing(2L);

        // Create and execute a rule.
        RuleConfiguration config = new RuleConfiguration(null, TestNotificationRule.class.getName(), RuleStatus.ACTIVE, "NotiRule", 2L, null);
        config.set("thing", 2L);
        config.set("limit", 42);
        config = ruleLogic.addNewRule(config, 2L);
        assertThat(config.getId(), notNullValue());

        // So far, nothing should have happened.
        thing.setInt(41);
        ThingTestUtils.flushAllActions(thing);
        assertThat(logic.getOutstandingNotifications(2L), is(empty()));

        // Now exceed the limit.
        thing.setInt(43);
        ThingTestUtils.flushAllActions(thing);
        List<Notification> notifs = new ArrayList<>(logic.getOutstandingNotifications(2L));
        assertThat(notifs, hasSize(1));
        assertThat(notifs.get(0).getTitle(), is("This is a test warning"));
        assertThat(notifs.get(0).getMessage(), is("Your test is at 43!"));
        assertThat(notifs.get(0).getSeverity(), is(NotificationSeverity.WARNING));

        // Do it again, nothing should have changed.
        thing.setInt(50);
        ThingTestUtils.flushAllActions(thing);
        assertThat(logic.getOutstandingNotifications(2L), hasSize(1));

        // Go over twice the limit, which should fire another warning.
        thing.setInt(420);
        ThingTestUtils.flushAllActions(thing);
        notifs = new ArrayList<>(logic.getOutstandingNotifications(2L));
        assertThat(notifs, hasSize(2));
        assertThat(notifs.get(0).getTitle(), is("This is a test warning"));
        assertThat(notifs.get(0).getMessage(), is("Your test is at 420!"));
        assertThat(notifs.get(0).getSeverity(), is(NotificationSeverity.WARNING_IMPORTANT));
        assertThat(notifs.get(1).getTitle(), is("This is a test warning"));
        assertThat(notifs.get(1).getMessage(), is("Your test is at 43!"));
        assertThat(notifs.get(1).getSeverity(), is(NotificationSeverity.WARNING));

        // Tidy up.
        ruleLogic.deleteRule(config.getId());

    }

    @Test
    public void shouldFireListeners() {

        // Register listener for Vader.
        @SuppressWarnings("unchecked")
        Consumer<Notification> listener = mock(Consumer.class);
        logic.registerListener(3L, listener);

        // Fire an arbitrary notification.
        Notification notification = NotificationBuilder.create().forUser(3L).name("Test") //
                .titleKey("test").messageKey("test").severity(NotificationSeverity.ERROR).build();
        logic.postNotification(notification);
        verify(listener, times(1)).accept(notification);

        // Fire a notification for another user.
        Notification notification2 = NotificationBuilder.create().forUser(2L).name("Test") //
                .titleKey("test").messageKey("test").severity(NotificationSeverity.ERROR).build();
        logic.postNotification(notification2);
        verify(listener, never()).accept(notification2);

        // Unregister.
        logic.unregisterListener(3L, listener);
        Notification notification3 = NotificationBuilder.create().forUser(3L).name("Test3") //
                .titleKey("test").messageKey("test").severity(NotificationSeverity.ERROR).build();
        logic.postNotification(notification3);
        verify(listener, never()).accept(notification3);

    }

}
