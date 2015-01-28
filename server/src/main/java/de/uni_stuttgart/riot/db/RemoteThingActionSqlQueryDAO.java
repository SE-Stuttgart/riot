package de.uni_stuttgart.riot.db;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.remote.RemoteThingAction;

/**
 * DAO for {@link RemoteThingAction}.
 *
 */
public class RemoteThingActionSqlQueryDAO extends SqlQueryDAO<RemoteThingAction> {

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
    public RemoteThingActionSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    @Override
    protected Class<RemoteThingAction> getMyClazz() {
        return RemoteThingAction.class;
    }

}
