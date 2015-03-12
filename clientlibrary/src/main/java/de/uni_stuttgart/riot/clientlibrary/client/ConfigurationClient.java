package de.uni_stuttgart.riot.clientlibrary.client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import de.uni_stuttgart.riot.clientlibrary.BaseClient;
import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;

/**
 * Rest client for the Configuration.
 */
public class ConfigurationClient extends BaseClient {

    /** The Constant GET_CONFIG. */
    private static final String GET_CONFIG = "config";

    /** The Constant GET_CONFIG_ID. */
    private static final String GET_CONFIG_ID = "config/";

    /** The Constant GET_CONFIG_KEY. */
    private static final String GET_CONFIG_KEY = "config/key/";

    /** The Constant ADD_CONFIG. */
    private static final String ADD_CONFIG = "config/";

    /** The Constant DELETE_CONFIG_ID. */
    private static final String DELETE_CONFIG_ID = "config/";

    /** The Constant DELETE_CONFIG_KEY. */
    private static final String DELETE_CONFIG_KEY = "config/key/";

    /** The Constant UPDATE_CONFIG. */
    private static final String UPDATE_CONFIG = "config/";

    /**
     * Constructor.
     * 
     * @param serverConnector
     *            the {@link ServerConnector} to be used
     */
    public ConfigurationClient(ServerConnector serverConnector) {
        super(serverConnector);
    }

    /**
     * Updates the configuration entry with id configId with the given values of configEntry.
     *
     * @param configId
     *            id of user to be updated
     * @param configEntry
     *            new values for the user
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public void updateConfigurationEntry(long configId, ConfigurationEntry configEntry) throws RequestException, IOException {
        configEntry.setId(configId);
        getConnector().doPUT(UPDATE_CONFIG + configId, configEntry);
    }

    /**
     * Deletes the configuration entry with id configId.
     * 
     * @param configId
     *            id of the configuration entry to be deleted
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public void removeConfigurationEntry(long configId) throws RequestException, IOException {
        getConnector().doDELETE(DELETE_CONFIG_ID + configId);
    }

    /**
     * Deletes the configuration entry with key configKey.
     * 
     * @param configKey
     *            key of configuration entry to be deleted
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public void removeConfigurationEntry(ConfigurationKey configKey) throws RequestException, IOException {
        getConnector().doDELETE(DELETE_CONFIG_KEY + configKey.name());
    }

    /**
     * Adds a configuration entry.
     *
     * @param <T>
     *            the generic type. Use the same type as saved in the used {@link ConfigurationKey}
     * @param key
     *            the key
     * @param value
     *            the value
     * @return the added configuration entry
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public <T> ConfigurationEntry addConfigurationEntry(ConfigurationKey key, T value) throws RequestException, IOException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (key.getValueType() != value.getClass()) {
            throw new RequestException("The value '" + value + "' has the type " + value.getClass() + ", but key '" + key.name() + "' requires the type to be " + key.getValueType());
        }

        ConfigurationEntry configEntry = new ConfigurationEntry(key, value.toString(), value.getClass().getSimpleName());
        return getConnector().doPOST(ADD_CONFIG, configEntry, ConfigurationEntry.class);
    }

    /**
     * Returns the configuration entry with the given id.
     * 
     * @param id
     *            configuration entry id
     * @return the user
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws NotFoundException
     *             When the configuration entry could not be found.
     */
    public ConfigurationEntry getConfigurationEntry(long id) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(GET_CONFIG_ID + id, ConfigurationEntry.class);
    }

    /**
     * Returns the configuration entry with the given key.
     * 
     * @param key
     *            configuration key
     * @return the user
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws NotFoundException
     *             When the configuration entry could not be found.
     */
    public ConfigurationEntry getConfigurationEntry(ConfigurationKey key) throws RequestException, IOException, NotFoundException {
        return getConnector().doGET(GET_CONFIG_KEY + key.name(), ConfigurationEntry.class);
    }

    /**
     * Gets the configuration value directly casted to the correct type.
     *
     * @param <T>
     *            the generic type
     * @param id
     *            the id
     * @param type
     *            the type to cast to. Can be retrieved from {@link ConfigurationKey#getValueType()}
     * @return the configuration value
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws NotFoundException
     *             When the configuration entry could not be found.
     */
    public <T> T getConfigurationValue(long id, Class<T> type) throws RequestException, IOException, NotFoundException {
        return processConfigurationEntry(type, getConfigurationEntry(id));
    }

    /**
     * Gets the configuration value directly casted to the correct type.
     *
     * @param <T>
     *            the generic type
     * @param key
     *            the configuration key from {@link ConfigurationKey}
     * @param type
     *            the type to cast to. Can be retrieved from {@link ConfigurationKey#getValueType()}
     * @return the configuration value
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws NotFoundException
     *             When the configuration entry could not be found.
     */
    public <T> T getConfigurationValue(ConfigurationKey key, Class<T> type) throws RequestException, IOException, NotFoundException {
        return processConfigurationEntry(type, getConfigurationEntry(key));
    }

    /**
     * Process configuration entry.
     *
     * @param <T>
     *            the generic type
     * @param type
     *            the type
     * @param configEntry
     *            the config entry
     * @return the t
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    private <T> T processConfigurationEntry(Class<T> type, ConfigurationEntry configEntry) throws RequestException {
        if (StringUtils.equals(type.getSimpleName(), configEntry.getDataType())) {
            if (StringUtils.equals(configEntry.getDataType(), String.class.getSimpleName())) {
                return type.cast(configEntry.getConfigValue().subSequence(1, configEntry.getConfigValue().length() - 1));
            }

            try {
                Method method = type.getMethod("valueOf", String.class);
                return type.cast(method.invoke(null, configEntry.getConfigValue()));
            } catch (NoSuchMethodException e) {
                throw new RequestException(e);
            } catch (SecurityException e) {
                throw new RequestException(e);
            } catch (IllegalAccessException e) {
                throw new RequestException(e);
            } catch (IllegalArgumentException e) {
                throw new RequestException(e);
            } catch (InvocationTargetException e) {
                throw new RequestException(e);
            }
        }
        throw new RequestException("The ConfigurationEntry has the type " + configEntry.getDataType() + " and not the desired type " + type.getSimpleName());
    }

    /**
     * Returns all configuration entries.
     * 
     * @return collection of all configuration entries.
     * @throws IOException
     *             When a network error occured.
     * @throws RequestException
     *             When the request could not be executed.
     */
    public Collection<ConfigurationEntry> getConfiguration() throws RequestException, IOException {
        try {
            return getConnector().doGETCollection(GET_CONFIG, ConfigurationEntry.class);
        } catch (NotFoundException e) {
            throw new RequestException(e);
        }
    }

}
