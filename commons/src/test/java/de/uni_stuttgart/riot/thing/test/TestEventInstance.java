package de.uni_stuttgart.riot.thing.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;

/**
 * An event instance type for testing that contains a parameter.
 * 
 * @author Philipp Keck
 */
public class TestEventInstance extends EventInstance {

    private final int parameter;

    public TestEventInstance(Event<? extends EventInstance> event, int parameter) {
        super(event);
        this.parameter = parameter;
    }

    @JsonCreator
    public TestEventInstance(JsonNode node) {
        super(node);
        this.parameter = node.get("parameter").asInt();
    }

    public int getParameter() {
        return parameter;
    }

}
