package de.uni_stuttgart.riot.db;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.data.contact.Contact;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * DAO for {@link Contact}.
 * 
 */
public class ContactSqlQueryDAO extends SqlQueryDAO<Contact> {

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
    public ContactSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    @Override
    protected Class<Contact> getMyClazz() {
        return Contact.class;
    }

}
