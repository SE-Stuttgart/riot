package de.uni_stuttgart.riot.db.thing;

import java.sql.SQLException;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.thing.rest.ThingShare;

/**
 * DAO for ThingUser entities.
 * 
 * @author Philipp Keck
 */
public class ThingUserSqlQueryDAO extends SqlQueryDAO<ThingUser> {

    @Override
    protected Class<ThingUser> getMyClazz() {
        return ThingUser.class;
    }

    /**
     * Ensures that the permissions from the given share are the only ones that the user has for the thing.
     * 
     * @param thingId
     *            The thing ID.
     * @param share
     *            The share (contains user and permissions). If it is empty, an existing entry will be deleted and no new one will be
     *            inserted.
     * @throws DatasourceDeleteException
     *             When writing to the database fails.
     * @throws DatasourceInsertException
     *             When writing to the database fails.
     */
    public void saveThingShare(long thingId, ThingShare share) throws DatasourceDeleteException, DatasourceInsertException {
        if (share == null) {
            throw new IllegalArgumentException("share must not be null");
        }

        // Remove old entry, if present.
        deleteThingShare(thingId, share.getUserId());

        // Insert new entry.
        if (!share.isEmpty()) {
            try (Connection connection = getConnection()) {
                insert(new ThingUser(thingId, share.getUserId(), share.getPermissions()));
            } catch (SQLException e) {
                throw new DatasourceInsertException(e);
            }
        }
    }

    /**
     * Deletes a share, effectively revoking all privileges.
     * 
     * @param thingId
     *            The thing id.
     * @param userId
     *            The user id.
     * @throws DatasourceDeleteException
     *             When the deletion fails.
     */
    public void deleteThingShare(long thingId, long userId) throws DatasourceDeleteException {
        try (Connection connection = getConnection()) {
            String deleteQuery = "DELETE FROM things_users WHERE thingID = :thingID AND userID = :userID";
            try (Query stmt = connection.createQuery(deleteQuery)) {
                stmt.addParameter("thingID", thingId);
                stmt.addParameter("userID", userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DatasourceDeleteException(e);
        }
    }

}
