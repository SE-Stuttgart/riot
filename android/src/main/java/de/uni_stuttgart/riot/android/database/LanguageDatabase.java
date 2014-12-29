package de.uni_stuttgart.riot.android.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database accessor for languages.
 */
public class LanguageDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERION = 1;
    private static final String DATABASE_NAME = "RIOT";
    private static final String TABLE_LANGUAGE = "language";

    private static final String KEY_ID = "id";
    private static final String KEY_DESC = "description";

    /**
     * Creates a new instance.
     * 
     * @param context
     *            The Android context.
     */
    public LanguageDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERION);
        // TODO Auto-generated constructor stub
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LANGUAGE + "(" + KEY_ID + " TEXT PRIMARY KEY," + KEY_DESC + " TEXT)");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older version of the table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);

        // Create table again
        onCreate(db);
    }

    /**
     * Adds a new language.
     * 
     * @param id
     *            The ID of the language.
     * @param description
     *            A description for the language.
     */
    public void addLanguage(String id, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_DESC, description);

        db.insert(TABLE_LANGUAGE, null, values);
        db.close();
    }

    /**
     * Gets the number of available languages.
     * 
     * @return The number of available languages.
     */
    public int getCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LANGUAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int i = cursor.getCount();
        cursor.close();

        // return count
        return i;
    }

    /**
     * Deletes all languages.
     */
    public void deleteAllLanguages() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LANGUAGE);
        db.close();
    }

    /**
     * Retrieves the first language.
     * 
     * @return The ID of the first language.
     */
    public String getLanguage() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LANGUAGE, // a. table
                null, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        String id = cursor.getString(0);
        return id;
    }
}
