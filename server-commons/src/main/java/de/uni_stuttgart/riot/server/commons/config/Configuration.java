package de.uni_stuttgart.riot.server.commons.config;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.uni_stuttgart.riot.server.commons.db.ConnectionMgr;
import de.uni_stuttgart.riot.server.commons.db.SearchFields;
import de.uni_stuttgart.riot.server.commons.db.SearchParameter;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

/**
 * Manages all configurable values for the server. All data is saved in the database.
 * 
 * @author Niklas Schnabel
 */
public class Configuration {

    /** The DAO for accessing the database. */
    private static ConfigurationDAO dao;

    /** The Object Mapper. */
    private static ObjectMapper om;

    static {
        init();
    }

    /**
     * Constructor should not be used, because everything is static.
     */
    private Configuration() {
    }

    /**
     * Adds a new value if it does not exist yet. If the value does exist, it will be updated.
     *
     * @param <T>
     *            the generic type
     * @param key
     *            the key
     * @param value
     *            the value
     * @throws DatasourceInsertException
     *             thrown if saving the key and value fails
     * @throws DatasourceUpdateException
     *             the datasource update exception
     * @throws JsonProcessingException
     *             the json processing exception
     */
    public static <T> void addOrUpdate(ConfigurationKey key, T value) throws DatasourceInsertException, DatasourceUpdateException, JsonProcessingException {
        if (key.getValueType() != value.getClass()) {
            throw new ConfigurationException("The value has the not allowed type " + key.getValueType() + ". Instead it has the type " + value.getClass().getSimpleName());
        }

        String valueString = om.writeValueAsString(value);
        ConfigurationStorable old;
        try {
            old = dao.findByUniqueField(new SearchParameter(SearchFields.CONFIGKEY, key));
        } catch (DatasourceFindException e) {
            old = null;
        }

        if (old != null) {
            ConfigurationStorable newOne = new ConfigurationStorable(key, valueString, getClassName(value.getClass()));
            newOne.setId(old.getId());
            dao.update(newOne);
        } else {
            dao.insert(new ConfigurationStorable(key, valueString, getClassName(value.getClass())));
        }
    }

    /**
     * Delete a key/value pair.
     *
     * @param key
     *            the key
     * @throws JsonProcessingException
     *             the json processing exception
     */
    public static void delete(ConfigurationKey key) throws JsonProcessingException {
        try {
            dao.delete(dao.findByUniqueField(new SearchParameter(SearchFields.CONFIGKEY, key)));
        } catch (DatasourceDeleteException | DatasourceFindException e) {
            // item was already deleted, so ignore the exception
        }
    }

    /**
     * Gets the value of the given key. If no value is found for the given key, a {@link ConfigurationException} will be thrown.
     *
     * @param <T>
     *            the type of the value to be returned
     * @param key
     *            the key
     * @param type
     *            the type of the value to be returned
     * @return the value of the key
     */
    private static <T> T get(ConfigurationKey key, Class<T> type) {
        Object value;
        try {
            value = getValueFromDB(key);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        } catch (DatasourceFindException e) {
            value = null;
        }

        if (value != null) {
            if (type.isInstance(value)) {
                return type.cast(value);
            }
            throw new ConfigurationException("A value was found for the key " + key.toString() + //
                    " but the value is not a " + type.getName() + " instead it is a " + value.getClass());
        }
        throw new ConfigurationException("The key " + key.toString() + " does not exist");
    }

    /**
     * Gets the value as boolean.
     *
     * @param key
     *            the key
     * @return the boolean
     */
    public static boolean getBoolean(ConfigurationKey key) {
        return get(key, Boolean.class);
    }

    /**
     * Gets the class name.
     *
     * @param <T>
     *            the generic type
     * @param type
     *            the type
     * @return the class name
     */
    private static <T> String getClassName(Class<T> type) {
        return type.getSimpleName();
    }

    /**
     * Gets the value as double.
     *
     * @param key
     *            the key
     * @return the double
     */
    public static double getDouble(ConfigurationKey key) {
        return get(key, Double.class);
    }

    /**
     * Gets the value as float.
     *
     * @param key
     *            the key
     * @return the float
     */
    public static float getFloat(ConfigurationKey key) {
        return get(key, Float.class);
    }

    /**
     * Gets the value as integer.
     *
     * @param key
     *            the key
     * @return the int
     */
    public static int getInt(ConfigurationKey key) {
        return get(key, Integer.class);
    }

    /**
     * Gets the value as long.
     *
     * @param key
     *            the key
     * @return the long
     */
    public static long getLong(ConfigurationKey key) {
        return get(key, Long.class);
    }

    /**
     * Gets the value as string.
     *
     * @param key
     *            the key
     * @return the string
     */
    public static String getString(ConfigurationKey key) {
        return get(key, String.class);
    }

    /**
     * Gets the value from db.
     *
     * @param key
     *            the key
     * @return the value from db
     * @throws DatasourceFindException
     *             the datasource find exception
     * @throws JsonParseException
     *             the json parse exception
     * @throws JsonMappingException
     *             the json mapping exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static Object getValueFromDB(ConfigurationKey key) throws DatasourceFindException, JsonParseException, JsonMappingException, IOException {
        ConfigurationStorable configItem = dao.findByUniqueField(new SearchParameter(SearchFields.CONFIGKEY, key));
        ConfigurationKey keyDB = ConfigurationKey.valueOf(configItem.getConfigKey());

        return om.readValue(configItem.getConfigValue(), keyDB.getValueType());
    }

    /**
     * Clear the current configuration and get the values from the database.
     */
    public static void init() {

        try {
            dao = new ConfigurationDAO(ConnectionMgr.openConnection(), false);
            om = new ObjectMapper();
        } catch (Exception e) {
            throw new ConfigurationException(e);
        }
    }
}
