package de.uni_stuttgart.riot.calendar;

import javax.ws.rs.Path;

import de.uni_stuttgart.riot.rest.BaseResource;

/**
 * The Class CalendarResource.
 */
@Path("calendar")
public class CalendarResource extends BaseResource<CalendarEntry> {

    /**
     * Instantiates a new calendar resource.
     */
    public CalendarResource() {
        super(new CalendarModelManager());
    }

}
