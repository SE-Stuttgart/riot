package de.uni_stuttgart.riot.thing.rest;

/**
 * The Enum ThingPermission.
 */
public enum ThingPermission {

    /** The right to read a thing and receive events from it, but not modify it. */
    READ,

    /** The right to send actions to a thing. */
    CONTROL,

    /** The right to send events and receive updates for a thing. */
    EXECUTE,

    /** The right to delete a thing. */
    DELETE,

    /** The right to share a thing with another user. */
    SHARE

}
