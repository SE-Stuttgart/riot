package de.uni_stuttgart.riot.rule;

/**
 * This exception is thrown when trying to launch or shutdown a rule with an invalid configuration.
 * 
 * @author Philipp Keck
 */
public class IllegalRuleParameterException extends IllegalRuleConfigurationException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for IllegalRuleParameterException.
     * 
     * @param parameter
     *            The illegal parameter.
     */
    public IllegalRuleParameterException(RuleParameter<?> parameter) {
        this(parameter, null);
    }

    /**
     * Constructor for IllegalRuleParameterException.
     * 
     * @param parameter
     *            The illegal parameter.
     * @param hint
     *            A hint message that indicates what a legal parameter value would be.
     */
    public IllegalRuleParameterException(RuleParameter<?> parameter, String hint) {
        super(buildMessage(parameter, hint));
    }

    private static String buildMessage(RuleParameter<?> parameter, String hint) {
        StringBuilder builder = new StringBuilder();
        builder.append(parameter.get());
        builder.append(" is not a valid value for parameter ");
        builder.append(parameter.getName());
        builder.append(" of rule ");
        builder.append(parameter.getRule().getClass().getSimpleName());
        builder.append("!");
        if (hint != null) {
            builder.append(" ");
            builder.append(hint);
        }
        return builder.toString();
    }

}
