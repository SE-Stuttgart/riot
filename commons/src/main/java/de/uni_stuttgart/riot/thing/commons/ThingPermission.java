package de.uni_stuttgart.riot.thing.commons;

/**
 * The Enum ThingPermission.
 */
public enum ThingPermission {

    /** The right to read a thing and receive events from it, but not modify it. */
    READ("read"),

    /** The right to send actions to a thing. */
    CONTROL("control"),

    /** The right to send events and receive updates for a thing. */
    EXECUTE("execute"),

    /** The right to delete a thing. */
    DELETE("delete"),

    /** The right to share a thing with another user. */
    SHARE("share"),

    /** The right to do everything with the thing. */
    FULL("*");

    /** The shiro permission. */
    private String shiroPermission;

    /**
     * Instantiates a new thing permission.
     *
     * @param shiroPermission
     *            the shiro permission
     */
    private ThingPermission(String shiroPermission) {
        this.shiroPermission = shiroPermission;
    }

    /**
     * Build the permission in way so that shiro understands it.
     *
     * @param id
     *            the id
     * @return the shiro permission
     */
    public String buildShiroPermission(long id) {
        return "thing:" + id + ":" + shiroPermission;
    }

    /**
     * Build the permission in a way so that shiro understands it, using a wildcard for the thing, so that it applies to all things.
     * 
     * @return the shiro permission
     */
    public String buildWildcardShiroPermission() {
        return "thing:*:" + shiroPermission;
    }

}
