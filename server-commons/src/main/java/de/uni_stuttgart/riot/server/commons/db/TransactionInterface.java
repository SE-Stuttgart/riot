package de.uni_stuttgart.riot.server.commons.db;

import java.sql.SQLException;

public interface TransactionInterface {

	public abstract void commit() throws SQLException;

	public abstract void rollback() throws SQLException;

}