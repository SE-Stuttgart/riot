package de.uni_stuttgart.riot.server.commons.config;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * The Class ConfigurationStorable.
 * 
 * @author Niklas Schnabel
 */
public class ConfigurationStorable extends Storable {

    /** The key. */
    private final String configKey;

    /** The value. */
    private final String configValue;

    /** The data type. */
    private final String dataType;

    /**
     * Instantiates a new configuration storable.
     *
     * @param id
     *            the id
     * @param key
     *            the key
     * @param value
     *            the value
     * @param dataType
     *            the data type
     */
    public ConfigurationStorable(final Long id, final ConfigurationKey key, final String value, String dataType) {
        super(id);
        this.configKey = key.name();
        this.configValue = value;
        this.dataType = dataType;
    }

    /**
     * Instantiates a new configuration storable.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     * @param dataType
     *            the data type
     */
    public ConfigurationStorable(final ConfigurationKey key, final String value, String dataType) {
        super(-1L);
        this.configKey = key.toString();
        this.configValue = value;
        this.dataType = dataType;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public ConfigurationKey getKey() {
        return ConfigurationKey.valueOf(configKey);
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * Gets the data type.
     *
     * @return the data type
     */
    public String getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return "ConfigurationStorable [configKey=" + configKey + ", configValue=" + configValue + ", dataType=" + dataType + "]";
    }
}
