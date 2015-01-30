package de.uni_stuttgart.riot.server.commons.config;

import java.sql.SQLException;

import org.sql2o.Connection;

import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * The Class ConfigurationDAO.
 * 
 * @author Niklas Schnabel
 */
public class ConfigurationDAO extends SqlQueryDAO<ConfigurationEntry> {

    @Override
    protected Class<ConfigurationEntry> getMyClazz() {
        return ConfigurationEntry.class;
    }

}
