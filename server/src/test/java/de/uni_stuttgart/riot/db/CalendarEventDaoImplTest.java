package de.uni_stuttgart.riot.db;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uni_stuttgart.riot.model.CalendarEvent;

public class CalendarEventDaoImplTest {

    private static CalendarEventDao dao;
    private static CalendarEvent event;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        dao = new CalendarEventDaoImpl();
        event = new CalendarEvent("Important Meeting", new Date(), new Date());
    }

    @Test
    public void insertAndDeleteCalendarEventTest() {
        try {
            // --- Inserting
            dao.insertCalendarEvent(event);

            CalendarEvent retrievedEvent = dao.getCalendarEventById(event.getId());

            Assert.assertNotNull(retrievedEvent);
            Assert.assertEquals(event.getId(), retrievedEvent.getId());
            Assert.assertEquals(event.getTitle(), retrievedEvent.getTitle());
            // FIXME Date
            // Assert.assertEquals(event.getStartTime(), retrievedEvent.getStartTime());

            // --- Deleting
            dao.deleteCalendarEvent(event);

            retrievedEvent = dao.getCalendarEventById(event.getId());
            Assert.assertNull(retrievedEvent);

        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void getCalendarEventsTest() {

        List<CalendarEvent> events;
        try {
            // --- Inserting
            dao.insertCalendarEvent(event);

            // --- Getting All events
            events = dao.getCalendarEvents();

            for (CalendarEvent calendarEntry : events) {
                System.out.println("entry id:" + calendarEntry.getId());
            }

            // --- Deleting
            dao.deleteCalendarEvent(event);

        } catch (DaoException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
