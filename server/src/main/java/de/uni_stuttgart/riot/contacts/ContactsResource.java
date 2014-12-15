package de.uni_stuttgart.riot.contacts;

import javax.ws.rs.Path;

import de.uni_stuttgart.riot.rest.BaseResource;

/**
 * The Class ContactsResource.
 */
@Path("contacts")
public class ContactsResource extends BaseResource<ContactEntry> {

    /**
     * Instantiates a new contacts resource.
     */
    public ContactsResource() {
        super(new ContactsModelManager());
    }

}
