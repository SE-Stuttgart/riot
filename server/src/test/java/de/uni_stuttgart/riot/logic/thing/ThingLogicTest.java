package de.uni_stuttgart.riot.logic.thing;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.db.JerseyDBTestBase;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;
import de.uni_stuttgart.riot.thing.commons.ThingInfo;
import de.uni_stuttgart.riot.thing.remote.RemoteThing;
import de.uni_stuttgart.riot.thing.remote.ThingLogic;

public class ThingLogicTest extends JerseyDBTestBase {

    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }

    @Test
    public void getThingTest() throws DatasourceFindException {
        ThingLogic logic = new ThingLogic();
        RemoteThing thing = logic.getThing(1);
        assertEquals("Android", thing.getName());
        assertEquals(2, thing.getActions().size());
        assertEquals(2, thing.getProperties().size());
    }

    @Test
    public void getAllThingsTest() throws DatasourceFindException, IOException {
        ThingLogic logic = new ThingLogic();
        Collection<ThingInfo> infos = logic.getAllThings(1);
        assertEquals(1, infos.size());
    }
}
