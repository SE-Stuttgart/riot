package de.uni_stuttgart.riot.clientlibrary.server.client;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.type.TypeReference;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationEntry;
import de.uni_stuttgart.riot.commons.rest.data.config.ConfigurationKey;

/**
 * Rest client for the Configuration.
 */
public class ConfigurationClient {

    /** The Constant PREFIX. */
    private static final String PREFIX = "/api/v1/";

    /** The Constant GET_CONFIG. */
    private static final String GET_CONFIG = PREFIX + "config";

    /** The Constant GET_CONFIG_ID. */
    private static final String GET_CONFIG_ID = PREFIX + "config/";

    /** The Constant GET_CONFIG_KEY. */
    private static final String GET_CONFIG_KEY = PREFIX + "config/key/";

    /** The Constant ADD_CONFIG. */
    private static final String ADD_CONFIG = PREFIX + "config/";

    /** The Constant DELETE_CONFIG_ID. */
    private static final String DELETE_CONFIG_ID = PREFIX + "config/";

    /** The Constant DELETE_CONFIG_KEY. */
    private static final String DELETE_CONFIG_KEY = PREFIX + "config/key/";

    /** The Constant UPDATE_CONFIG. */
    private static final String UPDATE_CONFIG = PREFIX + "config/";

    /** The login client. */
    private final LoginClient loginClient;

    /**
     * Constructor.
     * 
     * @param loginClient
     *            the {@link LoginClient} to be used
     */
    public ConfigurationClient(LoginClient loginClient) {
        this.loginClient = loginClient;
    }

