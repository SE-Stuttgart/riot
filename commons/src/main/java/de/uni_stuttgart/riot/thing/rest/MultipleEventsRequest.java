package de.uni_stuttgart.riot.thing.rest;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The request message to register and/or unregister from/to multiple events. It is used in {@link ThingService}.
 */
public class MultipleEventsRequest {

    private Collection<RegisterEventRequest> registerTo;
    private Collection<RegisterEventRequest> unregisterFrom;

    /**
     * Instantiates a new empty register request.
     */
    public MultipleEventsRequest() {
        this(new ArrayList<RegisterEventRequest>(), new ArrayList<RegisterEventRequest>());
    }

    /**
     * Creates a new request.
     * 
     * @param registerTo
     *            The events to register to.
     * @param unregisterFrom
     *            The events to unregister from.
     */
    public MultipleEventsRequest(Collection<RegisterEventRequest> registerTo, Collection<RegisterEventRequest> unregisterFrom) {
        this.registerTo = registerTo;
        this.unregisterFrom = unregisterFrom;
    }

    public Collection<RegisterEventRequest> getRegisterTo() {
        return registerTo;
    }

    public void setRegisterTo(Collection<RegisterEventRequest> registerTo) {
        this.registerTo = registerTo;
    }

    public Collection<RegisterEventRequest> getUnregisterFrom() {
        return unregisterFrom;
    }

    public void setUnregisterFrom(Collection<RegisterEventRequest> unregisterFrom) {
        this.unregisterFrom = unregisterFrom;
    }

}
