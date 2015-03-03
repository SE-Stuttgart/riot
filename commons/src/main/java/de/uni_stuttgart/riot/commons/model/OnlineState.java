package de.uni_stuttgart.riot.commons.model;


import java.util.EnumSet;
import java.util.HashMap;

/**
 * This enum is for identify the online state of a storable item (like a user, a device or a thing).
 *
 * @author Benny
 */
public enum OnlineState {
    /**
     * Shows that a item is "away".
     */
    STATUS_AWAY(0, "away"),
    /**
     * Shows that a item is "offline".
     */
    STATUS_OFFLINE(1, "offline"),
    /**
     * Shows that a item is "online".
     */
    STATUS_ONLINE(2, "online");

    private static HashMap<Long, OnlineState> enumIdMap = new HashMap<Long, OnlineState>();
    private static HashMap<String, OnlineState> enumMessageMap = new HashMap<String, OnlineState>();

    private long id;
    private String message;

    /**
     * Maps all enumerations with the id as key.
     */
    static {
        for (OnlineState obj : EnumSet.allOf(OnlineState.class)) {
            enumIdMap.put(obj.id, obj);
        }
        for (OnlineState obj : EnumSet.allOf(OnlineState.class)) {
            enumMessageMap.put(obj.message, obj);
        }
    }

    /**
     * Constructor.
     *
     * @param imageResource the resource id of the image
     */
    OnlineState(long id, String message) {
        this.id = id;
        this.message = message;
    }


    /**
     * Returns the enum mapped with the given value.
     *
     * @param message is the message of the searched enum
     * @return the enum object with the given value
     */
    public static OnlineState getEnumByValue(String message) {
        return enumMessageMap.get(message);
    }

    /**
     * Returns the enum mapped with the given value.
     *
     * @param id is the id of the searched enum
     * @return the enum object with the given value
     */
    public static OnlineState getEnumById(long id) {
        return enumIdMap.get(id);
    }
}