    /**
     * Updates the configuration entry with id configId with the given values of configEntry.
     *
     * @param configId
     *            id of user to be updated
     * @param configEntry
     *            new values for the user
     * @return the int
     * @throws RequestException .
     */
    public int updateConfigurationEntry(long configId, ConfigurationEntry configEntry) throws RequestException {
        configEntry.setId(configId);
        HttpResponse response = this.loginClient.put(this.loginClient.getServerUrl() + UPDATE_CONFIG + configId, configEntry);
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Deletes the configuration entry with id configId.
     * 
     * @param configId
     *            id of the configuration entry to be deleted
     * @return http code (200 ok)
     * @throws RequestException .
     */
    public int removeConfigurationEntry(long configId) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + DELETE_CONFIG_ID + configId);
        int result = response.getStatusLine().getStatusCode();
        return result;
    }

    /**
     * Deletes the configuration entry with key configKey.
     * 
     * @param configKey
     *            key of configuration entry to be deleted
     * @return http code (200 ok)
     * @throws RequestException .
     */
    public int removeConfigurationEntry(ConfigurationKey configKey) throws RequestException {
        HttpResponse response = this.loginClient.delete(this.loginClient.getServerUrl() + DELETE_CONFIG_KEY + configKey.name());
        int result = response.getStatusLine().getStatusCode();
        return result;
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
     * @throws RequestException .
     */
    public <T> ConfigurationEntry addConfigurationEntry(ConfigurationKey key, T value) throws RequestException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        if (key.getValueType() != value.getClass()) {
            throw new RequestException("The value '" + value + "' has the type " + value.getClass() + ", but key '" + key.name() + "' requires the type to be " + key.getValueType());
        }

        ConfigurationEntry configEntry = new ConfigurationEntry(key, value.toString(), value.getClass().getSimpleName());
        HttpResponse response = this.loginClient.post(this.loginClient.getServerUrl() + ADD_CONFIG, configEntry);
        try {
            ConfigurationEntry result = loginClient.getJsonMapper().readValue(response.getEntity().getContent(), ConfigurationEntry.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the configuration entry with the given id.
     * 
     * @param id
     *            configuration entry id
     * @return the user
     * @throws RequestException .
     */
    public ConfigurationEntry getConfigurationEntry(long id) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_CONFIG_ID + id);
        try {
            ConfigurationEntry result = loginClient.getJsonMapper().readValue(response.getEntity().getContent(), ConfigurationEntry.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }

    /**
     * Returns the configuration entry with the given key.
     * 
     * @param key
     *            configuration key
     * @return the user
     * @throws RequestException .
     */
    public ConfigurationEntry getConfigurationEntry(ConfigurationKey key) throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_CONFIG_KEY + key.name());
        try {
            ConfigurationEntry result = loginClient.getJsonMapper().readValue(response.getEntity().getContent(), ConfigurationEntry.class);
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
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
     * @throws RequestException
     *             the request exception
     */
    private <T> T getConfigurationValue(long id, Class<T> type) throws RequestException {
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
     * @throws RequestException
     *             the request exception
     */
    private <T> T getConfigurationValue(ConfigurationKey key, Class<T> type) throws RequestException {
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
     * @throws RequestException
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
     * Gets configuration value as integer.
     *
     * @param key
     *            the key
     * @return the integer configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public int getIntegerConfigurationValue(ConfigurationKey key) throws RequestException {
        return getConfigurationValue(key, Integer.class);
    }

    /**
     * Gets configuration value as long.
     *
     * @param key
     *            the key
     * @return the long configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public long getLongConfigurationValue(ConfigurationKey key) throws RequestException {
        return getConfigurationValue(key, Long.class);
    }

    /**
     * Gets configuration value as float.
     *
     * @param key
     *            the key
     * @return the float configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public float getFloatConfigurationValue(ConfigurationKey key) throws RequestException {
        return getConfigurationValue(key, Float.class);
    }

    /**
     * Gets configuration value as double.
     *
     * @param key
     *            the key
     * @return the double configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public double getDoubleConfigurationValue(ConfigurationKey key) throws RequestException {
        return getConfigurationValue(key, Double.class);
    }

    /**
     * Gets configuration value as boolean.
     *
     * @param key
     *            the key
     * @return the boolean configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public boolean getBooleanConfigurationValue(ConfigurationKey key) throws RequestException {
        return getConfigurationValue(key, Boolean.class);
    }

    /**
     * Gets configuration value as string.
     *
     * @param key
     *            the key
     * @return the string configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public String getStringConfigurationValue(ConfigurationKey key) throws RequestException {
        return getConfigurationValue(key, String.class);
    }

    /**
     * Gets configuration value as integer.
     *
     * @param id
     *            the id
     * @return the integer configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public int getIntegerConfigurationValue(long id) throws RequestException {
        return getConfigurationValue(id, Integer.class);
    }

    /**
     * Gets configuration value as long.
     *
     * @param id
     *            the id
     * @return the long configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public long getLongConfigurationValue(long id) throws RequestException {
        return getConfigurationValue(id, Long.class);
    }

    /**
     * Gets configuration value as float.
     *
     * @param id
     *            the id
     * @return the float configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public float getFloatConfigurationValue(long id) throws RequestException {
        return getConfigurationValue(id, Float.class);
    }

    /**
     * Gets configuration value as double.
     *
     * @param id
     *            the id
     * @return the double configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public double getDoubleConfigurationValue(long id) throws RequestException {
        return getConfigurationValue(id, Double.class);
    }

    /**
     * Gets configuration value as boolean.
     *
     * @param id
     *            the id
     * @return the boolean configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public boolean getBooleanConfigurationValue(long id) throws RequestException {
        return getConfigurationValue(id, Boolean.class);
    }

    /**
     * Gets configuration value as string.
     *
     * @param id
     *            the id
     * @return the string configuration value
     * @throws RequestException
     *             thrown if value is not found or an error occurs
     */
    public String getStringConfigurationValue(long id) throws RequestException {
        return getConfigurationValue(id, String.class);
    }

    /**
     * Returns all configuration entries.
     * 
     * @return collection of all configuration entries.
     * @throws RequestException .
     */
    public Collection<ConfigurationEntry> getConfiguration() throws RequestException {
        HttpResponse response = this.loginClient.get(this.loginClient.getServerUrl() + GET_CONFIG);
        try {
            Collection<ConfigurationEntry> result = loginClient.getJsonMapper().readValue(response.getEntity().getContent(), new TypeReference<Collection<ConfigurationEntry>>() {
            });
            return result;
        } catch (Exception e) {
            throw new RequestException(e);
        }
    }
}
