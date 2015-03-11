package de.uni_stuttgart.riot.thing.rest;

/**
 * The Class ShareRequest.
 */
public class ShareRequest {

    /** The right. */
    private ThingPermission permission;

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
     * @param accessRight
     *            the right
     */
    public ShareRequest(long userId, ThingPermission accessRight) {
        this.userId = userId;
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
     * Gets the user id.
     *
     * @return the user id
     */
    public long getUserId() {
        return userId;
    }

}
