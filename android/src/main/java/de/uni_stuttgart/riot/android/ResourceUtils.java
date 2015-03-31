package de.uni_stuttgart.riot.android;

import android.content.Context;

/**
 * Utility functions for looking up Android resources programmatically.
 * 
 * @author Philipp Keck
 */
public abstract class ResourceUtils {

    /**
     * Cannot instantiate.
     */
    private ResourceUtils() {
    }

    /**
     * Gets a String from the Android resources by its key.
     * 
     * @param context
     *            An Android context.
     * @param key
     *            The key of the String.
     * @return The String.
     */
    public static String getString(Context context, String key) {
        int resId = context.getResources().getIdentifier(key, "string", R.class.getPackage().getName());
        if (resId < 1) {
            throw new IllegalArgumentException("String with key " + key + " not found!");
        }
        return context.getString(resId);
    }
}
