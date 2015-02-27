package de.uni_stuttgart.riot.android.language;

import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import de.uni_stuttgart.riot.android.database.DatabaseAccess;
import de.uni_stuttgart.riot.android.database.RIOTDatabase;

/**
 * Supports the different languages.
 * 
 * <br/>
 * en = English <br/>
 * de = German
 *
 */
public class Language {

    private static RIOTDatabase db;
    private static Locale locale;
    private static Configuration config;

    private Language() {
        // For checkstyle.
    }

    /**
     * Sets the chosen language.
     * 
     * @param context
     *            Needs the application/activity context.
     */
    public static void setLanguage(Context context) {
        db = DatabaseAccess.getDatabase();

        // If the count is 0 the default language is set to English
        // Else the entry of the database is set as language
        if (db.getLanguageCount() == 0) {
            locale = new Locale("en");
        } else {
            locale = new Locale(db.getLanguage());
        }

        Locale.setDefault(locale);

        config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

}
