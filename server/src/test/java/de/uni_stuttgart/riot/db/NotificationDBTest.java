package de.uni_stuttgart.riot.db;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.test.BaseDatabaseTest;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.NotificationBuilder;
import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

@TestData({ "/schema/schema_usermanagement.sql", "/data/testdata_usermanagement.sql", "/schema/schema_things.sql" })
public class NotificationDBTest extends BaseDatabaseTest {

    /**
     * The SUT is the DAO.
     */
    NotificationDAO dao = new NotificationDAO();

    @Test
    public void shouldCreateAndFindByID() throws DatasourceInsertException, DatasourceFindException {

        // Create a notification.
        Notification notification = NotificationBuilder.create() //
                .forUser(1) //
                .name("testName") //
                .titleKey("testTitleKey") //
                .messageKey("testMessageKey") //
                .param("test.param", "cool").severity(NotificationSeverity.ERROR) //
                .build();

        // Store it.
        assertThat(notification.getId(), is(nullValue()));
        dao.create(notification);
        assertThat(notification.getId(), not(nullValue()));

        // Retrieve it by ID.
        Notification restored = dao.findBy(notification.getId());
        assertThat(restored.getUserID(), is(1L));
        assertThat(restored.getName(), is("testName"));
        assertThat(restored.getTitle(), is("This is a test title!"));
        assertThat(restored.getMessage(), is("This is a test message with a cool parameter!"));
        assertThat(restored.getArguments().size(), is(1));
        assertThat(restored.getArguments().get("test.param"), is("cool"));
        assertThat(restored.getSeverity(), is(NotificationSeverity.ERROR));

    }

    @Test
    public void shouldFindByUser() throws DatasourceInsertException, DatasourceFindException, DatasourceUpdateException {

        // Create a notification.
        Notification notification = NotificationBuilder.create() //
                .forUser(1) //
                .name("testName") //
                .titleKey("testTitleKey") //
                .messageKey("testMessageKey") //
                .param("test.param", "boring").severity(NotificationSeverity.ERROR) //
                .build();

        // Ensure that Yoda currently has no notifications.
        assertThat(dao.findOutstandingNotifications(1L), is(empty()));

        // Store it.
        assertThat(notification.getId(), is(nullValue()));
        dao.create(notification);
        assertThat(notification.getId(), not(nullValue()));

        // Find it now.
        Collection<Notification> restoreds = dao.findOutstandingNotifications(1L);
        assertThat(restoreds, hasSize(1));
        Notification restored = restoreds.iterator().next();
        assertThat(restored.getUserID(), is(1L));
        assertThat(restored.getName(), is("testName"));
        assertThat(restored.getTitle(), is("This is a test title!"));
        assertThat(restored.getMessage(), is("This is a test message with a boring parameter!"));
        assertThat(restored.getArguments().size(), is(1));
        assertThat(restored.getArguments().get("test.param"), is("boring"));
        assertThat(restored.getSeverity(), is(NotificationSeverity.ERROR));

        // Mark it as dismissed, it should disappear.
        restored.setDismissed(true);
        dao.updateDismissed(restored.getId(), restored.getUserID(), true);
        assertThat(dao.findOutstandingNotifications(1L), is(empty()));

    }

}
