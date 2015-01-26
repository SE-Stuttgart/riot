package de.uni_stuttgart.riot.server.commons.config;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * The Class ConfigurationStorable.
 * 
 * @author Niklas Schnabel
 */
public class ConfigurationStorable extends Storable {

    /** The key. */
    private String configKey;

    /** The value. */
    private String configValue;

    /** The data type. */
    private String dataType;

    /**
     * Instantiates a new configuration storable.
     */
    public ConfigurationStorable() {
    }

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
    public ConfigurationStorable(Long id, ConfigurationKey key, String value, String dataType) {
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
    public ConfigurationStorable(ConfigurationKey key, String value, String dataType) {
        super(-1L);
        this.configKey = key.toString();
        this.configValue = value;
        this.dataType = dataType;
    }

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
    public ConfigurationStorable(Long id, String key, String value, String dataType) {
        super(id);
        this.configKey = key;
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

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((configKey == null) ? 0 : configKey.hashCode());
        result = prime * result + ((configValue == null) ? 0 : configValue.hashCode());
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        return result;
    }

    // CHECKSTYLE: OFF - Auto generated code
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ConfigurationStorable other = (ConfigurationStorable) obj;
        if (configKey == null) {
            if (other.configKey != null)
                return false;
        } else if (!configKey.equals(other.configKey))
            return false;
        if (configValue == null) {
            if (other.configValue != null)
                return false;
        } else if (!configValue.equals(other.configValue))
            return false;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        return true;
    }
}
