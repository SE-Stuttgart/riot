package de.uni_stuttgart.riot.thing.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.references.Reference;
import de.uni_stuttgart.riot.references.StaticReference;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An action instance type that contains a reference parameter.
 * 
 * @author Philipp Keck
 */
public class TestRefActionInstance extends ActionInstance {

    @Parameter(ui = UIHint.ReferenceDropDown.class)
    private final Reference<TestReferenceable> parameter;

    public TestRefActionInstance(Action<? extends ActionInstance> action, TestReferenceable parameter) {
        super(action);
        this.parameter = StaticReference.create(parameter);
    }

    @JsonCreator
    public TestRefActionInstance(JsonNode node) {
        super(node);
        this.parameter = StaticReference.create(node.get("parameter"));
    }

    public Reference<TestReferenceable> getParameter() {
        return parameter;
    }

}
