package de.uni_stuttgart.riot.db;

import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * DAO for {@link Contact}.
 * 
 */
public class ContactSqlQueryDAO extends SqlQueryDAO<Contact> {

    @Override
    protected Class<Contact> getMyClazz() {
        return Contact.class;
    }

}
