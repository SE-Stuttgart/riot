package de.uni_stuttgart.riot.server.commons.config;

import de.uni_stuttgart.riot.server.commons.db.SqlQueryDAO;

/**
 * The Class ConfigurationDAO.
 * 
 * @author Niklas Schnabel
 */
public class ConfigurationDAO extends SqlQueryDAO<ConfigurationStorable> {

    @Override
    protected Class<ConfigurationStorable> getMyClazz() {
        return ConfigurationStorable.class;
    }

}
