package de.uni_stuttgart.riot.rule;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingState;

/**
 * Represents a possible configuration for a {@link Rule}, that is, the values of its parameters. This class is similar to what
 * {@link ThingState} is for {@link Thing}s. Since the rule classes themselves are not accessible at the client side, these descriptions are
 * necessary to create, view and edit rules.
 * 
 * @author Philipp Keck
 */
public class RuleConfiguration {

    private final String type;
    private Long ruleId;
    private RuleStatus status;
    private String name;
    private long ownerId;
    private final Map<String, Object> parameterValues;

    /**
     * Creates a new instance (also from JSON).
     * 
     * @param type
     *            The type of the rule (as a fully qualified Java class name).
     *
     * @param ruleId
     *            The ID of the rule or <tt>null</tt> if the rule has not been created yet.
     * @param status
     *            The status of the rule.
     * @param parameterValues
     *            The values of the rule's parameters.
     */
    @JsonCreator
    public RuleConfiguration(@JsonProperty("type") String type, @JsonProperty("ruleId") Long ruleId, @JsonProperty("status") RuleStatus status, @JsonProperty("parameterValues") Map<String, Object> parameterValues) {
        if (parameterValues == null) {
            throw new IllegalArgumentException("parameterValues must not be null!");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null!");
        }
        this.type = type;
        this.ruleId = ruleId;
        this.status = status;
        this.parameterValues = parameterValues;
    }

    /**
     * Creates a new instance.
     * 
     * @param type
     *            The type of the rule (as a fully qualified Java class name).
     * @param parameterValues
     *            The values of the rule's parameters.
     */
    public RuleConfiguration(String type, Map<String, Object> parameterValues) {
        this(type, null, RuleStatus.DEACTIVATED, parameterValues);
    }

    /**
     * Creates a new empty instance.
     * 
     * @param type
     *            The type of the rule (as a fully qualified Java class name).
     */
    public RuleConfiguration(String type) {
        this(type, new HashMap<String, Object>());
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
     * Gets the ID of the rule that this configuration is for.
     * 
     * @return The ID of the rule or <tt>null</tt> if the rule has not been created yet.
     */
    public Long getRuleId() {
        return ruleId;
    }

    /**
     * Sets the ID of the rule that this configuration is for.
     * 
     * @param ruleId
     *            The ID of the rule, never <tt>null</tt>.
     */
    public void setRuleId(Long ruleId) {
        if (ruleId == null) {
            throw new IllegalArgumentException("ruleId must not be null!");
        }
        this.ruleId = ruleId;
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
    @JsonTypeInfo(use = Id.CLASS, include = As.WRAPPER_OBJECT)
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

}
