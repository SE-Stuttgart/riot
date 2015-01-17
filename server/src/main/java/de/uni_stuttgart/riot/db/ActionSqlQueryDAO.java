package de.uni_stuttgart.riot.db;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;
import de.uni_stuttgart.riot.thing.commons.action.Action;

public class ActionSqlQueryDAO extends SqlQueryDAO<Action> {

    
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
    public ActionSqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    @Override
    protected Class<Action> getMyClazz() {
        return Action.class;
    }

}
