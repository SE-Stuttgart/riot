package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * The Class CalendarSqlQueryDAO.
 */
public class CalendarSqlQueryDAO extends SqlQueryDAO<CalendarEntry> {

    @Override
    protected Class<CalendarEntry> getMyClazz() {
        return CalendarEntry.class;
    }

}
