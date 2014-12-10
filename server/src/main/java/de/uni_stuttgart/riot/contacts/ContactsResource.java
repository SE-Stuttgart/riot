package de.uni_stuttgart.riot.contacts;

import java.net.URI;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import de.uni_stuttgart.riot.rest.BaseResource;

/**
 * The Class ContactsResource.
 */
@Path("contacts")
public class ContactsResource extends BaseResource<ContactEntry> {
    
   @Context
   private UriInfo uriInfo;
    
    /**
     * Instantiates a new contacts resource.
     */
    public ContactsResource() {
        super(new ContactsModelManager());
    }
    
    @Override
    protected URI getUriForModel(ContactEntry model) {
        return uriInfo.getBaseUriBuilder().path(this.getClass()).path("" + model.getId()).build();
    }

}
