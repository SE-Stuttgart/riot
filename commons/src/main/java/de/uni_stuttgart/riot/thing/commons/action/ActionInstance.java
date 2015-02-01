package de.uni_stuttgart.riot.thing.commons.action;


import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Instance;

/**
 * TODO .
 *
 */
public abstract class ActionInstance extends Instance {

    public ActionInstance(Timestamp time, long thingId) {
        super(time, thingId);
    }

}
