package de.uni_stuttgart.riot.logic.thing;

import de.uni_stuttgart.riot.commons.test.JerseyDBTestBase;
import de.uni_stuttgart.riot.server.commons.rest.RiotApplication;

public class ThingLogicTest extends JerseyDBTestBase {

    @Override
    protected RiotApplication configure() {
        return new RiotApplication();
    }
}
