package de.uni_stuttgart.riot.server.commons.config;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * The Class ConfigurationDAO.
 * 
 * @author Niklas Schnabel
 */
public class ConfigurationDAO extends SqlQueryDAO<ConfigurationStorable> {

    /**
     * Instantiates a new configuration dao.
     *
     * @param connection
     *            the connection
     * @param transaction
     *            the transaction
     * @throws SQLException
     *             the SQL exception
     */
    public ConfigurationDAO(Connection connection, boolean transaction) throws SQLException {
        super(connection, transaction);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO#getMyClazz()
     */
    @Override
    protected Class<ConfigurationStorable> getMyClazz() {
        return ConfigurationStorable.class;
    }

}
