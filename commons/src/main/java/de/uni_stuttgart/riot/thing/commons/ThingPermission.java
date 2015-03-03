package de.uni_stuttgart.riot.thing.commons;

/**
 * The Enum ThingPermission.
 */
public enum ThingPermission {

    /** The right to read, but not modify a thing. */
    READ("read"),

    /** The right to modify a thing. */
    UPDATE("update"),

    /** The right to delete a thing. */
    DELETE("delete"),

    /** The right to share a thing with another user. */
    SHARE("share"),

    /** The right to do everything with the ting. */
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
     * Build the permission in way so that shiro understands it. If the id is invalid (id < 0), a wildcard instead of the id is used.
     *
     * @param id
     *            the id
     * @return the shiro permission
     */
    public String buildShiroPermission(long id) {
        if (id >= 0) {
            return "thing:" + id + ":" + shiroPermission;
        } else {
            return "thing:*:" + shiroPermission;
        }
    }
}
