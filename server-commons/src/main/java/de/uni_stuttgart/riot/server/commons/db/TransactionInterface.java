package de.uni_stuttgart.riot.server.commons.db;

import java.sql.SQLException;

/**
 * Interface for SQL Transactions.
 *
 */
public interface TransactionInterface {

    /**
     * commits changes.
     * 
     * @throws SQLException
     *             if operation not possible
     */
    void commit() throws SQLException;

    /**
     * rolls back changes.
     * 
     * @throws SQLException
     *             if operation not possible
     */
    void rollback() throws SQLException;

}
