package de.uni_stuttgart.riot.thing.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

/**
 * An action instance type for testing that contains a parameter.
 * 
 * @author Philipp Keck
 */
public class TestActionInstance extends ActionInstance {

    private final int parameter;

    public TestActionInstance(Action<? extends ActionInstance> action, int parameter) {
        super(action);
        this.parameter = parameter;
    }

    @JsonCreator
    public TestActionInstance(JsonNode node) {
        super(node);
        this.parameter = node.get("parameter").asInt();
    }

    public int getParameter() {
        return parameter;
    }

}
