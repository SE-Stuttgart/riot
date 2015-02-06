package de.uni_stuttgart.riot.android.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ListView;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.FilterItem;
import de.uni_stuttgart.riot.android.NotificationScreen;
import de.uni_stuttgart.riot.android.location.MyLocation;
import de.uni_stuttgart.riot.android.notification.Notification;
import de.uni_stuttgart.riot.android.notification.NotificationAdapter;
import de.uni_stuttgart.riot.android.notification.NotificationType;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class RIOTDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERION = 1;
    private static final String DATABASE_NAME = "Database";

    private static final String TABLE_FILTER = "filter";
    private static final String FILTER_COLUMN_ID = "id";
    private static final String FILTER_COLUMN_TYPE = "type";
    private static final String FILTER_COLUMN_CHECKED = "is_checked";

    private static final String TABLE_NOTIFICATION = "notification";
    private static final String NOTIFICATION_COLUMN_TITLE = "title";
    private static final String NOTIFICATION_COLUMN_TYPE = "type";
    private static final String NOTIFICATION_COLUMN_ID = "id";
    private static final String NOTIFICATION_COLUMN_CONTENT = "content";
    private static final String NOTIFICATION_COLUMN_DATE = "date";
    private static final String NOTIFICATION_COLUMN_THING = "thing";

    private static final String TABLE_LANGUAGE = "language";
    private static final String LANGUAGE_COLUMN_ID = "id";

    private static final String TABLE_LOCATION = "location";
    private static final String LOCATION_COLUMN_ID = "id";
    private static final String LOCATION_COLUMN_DESC = "description";
    private static final String LOCATION_COLUMN_LONGITUDE = "longitude";
    private static final String LOCATION_COLUMN_LATITUDE = "latitude";

    private String valueFromIntent;
    private NotificationScreen notificationScreen;

    public RIOTDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERION);
    }

    public RIOTDatabase(NotificationScreen notificationScreen, String valueFromIntent) {
        super(notificationScreen, DATABASE_NAME, null, DATABASE_VERION);

        // Sets the application context
        this.notificationScreen = notificationScreen;

        // Sets the value from intent
        this.valueFromIntent = valueFromIntent;
    }

    /**
     * Creating the tables.
     */
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FILTER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER + "(" + FILTER_COLUMN_ID + " INTEGER PRIMARY KEY," + FILTER_COLUMN_TYPE + " TEXT," + FILTER_COLUMN_CHECKED + " INTEGER)";

        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + "(" + NOTIFICATION_COLUMN_ID + " INTEGER PRIMARY KEY," + NOTIFICATION_COLUMN_TYPE + " TEXT," + NOTIFICATION_COLUMN_TITLE + " TEXT," + NOTIFICATION_COLUMN_CONTENT + " TEXT, " + NOTIFICATION_COLUMN_DATE + " TEXT, " + NOTIFICATION_COLUMN_THING + " TEXT)";

        String CREATE_LANGUAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LANGUAGE + "(" + LANGUAGE_COLUMN_ID + " TEXT PRIMARY KEY)";

        String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + "(" + LOCATION_COLUMN_ID + " TEXT PRIMARY KEY," + LOCATION_COLUMN_DESC + " TEXT," + LOCATION_COLUMN_LATITUDE + " TEXT," + LOCATION_COLUMN_LONGITUDE + " TEXT )";

        db.execSQL(CREATE_FILTER_TABLE);
        db.execSQL(CREATE_NOTIFICATION_TABLE);
        db.execSQL(CREATE_LANGUAGE_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older version of the table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);

        // Create table again
        onCreate(db);
    }

    /**
     * 
     * @param id
     * @param type
     * @param isChecked
     * @return
     */
    public void updateFilterSetting(FilterItem filter) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILTER_COLUMN_ID, filter.getId());
        values.put(FILTER_COLUMN_TYPE, filter.getType().toString());
        values.put(FILTER_COLUMN_CHECKED, filter.getItem().isChecked());

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FILTER + " WHERE " + FILTER_COLUMN_ID + " = ?", new String[] { String.valueOf(filter.getId()) });

        if (cursor.moveToFirst()) {
            System.out.println("Filter Eintrag update");
            db.update(TABLE_FILTER, // table
                    values, // column/value
                    FILTER_COLUMN_ID + " = ?", // selections
                    new String[] { String.valueOf(filter.getId()) });
        } else {
            System.out.println("Filter Eintrag neu anlegen");
            db.insert(TABLE_FILTER, null, values);
        }

        filterNotifications();

        cursor.close();
        db.close();
    }

    public void filterNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        String filteredNotifications = "SELECT * FROM " + TABLE_NOTIFICATION + " INNER JOIN " + TABLE_FILTER + " ON " + TABLE_NOTIFICATION + "." + NOTIFICATION_COLUMN_TYPE + " == " + TABLE_FILTER + "." + FILTER_COLUMN_TYPE + " AND " + TABLE_FILTER + "." + FILTER_COLUMN_CHECKED + "== 1";

        Cursor cursor = db.rawQuery(filteredNotifications, null);
        List<Notification> notificationList = new ArrayList<Notification>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(NOTIFICATION_COLUMN_ID));

                String title = cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_TITLE));

                String content = cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_CONTENT));

                String type = cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_TYPE));

                String date = cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_DATE));

                String thing = cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_THING));

                if (valueFromIntent.equals(thing)) {
                    // TODO: richtiges Datum verwenden
                    Notification notificiation = new Notification(id, title, content, NotificationType.valueOf(type), new SimpleDateFormat("K:mm a, E d.MMM,yyyy").format(new Date()), thing);
                    notificationList.add(notificiation);
                }

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        NotificationAdapter chapterListAdapter = new NotificationAdapter(notificationScreen, notificationList);
        ListView notification = (ListView) notificationScreen.findViewById(R.id.NotificationList);
        notification.setAdapter(chapterListAdapter);
    }

    /**
     * 
     * @param type
     * @return
     */
    public boolean getFilterSettings(NotificationType type) {

        boolean isChecked = false;
        int i = 0;

        String query = "SELECT * FROM " + TABLE_FILTER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(FILTER_COLUMN_TYPE)).equals(type.toString())) {
                    i = cursor.getInt(cursor.getColumnIndex(FILTER_COLUMN_CHECKED));
                }
            } while (cursor.moveToNext());
        }

        // 1 = checked, 0 = unchecked
        if (i == 1) {
            isChecked = true;
        } else {
            isChecked = false;
        }

        cursor.close();
        db.close();

        return isChecked;
    }

    /**
     * We set the filter settings into the database.
     * 
     * @param filter
     */
    public void setFilter(FilterItem filter) {

        if (filter.getItem().isChecked() == false) {
            filter.getItem().setChecked(true);

            updateFilterSetting(filter);

        } else {
            filter.getItem().setChecked(false);

            updateFilterSetting(filter);
        }
    }

    /**
     * 
     * @param notificationList
     */

    public void updateNotificationEntries(List<Notification> notificationList) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (Notification notification : notificationList) {
            ContentValues values = new ContentValues();
            values.put(NOTIFICATION_COLUMN_ID, notification.getId());
            values.put(NOTIFICATION_COLUMN_TITLE, notification.getTitle());
            values.put(NOTIFICATION_COLUMN_TYPE, notification.getType().toString());
            values.put(NOTIFICATION_COLUMN_CONTENT, notification.getContent());
            values.put(NOTIFICATION_COLUMN_DATE, notification.getDate());
            values.put(NOTIFICATION_COLUMN_THING, notification.getThingName());

            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATION + " WHERE " + NOTIFICATION_COLUMN_ID + " = ?", new String[] { String.valueOf(notification.getId()) });

            if (cursor.moveToFirst()) {
                System.out.println("Notification Eintrag update");
                db.update(TABLE_NOTIFICATION, // table
                        values, // column/value
                        NOTIFICATION_COLUMN_ID + " = ?", // selections
                        new String[] { String.valueOf(notification.getId()) });
            } else {
                System.out.println("Notification Eintrag neu anlegen");
                db.insert(TABLE_NOTIFICATION, null, values);
            }

            cursor.close();
        }

        db.close();
    }

    /**
     * Add a new language into the table language.
     * 
     * @param id
     *            String like "en" or "de"
     * @param description
     *            set the description
     */
    public void setLanguage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_LANGUAGE);

        ContentValues values = new ContentValues();
        values.put(LANGUAGE_COLUMN_ID, id);

        db.insert(TABLE_LANGUAGE, null, values);
        db.close();
    }

    /**
     * 
     * Get the total count of all entries which are stored in in the language table.
     * 
     * @return the number of records which are stored in the language table
     */
    public int getLanguageCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LANGUAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int i = cursor.getCount();
        cursor.close();

        // return count
        return i;
    }

    /**
     * Returns the actual language.
     * 
     * @return the chosen language as a String
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

    public void updateLocation(MyLocation location) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOCATION_COLUMN_ID, location.getPlace());
        values.put(LOCATION_COLUMN_DESC, location.getAddress());
        values.put(LOCATION_COLUMN_LATITUDE, location.getLatitude());
        values.put(LOCATION_COLUMN_LONGITUDE, location.getLongitude());

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LOCATION + " WHERE " + LOCATION_COLUMN_ID + " = ?", new String[] { location.getPlace() });

        if (cursor.moveToFirst()) {
            db.update(TABLE_LOCATION, // table
                    values, // column/value
                    LOCATION_COLUMN_ID + " = ?", // selections
                    new String[] { location.getPlace() });
        } else {
            db.insert(TABLE_LOCATION, null, values);
        }

        cursor.close();
        db.close();
    }

    public List<MyLocation> getLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<MyLocation> myLocation = new LinkedList<MyLocation>();

        Cursor cursor = db.query(TABLE_LOCATION, new String[] { LOCATION_COLUMN_ID, LOCATION_COLUMN_DESC, LOCATION_COLUMN_LATITUDE, LOCATION_COLUMN_LONGITUDE }, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String place = cursor.getString(0);
            String address = cursor.getString(1);
            double latitude = Double.valueOf(cursor.getString(2));
            double longitude = Double.valueOf(cursor.getString(3));

            myLocation.add(new MyLocation(place, address, latitude, longitude));
        }
        cursor.close();
        db.close();

        return myLocation;
    }

    public int getLocationCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int i = cursor.getCount();
        cursor.close();

        // return count
        return i;
    }
}
