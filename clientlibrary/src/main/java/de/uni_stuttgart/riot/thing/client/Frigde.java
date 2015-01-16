package de.uni_stuttgart.riot.thing.client;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;


public class Frigde extends ClientThing {

    public Frigde(ThingClient thingClient, String name, long delay) {
        super(thingClient, name,delay);
    }

    @Override
    protected void initActions() {
    }

    @Override
    protected void initProperties() {
        this.addProperty(new Property<Boolean>("on", false));
    }

    @Override
    protected void handleActionInstances(ActionInstance actionInstance) {
        
    }

}
