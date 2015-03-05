package de.uni_stuttgart.riot.clientlibrary.thing.test;

import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.client.MirroringThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;

public class DeviceBehavior extends MirroringThingBehavior {

    public DeviceBehavior(ThingClient thingClient) {
        super(thingClient);
    }

    @Override
    protected <A extends ActionInstance> void userFiredAction(A actionInstance) {
    }

}
