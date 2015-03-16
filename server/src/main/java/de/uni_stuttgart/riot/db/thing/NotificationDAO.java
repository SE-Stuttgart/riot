package de.uni_stuttgart.riot.db.thing;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * The Class NotificationDAO is used to map {@link Notification} to the database.
 */
public class NotificationDAO extends SqlQueryDAO<Notification> {

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO#getMyClazz()
     */
    @Override
    protected Class<Notification> getMyClazz() {
        return Notification.class;
    }

}
