package de.uni_stuttgart.riot.db;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * The Class CalendarSqlQueryDAO.
 */
public class CalendarSqlQueryDAO extends SqlQueryDAO<CalendarEntry> {

    /**
     * Constructor.
     * 
     * @param connection
     *            represents the connection to the database.
     * @param transaction
     *            set this flag if it is a transaction
     * @throws SQLException
     *             if an error to database access occurs.
     */
    public CalendarSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    @Override
    protected Class<CalendarEntry> getMyClazz() {
        return CalendarEntry.class;
    }

}
