package de.uni_stuttgart.riot.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.ResultSetHandler;

import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.server.commons.db.exception.NotFoundException;
import de.uni_stuttgart.riot.server.commons.rest.PaginatedCollection;

/**
 * The Class NotificationDAO is used to map {@link Notification} to the database.
 */
public class NotificationDAO {

    /**
     * Opens or retrieves the connection to be used by this DAO.
     * 
     * @return The connection.
     * @throws SQLException
     *             When opening a connection fails.
     */
    private Connection getConnection() throws SQLException {
        return ConnectionMgr.openConnection();
    }

    /**
     * Stores a new notification in the database and assigns it an ID.
     * 
     * @param notification
     *            The notification data, ID must be empty, will be filled after the call.
     * @throws DatasourceInsertException
     *             When storing the new notifiation failed.
     */
    public void create(Notification notification) throws DatasourceInsertException {
        try (Connection connection = getConnection()) {
            String query = "INSERT INTO notifications (id, userID, thingID, name, severity, titleKey, messageKey, time, dismissed)" + //
                    " VALUES (:id, :userID, :thingID, :name, :severity, :titleKey, :messageKey, :time, :dismissed)";
            try (Query stmt = connection.createQuery(query)) {
                stmt.addParameter("id", notification.getId());
                stmt.addParameter("userID", notification.getUserID());
                stmt.addParameter("thingID", notification.getThingID());
                stmt.addParameter("name", notification.getName());
                stmt.addParameter("severity", notification.getSeverity());
                stmt.addParameter("titleKey", notification.getTitleKey());
                stmt.addParameter("messageKey", notification.getMessageKey());
                stmt.addParameter("time", notification.getTime());
                stmt.addParameter("dismissed", notification.isDismissed());

                long key = stmt.executeUpdate().getKey(Long.class);
                if (key == 0) {
                    throw new DatasourceInsertException("Error on inserting new value");
                }
                notification.setId(key);
            }

            saveNotificationArguments(connection, notification);

        } catch (SQLException e) {
            throw new DatasourceInsertException(e);
        }
    }

