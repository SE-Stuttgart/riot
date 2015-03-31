package de.uni_stuttgart.riot.rule;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingState;

/**
 * Represents a possible configuration for a {@link Rule}, that is, the values of its parameters. This class is similar to what
 * {@link ThingState} is for {@link Thing}s. Since the rule classes themselves are not accessible at the client side, these descriptions are
 * necessary to create, view and edit rules.
 * 
 * @author Philipp Keck
 */
public class RuleConfiguration extends Storable implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String type;
    private RuleStatus status;
    private String name;
    private long ownerId;
    private final Map<String, Object> parameterValues;

    /**
     * Creates a new instance (also from JSON).
     * 
     * @param id
     *            The ID of the rule (configuration) or <tt>null</tt> if the rule has not been created yet.
     * @param type
     *            The type of the rule (as a fully qualified Java class name).
     * @param status
     *            The status of the rule.
     * @param name
     *            The name of the rule.
     * @param ownerId
     *            The ID of the user owning the rule.
     * @param parameterValues
     *            The values of the rule's parameters.
     */
    @JsonCreator
    public RuleConfiguration(@JsonProperty("id") Long id, @JsonProperty("type") String type, @JsonProperty("status") RuleStatus status, @JsonProperty("name") String name, @JsonProperty("ownerId") Long ownerId, @JsonProperty("parameterValues") Map<String, Object> parameterValues) {
        super(id);
        if (type == null) {
            throw new IllegalArgumentException("type must not be null!");
        }
        this.type = type;
        this.status = status;
        this.name = name;
        this.ownerId = (ownerId == null) ? 0 : ownerId;
        this.parameterValues = (parameterValues == null) ? new HashMap<String, Object>() : parameterValues;
    }

    /**
     * Creates a new empty instance.
     * 
     * @param type
     *            The type of the rule (as a fully qualified Java class name).
     */
    public RuleConfiguration(String type) {
        this(null, type, null, null, null, null);
    }

    /**
     * Gets the type of the rule (as a fully qualified Java class name).
     * 
     * @return The rule type.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the status of the rule.
     * 
     * @return The status.
     */
    public RuleStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the rule.
     * 
     * @param status
     *            The new status.
     */
    public void setStatus(RuleStatus status) {
        this.status = status;
    }

    /**
     * Gets the rule's name.
     * 
     * @return The name of the rule.
     */
    public String getName() {
        return name;
    }

    /**
     * Changes the name of the rule.
     * 
     * @param name
     *            The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the owner ID.
     * 
     * @return The ID of the user who owns the rule.
     */
    public long getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the owner ID.
     * 
     * @param ownerId
     *            The ID of the user who owns the rule.
     */
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Gets the values.
     * 
     * @return The values of the rule's parameters.
     */
    @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_ARRAY)
    public Map<String, Object> getParameterValues() {
        return parameterValues;
    }

    /**
     * Gets a parameter value by its name.
     * 
     * @param parameterName
     *            The parameter name.
     * @return The parameter value. <tt>null</tt> if the parameter does not exist.
     */
    public Object get(String parameterName) {
        return parameterValues == null ? null : parameterValues.get(parameterName);
    }

    /**
     * Gets a parameter value by its name.
     * 
     * @param <V>
     *            The expected type of the parameter.
     * @param parameterName
     *            The parameter name.
     * @param valueType
     *            The expected type of the parameter.
     * @return The parameter value. <tt>null</tt> if the parameter does not exist.
     */
    public <V> V get(String parameterName, Class<V> valueType) {
        return valueType.cast(get(parameterName));
    }

    /**
     * Sets the given parameter.
     * 
     * @param parameterName
     *            The name of the property.
     * @param parameterValue
     *            The value of the property.
     */
    public void set(String parameterName, Object parameterValue) {
        parameterValues.put(parameterName, parameterValue);
    }

    /**
     * Creates a shallow copy of the rule configuration.
     * 
     * @return The cloned instance.
     */
    public RuleConfiguration copy() {
        return new RuleConfiguration(getId(), type, status, name, ownerId, new HashMap<String, Object>(parameterValues));
    }

    // CHECKSTYLE:OFF
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (ownerId ^ (ownerId >>> 32));
        result = prime * result + ((parameterValues == null) ? 0 : parameterValues.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        RuleConfiguration other = (RuleConfiguration) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (ownerId != other.ownerId)
            return false;
        if (parameterValues == null) {
            if (other.parameterValues != null)
                return false;
        } else if (!parameterValues.equals(other.parameterValues))
            return false;
        if (status != other.status)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
