package de.uni_stuttgart.riot.db;

import java.util.List;

import de.uni_stuttgart.riot.model.CalendarEvent;

public interface CalendarEventDao {

    public List<CalendarEvent> getCalendarEvents() throws DaoException;

    public CalendarEvent getCalendarEventById(String id) throws DaoException;

    public void insertCalendarEvent(CalendarEvent calendarEvent) throws DaoException;

    public void updateCalendarEvent(CalendarEvent calendarEvent) throws DaoException;

    public void deleteCalendarEvent(CalendarEvent calendarEvent) throws DaoException;
}
