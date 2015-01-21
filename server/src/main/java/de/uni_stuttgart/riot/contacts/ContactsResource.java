package de.uni_stuttgart.riot.contacts;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.ws.rs.Path;

import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.db.ContactSqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.rest.BaseResource;

/**
 * The Class ContactsResource.
 */
@Path("contacts")
public class ContactsResource extends BaseResource<Contact> {

    /**
     * Instantiates a new contacts resource.
     * 
     * @throws NamingException .
     * @throws SQLException .
     */
    public ContactsResource() throws SQLException, NamingException {
        super(new ContactSqlQueryDAO(ConnectionMgr.openConnection(), false));
    }

}
