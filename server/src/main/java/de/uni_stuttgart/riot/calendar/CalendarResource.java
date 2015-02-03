package de.uni_stuttgart.riot.calendar;

import javax.ws.rs.Path;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;
import de.uni_stuttgart.riot.db.CalendarSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;

/**
 * The Class CalendarResource.
 */
@Path("calendar")
@RequiresAuthentication
public class CalendarResource extends BaseResource<CalendarEntry> {

    /**
     * Instantiates a new calendar resource.
     */
    public CalendarResource() {
        super(new CalendarSqlQueryDAO());
    }

    @Override
    public void init(CalendarEntry storable) throws Exception {
    }

}
