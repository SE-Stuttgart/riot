package de.uni_stuttgart.riot.rule;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.uni_stuttgart.riot.thing.ParameterDescription;
import de.uni_stuttgart.riot.thing.ThingDescription;

/**
 * A class to describe the structure of a certain type of rules. The description consists of all parameter descriptions. Since the rule
 * classes themselves are not accessible at the client side, these descriptions are necessary to create, view and edit rules. This class is
 * similar to {@link ThingDescription}, but is important to note that at rule description does not reference the concrete rule it describes.
 * Instead, a rule description describes a certain <em>type</em> of rules. This is because rule descriptions are also used in a prescriptive
 * way, that is, the rule description is received in order to instantiate a new instance of a certain type of rule, and in order to tell
 * which parameters are required for that.
 * 
 * @author Philipp Keck
 */
public class RuleDescription {

    private final String type;
    private final List<ParameterDescription> parameters;

    /**
     * Creates a new description instance.
     * 
     * @param type
     *            The type of the rule (as a fully qualified Java class name).
     * @param parameters
     *            The parameter descriptions.
     */
    @JsonCreator
    RuleDescription(@JsonProperty("type") String type, @JsonProperty("parameters") List<ParameterDescription> parameters) {
        this.type = type;
        this.parameters = parameters;
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
     * Gets the parameter descriptions.
     * 
     * @return The parameter descriptions.
     */
    public List<ParameterDescription> getParameters() {
        return parameters;
    }

    /**
     * Finds an parameter description by the parameter name.
     * 
     * @param name
     *            The name of the parameter.
     * @return The parameter description.
     */
    public ParameterDescription getParameterByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        for (ParameterDescription parameterDescription : parameters) {
            if (parameterDescription.getName().equals(name)) {
                return parameterDescription;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "RuleDescription [type=" + type + ", parameters=" + parameters + "]";
    }

}
