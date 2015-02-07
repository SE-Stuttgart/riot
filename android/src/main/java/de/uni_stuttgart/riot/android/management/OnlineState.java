package de.uni_stuttgart.riot.android.management;


import java.util.EnumSet;
import java.util.HashMap;

/**
 * Created by Benny on 09.01.2015.
 * This enum is for identify the online state of a storable item (like a user, a device or a thing).
 */
public enum OnlineState {
    /**
     * Shows that a item is "away".
     */
    STATUS_AWAY(0, "away", android.R.drawable.presence_away),
    /**
     * Shows that a item is "busy".
     */
    STATUS_BUSY(1, "busy", android.R.drawable.presence_busy),
    /**
     * Shows that a item is "invisible".
     */
    STATUS_INVISIBLE(2, "invisible", android.R.drawable.presence_invisible),
    /**
     * Shows that a item is "offline".
     */
    STATUS_OFFLINE(3, "offline", android.R.drawable.presence_offline),
    /**
     * Shows that a item is "online".
     */
    STATUS_ONLINE(4, "online", android.R.drawable.presence_online);

    private static HashMap<Long, OnlineState> enumIdMap = new HashMap<Long, OnlineState>();
    private static HashMap<String, OnlineState> enumMessageMap = new HashMap<String, OnlineState>();

    private long id;
    private String message;
    private int imageResource;

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
    OnlineState(long id, String message, int imageResource) {
        this.id = id;
        this.message = message;
        this.imageResource = imageResource;
    }

    /**
     * Help to get the resource id.
     *
     * @return the resource id of the image for that online state
     */
    public int getR() {
        return this.imageResource;
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
