package de.uni_stuttgart.riot.contacts;

import javax.ws.rs.Path;

import org.apache.shiro.authz.annotation.RequiresAuthentication;

import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.db.ContactSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;

/**
 * The Class ContactsResource.
 */
@Path("contacts")
@RequiresAuthentication
public class ContactsResource extends BaseResource<Contact> {

    /**
     * Instantiates a new contacts resource.
     */
    public ContactsResource() {
        super(new ContactSqlQueryDAO());
    }

    @Override
    public void init(Contact storable) throws Exception {
    }

}
