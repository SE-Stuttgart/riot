package de.uni_stuttgart.riot.db;

import java.util.List;

import org.sql2o.Connection;
import org.sql2o.Sql2o;

import de.uni_stuttgart.riot.model.CalendarEvent;

public class CalendarEventDaoImpl implements CalendarEventDao {

    private static Sql2o sql2o; // framework to execute sql statements on JDBC compliant DBs

    static {
        // FIXME
        sql2o = new Sql2o("jdbc:mysql://localhost:3306/calendar", "root", "2209!R1ot");
    }

    @Override
    public List<CalendarEvent> getCalendarEvents() throws DaoException {
        String sql = "SELECT * FROM calendarEvents";

        try {
            Connection con = sql2o.open();
            return con.createQuery(sql).executeAndFetch(CalendarEvent.class);
        } catch (Exception exp) {
            throw new DaoException("Failed by retrieving all calendar Events.", exp);
        }
    }

    @Override
    public CalendarEvent getCalendarEventById(String id) throws DaoException {
        String sql = "SELECT * FROM calendarEvents WHERE id = :id";

        try {
            Connection con = sql2o.open();
            List<CalendarEvent> events = con.createQuery(sql).addParameter("id", id).executeAndFetch(CalendarEvent.class);

            if (!events.isEmpty()) {
                return events.get(0);
            } else {
                return null;
            }

        } catch (Exception exp) {
            throw new DaoException("Failed by retrieving calendar Event by id.", exp);
        }
    }

    @Override
    public void insertCalendarEvent(CalendarEvent calendarEvent) throws DaoException {

        String sql = "INSERT INTO calendarEvents(id, title, startTime, endTime, allDayEvent," + " description, location) " + "VALUES (:id, :title, :startTime, :endTime, :allDayEvent," + " :description, :location)";

        try {
            Connection con = sql2o.open();
            con.createQuery(sql).bind(calendarEvent).executeUpdate();

        } catch (Exception exp) {
            throw new DaoException("Failed by inserting a calendar Event.", exp);
        }
    }

    @Override
    public void updateCalendarEvent(CalendarEvent calendarEvent) throws DaoException {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteCalendarEvent(CalendarEvent calendarEvent) throws DaoException {
        String sql = "DELETE FROM calendarEvents WHERE id = :id";

        try {
            Connection con = sql2o.open();
            con.createQuery(sql).addParameter("id", calendarEvent.getId()).executeUpdate();

        } catch (Exception exp) {
            throw new DaoException("Failed by deleting a calendar Event.", exp);
        }
    }
}
