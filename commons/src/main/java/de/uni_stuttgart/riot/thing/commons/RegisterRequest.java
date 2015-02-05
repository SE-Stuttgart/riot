package de.uni_stuttgart.riot.thing.commons;

import de.uni_stuttgart.riot.thing.commons.event.Event;

/**
 * The request message to register on events. It is used in {@link ThingService}.
 */
@SuppressWarnings("rawtypes")
public class RegisterRequest {

    private long registerOnThingId;
    private long thingId;
    private Event event;

    /**
     * Instantiates a new register request.
     */
    public RegisterRequest() {
    }

    /**
     * Instantiates a new register request.
     *
     * @param thingid
     *            the thing id that subscribes to event.
     * @param registerOnThingId
     *            the thing id to register on.
     * @param event
     *            the event to register on.
     */
    public RegisterRequest(long thingid, long registerOnThingId, Event event) {
        this.event = event;
        this.thingId = thingid;
        this.registerOnThingId = registerOnThingId;
    }

    public long getThingId() {
        return thingId;
    }

    public void setThingId(long thingId) {
        this.thingId = thingId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event action) {
        this.event = action;
    }

    public long getRegisterOnThingId() {
        return registerOnThingId;
    }

    public void setRegisterOnThingId(long registerOnThingId) {
        this.registerOnThingId = registerOnThingId;
    }
}
