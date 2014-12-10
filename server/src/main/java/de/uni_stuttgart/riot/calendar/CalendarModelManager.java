package de.uni_stuttgart.riot.calendar;

import java.sql.SQLException;
import java.util.Collection;

import javax.naming.NamingException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.db.ConnectionMgr;
import de.uni_stuttgart.riot.db.DaoException;
import de.uni_stuttgart.riot.rest.ModelManager;

/**
 * The Class CalendarModelManager.
 */
public class CalendarModelManager implements ModelManager<CalendarEntry> {

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#getById(long)
     */
    @Override
    public CalendarEntry getById(long id) throws DaoException {
        String sql = "SELECT * FROM calendarEntries WHERE id = :id";
        try (Connection con = ConnectionMgr.openConnection()) {
            return con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(CalendarEntry.class);
        } catch (NamingException | SQLException e) {
            throw new DaoException("Could not access calendar entry.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#get()
     */
    @Override
    public Collection<CalendarEntry> get() throws DaoException {
        String sql = "SELECT * FROM calendarEntries";
        // return entries.values();
        try (Connection con = ConnectionMgr.openConnection()) {
            return con.createQuery(sql).executeAndFetch(CalendarEntry.class);
        } catch (NamingException | SQLException e) {
            throw new DaoException("Could not access calendar entries.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#create(de.uni_stuttgart.riot.rest.ResourceModel)
     */
    @Override
    public CalendarEntry create(CalendarEntry model) throws DaoException {
        String sql = "INSERT INTO calendarEntries(title, startTime, endTime, allDayEvent, description, location) VALUES (:title, :startTime, :endTime, :allDayEvent, :description, :location)";

        try (Connection con = ConnectionMgr.openConnection()) {
            long id = con.createQuery(sql, true).bind(model).executeUpdate().getKey(Long.class);
            model.setId(id);
            return model;

        } catch (Exception e) {
            throw new DaoException("Failed to insert a calendar Event.", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#delete(long)
     */
    @Override
    public boolean delete(long id) throws DaoException {
        String sql = "DELETE FROM calendarEntries WHERE id = :id";

        try (Connection con = ConnectionMgr.openConnection()) {
            int rows = con.createQuery(sql).addParameter("id", id).executeUpdate().getResult();
            return rows > 0;
        } catch (NamingException | SQLException e) {
            throw new DaoException("Could not delete calendar entry", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ModelManager#update(de.uni_stuttgart.riot.rest.ResourceModel)
     */
    @Override
    public boolean update(CalendarEntry model) throws DaoException {
        String sql = "UPDATE calendarEntries SET title = :title, startTime = :startTime, endTime = :endTime, allDayEvent = :allDayEvent, description = :description, location = :location WHERE id = :id";

        try (Connection con = ConnectionMgr.openConnection()) {
            int rows = con.createQuery(sql, true).bind(model).executeUpdate().getResult();
            return rows > 0;
        } catch (Exception e) {
            throw new DaoException("Could not update calendar entry", e);
        }
    }

}
