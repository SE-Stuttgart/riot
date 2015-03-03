package de.uni_stuttgart.riot.thing.commons;

/**
 * The Class ShareRequest.
 */
public class ShareRequest {

    /** The right. */
    private ThingPermission permission;

    /** The thing id. */
    private long thingId;

    /** The user id. */
    private long userId;

    /**
     * Instantiates a new share request.
     */
    public ShareRequest() {
    }

    /**
     * Instantiates a new share request.
     *
     * @param userId
     *            the user
     * @param thingId
     *            the thing id
     * @param accessRight
     *            the right
     */
    public ShareRequest(long userId, long thingId, ThingPermission accessRight) {
        this.userId = userId;
        this.thingId = thingId;
        this.permission = accessRight;
    }

    /**
     * Gets the permission.
     *
     * @return the permission
     */
    public ThingPermission getPermission() {
        return permission;
    }

    /**
     * Gets the thing id.
     *
     * @return the thing id
     */
    public long getThingId() {
        return thingId;
    }

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public long getUserId() {
        return userId;
    }

}
