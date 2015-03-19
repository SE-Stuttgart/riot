package de.uni_stuttgart.riot.thing.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.references.Reference;
import de.uni_stuttgart.riot.references.StaticReference;
import de.uni_stuttgart.riot.references.TestReferenceable;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An action instance type that contains a reference parameter.
 * 
 * @author Philipp Keck
 */
public class TestRefActionInstance extends ActionInstance {

    @Parameter(ui = UIHint.ReferenceDropDown.class)
    private final Reference<TestReferenceable> parameter;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = { ThingPermission.SHARE, ThingPermission.READ })
    private final Reference<TestThing> thingParameter;

    public TestRefActionInstance(Action<? extends ActionInstance> action, TestReferenceable parameter, TestThing thing) {
        super(action);
        this.parameter = StaticReference.create(parameter);
        this.thingParameter = StaticReference.create(thing);
    }

    @JsonCreator
    public TestRefActionInstance(JsonNode node) {
        super(node);
        this.parameter = StaticReference.create(node.get("parameter"));
        this.thingParameter = StaticReference.create(node.get("thingParameter"));
    }

    public Reference<TestReferenceable> getParameter() {
        return parameter;
    }

    public Reference<TestThing> getThingParameter() {
        return thingParameter;
    }

}
