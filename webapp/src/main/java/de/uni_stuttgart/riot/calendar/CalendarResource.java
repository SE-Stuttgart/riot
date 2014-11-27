package de.uni_stuttgart.riot.calendar;

import java.net.URI;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.uni_stuttgart.riot.rest.BaseResource;

/**
 * The Class CalendarResource.
 */
@Path("calendar")
public class CalendarResource extends BaseResource<CalendarEntry> {
    
   @Context
   private UriInfo uriInfo;
    
    /**
     * Instantiates a new calendar resource.
     */
    public CalendarResource() {
        super(new CalendarModelManager());
    }
    
    @Override
    protected URI getUriForModel(CalendarEntry model) {
        return uriInfo.getBaseUriBuilder().path(this.getClass()).path("" + model.getId()).build();
    }

}
