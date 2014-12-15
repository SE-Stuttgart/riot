package de.uni_stuttgart.riot.db;

import java.util.List;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.contacts.ContactsModelManager;
import de.uni_stuttgart.riot.model.CalendarEvent;

/**
 * @deprecated Superseded by {@link ContactsModelManager}.
 */
public class CalendarEventDaoImpl implements CalendarEventDao {

    @Override
    public List<CalendarEvent> getCalendarEvents() throws DaoException {
        String sql = "SELECT * FROM calendarEvents";

        try (Connection con = ConnectionMgr.openConnection()) {
            return con.createQuery(sql).executeAndFetch(CalendarEvent.class);
        } catch (Exception exp) {
            throw new DaoException("Failed when retrieving all calendar Events.", exp);
        }
    }

    @Override
    public CalendarEvent getCalendarEventById(String id) throws DaoException {
        String sql = "SELECT * FROM calendarEvents WHERE id = :id";

        try (Connection con = ConnectionMgr.openConnection()) {
            List<CalendarEvent> events = con.createQuery(sql).addParameter("id", id).executeAndFetch(CalendarEvent.class);

            if (!events.isEmpty()) {
                return events.get(0);
            } else {
                return null;
            }

        } catch (Exception exp) {
            throw new DaoException("Failed when retrieving calendar Event by id.", exp);
        }
    }

    @Override
    public void insertCalendarEvent(CalendarEvent calendarEvent) throws DaoException {
        String sql = "INSERT INTO calendarEvents(id, title, startTime, endTime, allDayEvent," + " description, location) " + "VALUES (:id, :title, :startTime, :endTime, :allDayEvent," + " :description, :location)";

        try (Connection con = ConnectionMgr.openConnection()) {
            con.createQuery(sql).bind(calendarEvent).executeUpdate();
        } catch (Exception exp) {
            throw new DaoException("Failed when inserting a calendar Event.", exp);
        }
    }

    @Override
    public void updateCalendarEvent(CalendarEvent calendarEvent) throws DaoException {
        // TODO Auto-generated method stub
    }

    @Override
    public void deleteCalendarEvent(CalendarEvent calendarEvent) throws DaoException {
        String sql = "DELETE FROM calendarEvents WHERE id = :id";

        try (Connection con = ConnectionMgr.openConnection()) {
            con.createQuery(sql).addParameter("id", calendarEvent.getId()).executeUpdate();

        } catch (Exception exp) {
            throw new DaoException("Failed when deleting a calendar Event.", exp);
        }
    }
}
