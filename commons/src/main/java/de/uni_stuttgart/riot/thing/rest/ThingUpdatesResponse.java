package de.uni_stuttgart.riot.thing.rest;

import java.util.Collection;

import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventInstance;

/**
 * Aggregates all updates for a thing, i.e., everything that happened since the thing polled the last time and that is relevant to the
 * thing.
 * 
 * @author Philipp Keck
 */
public class ThingUpdatesResponse {

    private Collection<ActionInstance> outstandingActions;

    private Collection<EventInstance> occuredEvents;

    public Collection<ActionInstance> getOutstandingActions() {
        return outstandingActions;
    }

    public void setOutstandingActions(Collection<ActionInstance> outstandingActions) {
        this.outstandingActions = outstandingActions;
    }

    public Collection<EventInstance> getOccuredEvents() {
        return occuredEvents;
    }

    public void setOccuredEvents(Collection<EventInstance> occuredEvents) {
        this.occuredEvents = occuredEvents;
    }

}
