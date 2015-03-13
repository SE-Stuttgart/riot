package de.uni_stuttgart.riot.rule;

/**
 * A {@link RuleParameter} is a piece of input data to a {@link Rule}. Its value is defined by the current {@link RuleConfiguration} and
 * used by the rule to execute its behavior. {@link RuleParameter}s are similar in structure to the properties of things, but they do not
 * have associated events and actions and they can normally only be accessed from within the {@link Rule}. Also, {@link RuleParameter}s do
 * not store their current value.
 * 
 * @author Philipp Keck
 *
 * @param <V>
 *            The type of the parameter's values.
 */
public class RuleParameter<V> {

    private final transient Rule rule;
    private final String name;
    private final Class<V> valueType;

    /**
     * Creates a new rule parameter. Note that the constructor is internal. Rules are required to instantiate their parameters through
     * {@link Rule#newParameter(String, Class)}.
     * 
     * @param rule
     *            The rule that this parameter belongs to.
     * @param name
     *            The parameter name.
     * @param valueType
     *            The type of the parameter's values.
     */
    RuleParameter(Rule rule, String name, Class<V> valueType) {
        this.rule = rule;
        this.name = name;
        this.valueType = valueType;
    }

    /**
     * Gets the containing rule.
     * 
     * @return The rule that this parameter belongs to.
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Gets the parameter name.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the value type.
     * 
     * @return The type of the parameter's values.
     */
    public Class<V> getValueType() {
        return valueType;
    }

    /**
     * Gets the parameter value.
     *
     * @return The current value of the parameter.
     */
    public V get() {
        return valueType.cast(getRule().getConfiguration().get(name));
    }

}
