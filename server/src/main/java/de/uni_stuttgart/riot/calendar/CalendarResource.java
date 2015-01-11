package de.uni_stuttgart.riot.calendar;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;
import de.uni_stuttgart.riot.db.CalendarSqlQueryDAO;
import de.uni_stuttgart.riot.rest.BaseResource;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;

/**
 * The Class CalendarResource.
 */
@Path("calendar")
public class CalendarResource extends BaseResource<CalendarEntry> {

    /**
     * Instantiates a new calendar resource.
     * 
     * @throws SQLException .
     * @throws NamingException .
     * 
     */
    public CalendarResource() throws SQLException, NamingException {
        super(new CalendarSqlQueryDAO(ConnectionMgr.openConnection(), false));
    }

}
