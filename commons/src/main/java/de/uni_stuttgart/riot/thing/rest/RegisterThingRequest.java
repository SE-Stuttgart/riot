package de.uni_stuttgart.riot.thing.rest;

import de.uni_stuttgart.riot.thing.ThingState;

/**
 * A request for registering a new thing (sent by the client that executes the thing).
 * 
 * @author Philipp Keck
 */
public class RegisterThingRequest {

    private String type;
    private String name;
    private ThingState initialState;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ThingState getInitialState() {
        return initialState;
    }

    public void setInitialState(ThingState initialState) {
        this.initialState = initialState;
    }

}
