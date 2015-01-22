package de.uni_stuttgart.riot.android.management;


import java.util.EnumSet;
import java.util.HashMap;

/**
 * Created by Benny on 09.01.2015.
 */
public enum OnlineState {
    STATUS_AWAY("away", android.R.drawable.presence_away),
    STATUS_BUSY("busy", android.R.drawable.presence_busy),
    STATUS_INVISIBLE("invisible", android.R.drawable.presence_invisible),
    STATUS_OFFLINE("offline", android.R.drawable.presence_offline),
    STATUS_ONLINE("online", android.R.drawable.presence_online);

    private String message;
    private int imageResource;
    private static HashMap<String, OnlineState> enumMap = new HashMap<String, OnlineState>();

    /**
     * Maps all enumerations with the id as key
     */
    static {
        for (OnlineState obj : EnumSet.allOf(OnlineState.class)) {
            enumMap.put(obj.message, obj);
        }
    }

    /**
     * Constructor
     *
     * @param imageResource the resource id of the image
     */
    OnlineState(String message, int imageResource) {
        this.message = message;
        this.imageResource = imageResource;
    }

    /**
     * Help to get the resource id
     *
     * @return the resource id of the image for that online state
     */
    public int getR() {
        return this.imageResource;
    }

    /**
     * Returns the enum mapped with the given value
     *
     * @param message is the value message of the searched enum
     * @return the enum object with the given value
     */
    public static OnlineState getEnumByValue(String message) {
        return enumMap.get(message);
    }
}