    /**
     * Writes the arguments of the notification to the database.
     * 
     * @param connection
     *            The connection to be used.
     * @param n
     *            The notification that contains the arguments.
     * @throws SQLException
     *             When writing to the database fails.
     */
    private void saveNotificationArguments(Connection connection, Notification n) throws SQLException {
        String propertyQuery = "INSERT INTO notification_arguments (notificationID, name, val, valType) VALUES (:notificationID, :name, :val, :valType) ON DUPLICATE KEY UPDATE val = :val, valType = :valType";
        try (Query stmt = connection.createQuery(propertyQuery)) {
            for (Map.Entry<String, Object> argument : n.getArguments().entrySet()) {
                if (argument.getValue() == null) {
                    continue;
                }

                stmt.addParameter("notificationID", n.getId());
                stmt.addParameter("name", argument.getKey());
                stmt.addParameter("val", DBUtils.valueToString(argument.getValue()));
                stmt.addParameter("valType", argument.getValue().getClass().getName());
                stmt.addToBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Checks if the notification belongs to the user and then sets its {@link Notification#isDismissed()} field.
     * 
     * @param notificationID
     *            The ID of the notification.
     * @param userID
     *            The ID of the user.
     * @param dismissed
     *            Whether the notification is dismissed by the user or not.
     * @throws DatasourceUpdateException
     *             When the update failed.
     */
    public void updateDismissed(long notificationID, long userID, boolean dismissed) throws DatasourceUpdateException {
        String query = "UPDATE notifications SET dismissed = :dismissed WHERE id = :notificationID AND userID = :userID";
        try (Connection connection = getConnection(); Query stmt = connection.createQuery(query)) {
            stmt.addParameter("notificationID", notificationID);
            stmt.addParameter("userID", userID);
            stmt.addParameter("dismissed", dismissed);
            int res = stmt.executeUpdate().getResult();
            if (res == 0) {
                throw new DatasourceUpdateException("Update failed!");
            }
        } catch (SQLException e) {
            throw new DatasourceUpdateException(e);
        }
    }

    /**
     * Gets a notification by its ID.
     * 
     * @param id
     *            The ID of the notification.
     * @return The notification.
     * @throws DatasourceFindException
     *             If the notification does not exist.
     */
    public Notification findBy(long id) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM notifications WHERE id = :id";
            try (Query stmt = connection.createQuery(query)) {
                stmt.addParameter("id", id);
                Notification result = stmt.executeAndFetchFirst(new NotificationFetcher());
                if (result == null) {
                    throw new NotFoundException();
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    /**
     * Retrieves all non-dismissed notifications for the given user.
     * 
     * @param userID
     *            The ID of the user.
     * @return All notifications of the user with {@link Notification#isDismissed()} set to <tt>false</tt>.
     * @throws DatasourceFindException
     *             When fetching the data fails.
     */
    public Collection<Notification> findOutstandingNotifications(long userID) throws DatasourceFindException {
        String query = "SELECT * FROM notifications WHERE userID = :userID AND dismissed = FALSE ORDER BY time DESC";
        try (Connection connection = getConnection(); Query stmt = connection.createQuery(query)) {
            stmt.addParameter("userID", userID);
            return stmt.executeAndFetch(new NotificationFetcher());
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    /**
     * Retrieves all notifications for the given user, with pagination.
     * 
     * @param userID
     *            The ID of the user.
     * @param offset
     *            The offset of the first entry to return.
     * @param limit
     *            The maximum number of entries to return.
     * @return The paginated notifications.
     * @throws DatasourceFindException
     *             When retrieving the data fails.
     */
    public Collection<Notification> findAll(long userID, int offset, int limit) throws DatasourceFindException {
        if (offset < 0 || limit < 1) {
            throw new DatasourceFindException("Invalid parameter value");
        }

        String query = "SELECT * FROM notifications WHERE userID = :userID ORDER BY time DESC LIMIT :limit OFFSET :offset";
        String totalQuery = "SELECT COUNT(*) FROM notifications WHERE userID = :userID";
        try (Connection connection = getConnection()) {
            PaginatedCollection<Notification> result;
            try (Query stmt = connection.createQuery(query)) {
                stmt.addParameter("userID", userID);
                stmt.addParameter("offset", offset);
                stmt.addParameter("limit", limit);
                result = new PaginatedCollection<>(stmt.executeAndFetch(new NotificationFetcher()));
                result.setLimit(limit);
                result.setOffset(offset);
            }
            try (Query stmt = connection.createQuery(totalQuery)) {
                stmt.addParameter("userID", userID);
                result.setTotal(stmt.executeAndFetchFirst(Integer.class));
            }
            return result;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    /**
     * A handler for entries in a result set of notifications that will construct a new Notification instance for every line and retrieve
     * the arguments values separately and fill them into {@link Notification#getArguments()}.
     */
    private class NotificationFetcher implements ResultSetHandler<Notification> {

        @Override
        public Notification handle(ResultSet resultSet) throws SQLException {

            Notification notification = new Notification();
            notification.setId(resultSet.getLong("id"));
            notification.setUserID(resultSet.getLong("userID"));
            notification.setThingID(resultSet.getLong("thingID"));
            notification.setName(resultSet.getString("name"));
            notification.setSeverity(NotificationSeverity.valueOf(resultSet.getString("severity")));
            notification.setTitleKey(resultSet.getString("titleKey"));
            notification.setMessageKey(resultSet.getString("messageKey"));
            notification.setTime(new Date(resultSet.getTimestamp("time").getTime()));
            notification.setDismissed(resultSet.getBoolean("dismissed"));

            // Fetch argument values
            Map<String, Object> arguments = notification.getArguments();
            try (Connection connection = getConnection()) {
                String query = "SELECT name, val, valType FROM notification_arguments WHERE notificationID = :notificationID";
                try (Query stmt = connection.createQuery(query)) {
                    stmt.addParameter("notificationID", notification.getId());
                    for (UnresolvedEntry argument : stmt.executeAndFetch(UnresolvedEntry.class)) {
                        try {
                            arguments.put(argument.name, DBUtils.stringToValue(argument.val, Class.forName(argument.valType)));
                        } catch (ClassNotFoundException e) {
                            arguments.put(argument.name, argument.val);
                        }
                    }
                }
            }

            return notification;
        }
    }

    /**
     * Helper class that contains an entry as a String.
     */
    private static class UnresolvedEntry {
        public String name;
        public String val;
        public String valType;
    }

}
