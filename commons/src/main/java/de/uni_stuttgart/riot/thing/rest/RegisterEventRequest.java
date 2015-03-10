package de.uni_stuttgart.riot.thing.rest;

/**
 * The request message to register on events. It is used in {@link ThingService}.
 */
public class RegisterEventRequest {

    private long targetThingID;
    private String targetEventName;

    /**
     * Instantiates a new empty register request.
     */
    public RegisterEventRequest() {
    }

    /**
     * Instantiates a new register request.
     * 
     * @param targetThingID
     *            The Thing that is being observed, i.e., the thing that owns the event.
     * @param targetEventName
     *            The name of the event to register to.
     */
    public RegisterEventRequest(long targetThingID, String targetEventName) {
        super();
        this.targetThingID = targetThingID;
        this.targetEventName = targetEventName;
    }

    public long getTargetThingID() {
        return targetThingID;
    }

    public void setTargetThingID(long targetThingID) {
        this.targetThingID = targetThingID;
    }

    public String getTargetEventName() {
        return targetEventName;
    }

    public void setTargetEventName(String targetEventName) {
        this.targetEventName = targetEventName;
    }

}
