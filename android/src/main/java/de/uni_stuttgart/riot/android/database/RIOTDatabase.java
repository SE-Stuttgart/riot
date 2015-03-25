package de.uni_stuttgart.riot.android.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.uni_stuttgart.riot.android.FilterItem;
import de.uni_stuttgart.riot.android.HomeScreen;
import de.uni_stuttgart.riot.android.HomeScreenButton;
import de.uni_stuttgart.riot.android.HomeScreenCanvas;
import de.uni_stuttgart.riot.android.location.MyLocation;
import de.uni_stuttgart.riot.notification.Notification;
import de.uni_stuttgart.riot.notification.NotificationSeverity;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
public class RIOTDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERION = 1;
    private static final String DATABASE_NAME = "Database";

    private static final String TABLE_FILTER = "filter";
    private static final String FILTER_COLUMN_ID = "id";
    private static final String FILTER_COLUMN_SEVERITY = "severity";
    private static final String FILTER_COLUMN_CHECKED = "is_checked";

    private static final String TABLE_NOTIFICATION = "notification";
    private static final String NOTIFICATION_COLUMN_ID = "id";
    private static final String NOTIFICATION_COLUMN_THING = "thing";
    private static final String NOTIFICATION_COLUMN_SEVERITY = "severity";
    private static final String NOTIFICATION_COLUMN_NAME = "name";
    private static final String NOTIFICATION_COLUMN_TITLE = "title";
    private static final String NOTIFICATION_COLUMN_MESSAGE = "message";
    private static final String NOTIFICATION_COLUMN_TIME = "time";
    private static final String NOTIFICATION_COLUMN_DISMISSED = "dismissed";

    private static final String TABLE_LANGUAGE = "language";
    private static final String LANGUAGE_COLUMN_ID = "id";

    private static final String TABLE_LOCATION = "location";
    private static final String LOCATION_COLUMN_ID = "id";
    private static final String LOCATION_COLUMN_DESC = "description";
    private static final String LOCATION_COLUMN_LONGITUDE = "longitude";
    private static final String LOCATION_COLUMN_LATITUDE = "latitude";

    private static final String TABLE_COORDINATES = "coordinates";
    private static final String COORDINATES_COLUMN_ID = "id";
    private static final String COORDINATES_BUTTON_DESCRIPTION = "description";
    private static final String COORDINATES_COLUMN_XPOSITION = "x";
    private static final String COORDINATES_COLUMN_YPOSITION = "y";
    private static final String COORDINATES_IMAGEID = "imageid";

    public RIOTDatabase(HomeScreen homeScreen) {
        super(homeScreen, DATABASE_NAME, null, DATABASE_VERION);
    }

    /**
     * Creating the different tables.
     * 
     * @param db
     *            The database that is used to create the tables.
     */
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FILTER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FILTER + "(" + //
                FILTER_COLUMN_ID + " INTEGER PRIMARY KEY," + //
                FILTER_COLUMN_SEVERITY + " TEXT," + //
                FILTER_COLUMN_CHECKED + " INTEGER)";

        String CREATE_NOTIFICATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + "(" + //
                NOTIFICATION_COLUMN_ID + " INTEGER PRIMARY KEY," + //
                NOTIFICATION_COLUMN_THING + " INTEGER," + //
                NOTIFICATION_COLUMN_SEVERITY + " TEXT," + //
                NOTIFICATION_COLUMN_NAME + " TEXT," + //
                NOTIFICATION_COLUMN_TITLE + " TEXT," + //
                NOTIFICATION_COLUMN_MESSAGE + " TEXT, " + //
                NOTIFICATION_COLUMN_TIME + " INTEGER, " + //
                NOTIFICATION_COLUMN_DISMISSED + " INTEGER)";

        String CREATE_LANGUAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LANGUAGE + "(" + LANGUAGE_COLUMN_ID + " TEXT PRIMARY KEY)";

        String CREATE_LOCATION_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION + "(" + LOCATION_COLUMN_ID + " TEXT PRIMARY KEY," + LOCATION_COLUMN_DESC + " TEXT," + LOCATION_COLUMN_LATITUDE + " TEXT," + LOCATION_COLUMN_LONGITUDE + " TEXT )";

        String CREATE_COORDINATES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_COORDINATES + "(" + COORDINATES_COLUMN_ID + " INTEGER PRIMARY KEY," + COORDINATES_BUTTON_DESCRIPTION + " TEXT," + COORDINATES_COLUMN_XPOSITION + " INTEGER," + COORDINATES_COLUMN_YPOSITION + " INTEGER," + COORDINATES_IMAGEID + " INTEGER )";

        db.execSQL(CREATE_FILTER_TABLE);
        db.execSQL(CREATE_NOTIFICATION_TABLE);
        db.execSQL(CREATE_LANGUAGE_TABLE);
        db.execSQL(CREATE_LOCATION_TABLE);
        db.execSQL(CREATE_COORDINATES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older version of the table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COORDINATES);

        // Create table again
        onCreate(db);
    }

    /**
     * The different filters for the notifications are stored in the database and can be updated or created in this method.
     * 
     * @param filter
     *            Each filter object has three fields: id (the ID of the filter), type (name of the filter), isChecked (is the filter
     *            checked or unchecked).
     * 
     */
    public void updateFilters(FilterItem filter) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FILTER_COLUMN_ID, filter.getId());
        values.put(FILTER_COLUMN_SEVERITY, filter.getSeverity().name());
        values.put(FILTER_COLUMN_CHECKED, filter.getItem().isChecked());

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FILTER + " WHERE " + FILTER_COLUMN_ID + " = ?", new String[] { String.valueOf(filter.getId()) });

        if (cursor.moveToFirst()) {
            // System.out.println("Filter Eintrag update");
            db.update(TABLE_FILTER, // table
                    values, // column/value
                    FILTER_COLUMN_ID + " = ?", // selections
                    new String[] { String.valueOf(filter.getId()) });
        } else {
            // System.out.println("Filter Eintrag neu anlegen");
            db.insert(TABLE_FILTER, null, values);
        }

        cursor.close();
        db.close();
    }

    /**
     * The checked filters in the UI of the NotificationScreen are used to create a list of Notifications.
     */
    public Collection<Notification> getFilteredNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        String filteredNotifications = "SELECT * FROM " + TABLE_NOTIFICATION + " INNER JOIN " + TABLE_FILTER + " ON " + TABLE_NOTIFICATION + "." + NOTIFICATION_COLUMN_SEVERITY + " == " + TABLE_FILTER + "." + FILTER_COLUMN_SEVERITY + " AND " + TABLE_FILTER + "." + FILTER_COLUMN_CHECKED + "== 1";

        Cursor cursor = db.rawQuery(filteredNotifications, null);
        List<Notification> notificationList = new ArrayList<Notification>();

        if (cursor.moveToFirst()) {
            do {
                long thingIdZero = cursor.getLong(cursor.getColumnIndex(NOTIFICATION_COLUMN_THING));
                Long thingId = thingIdZero == 0 ? null : thingIdZero;

                Notification notification = new Notification();
                notification.setId(cursor.getLong(cursor.getColumnIndex(NOTIFICATION_COLUMN_ID)));
                notification.setThingID(thingId);
                notification.setSeverity(NotificationSeverity.valueOf(cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_SEVERITY))));
                notification.setName(cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_NAME)));
                notification.setResolvedTitle(cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_TITLE)));
                notification.setResolvedMessage(cursor.getString(cursor.getColumnIndex(NOTIFICATION_COLUMN_MESSAGE)));
                notification.setTime(new Date(cursor.getLong(cursor.getColumnIndex(NOTIFICATION_COLUMN_TIME))));
                notification.setDismissed(cursor.getInt(cursor.getColumnIndex(NOTIFICATION_COLUMN_DISMISSED)) == 1);
                notificationList.add(notification);

            } while (cursor.moveToNext());

            cursor.close();
            db.close();
        }

        return notificationList;
    }

    /**
     * To show the user, which filter is already checked or not, we have to get the checked status of each filter from the database.
     * 
     * @param severity
     *            The severity of the Filter we want to know the checked status
     * @return true = filter is checked, false = filter is unchecked
     */
    public boolean getFilterSettings(NotificationSeverity severity) {

        boolean isChecked = false;
        int i = 0;

        String query = "SELECT * FROM " + TABLE_FILTER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndex(FILTER_COLUMN_SEVERITY)).equals(severity.name())) {
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
     *            The filter that was checked or unchecked by the user
     */
    public void setFilter(FilterItem filter) {

        if (!filter.getItem().isChecked()) {
            filter.getItem().setChecked(true);

            updateFilters(filter);

        } else {
            filter.getItem().setChecked(false);

            updateFilters(filter);
        }
    }

    /**
     * Sets a new list of notifications, deleting all previous ones.
     * 
     * @param notificationList
     *            A collection of Notifications that is stored in the database.
     */

    public void setAllNotifications(Collection<Notification> notificationList) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.rawQuery("DELETE FROM " + TABLE_NOTIFICATION, new String[0]);

        for (Notification notification : notificationList) {
            if (notification.isDismissed()) {
                continue;
            }

            ContentValues values = new ContentValues();
            values.put(NOTIFICATION_COLUMN_ID, notification.getId());
            values.put(NOTIFICATION_COLUMN_THING, notification.getThingID());
            values.put(NOTIFICATION_COLUMN_SEVERITY, notification.getSeverity().name());
            values.put(NOTIFICATION_COLUMN_NAME, notification.getName());
            values.put(NOTIFICATION_COLUMN_TITLE, notification.getTitle());
            values.put(NOTIFICATION_COLUMN_MESSAGE, notification.getMessage());
            values.put(NOTIFICATION_COLUMN_TIME, notification.getTime().getTime());
            values.put(NOTIFICATION_COLUMN_DISMISSED, notification.isDismissed() ? 1 : 0);

            db.insert(TABLE_NOTIFICATION, null, values);
        }

        db.close();
    }

    /**
     * This methods stores the coordinates of each home screen button into the database. If the user restarts the app, the buttons are
     * replaced on their old positions.
     * 
     * @param button
     *            The pressed button on the home screen that opened its notification screen
     */
    public void updateHomeScreenButtonCoordinates(HomeScreenButton button) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;

        ContentValues values = new ContentValues();
        values.put(COORDINATES_COLUMN_ID, button.getId());
        values.put(COORDINATES_BUTTON_DESCRIPTION, button.getButtonDescription());
        values.put(COORDINATES_COLUMN_XPOSITION, button.getButtonX());
        values.put(COORDINATES_COLUMN_YPOSITION, button.getButtonY());
        values.put(COORDINATES_IMAGEID, button.getImageID());

        cursor = db.rawQuery("SELECT * FROM " + TABLE_COORDINATES + " WHERE " + COORDINATES_COLUMN_ID + " = ?", new String[] { String.valueOf(button.getId()) });

        if (cursor.moveToFirst()) {
            // System.out.println("Button Eintrag update");
            db.update(TABLE_COORDINATES, // table
                    values, // column/value
                    COORDINATES_COLUMN_ID + " = ?", // selections
                    new String[] { String.valueOf(button.getId()) });
        } else {
            // System.out.println("Button Eintrag neu anlegen");
            db.insert(TABLE_COORDINATES, null, values);
        }

        cursor.close();
        db.close();
    }

    /**
     * This method gets all HomeScreen buttons stored in the database.
     * 
     * @param canvas
     *            The canvas that is associated with every HomeScreenButton.
     * 
     * @return A list of all buttons on the HomeScreen
     */
    public List<HomeScreenButton> getHomeScreenButtonCoordinates(HomeScreenCanvas canvas) {
        SQLiteDatabase db = this.getWritableDatabase();
        String getButtonsQuery = "SELECT * FROM " + TABLE_COORDINATES;

        Cursor cursor = db.rawQuery(getButtonsQuery, null);
        List<HomeScreenButton> buttonList = new ArrayList<HomeScreenButton>();

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COORDINATES_COLUMN_ID));
                String description = cursor.getString(cursor.getColumnIndex(COORDINATES_BUTTON_DESCRIPTION));
                int xPosition = cursor.getInt(cursor.getColumnIndex(COORDINATES_COLUMN_XPOSITION));
                int yPosition = cursor.getInt(cursor.getColumnIndex(COORDINATES_COLUMN_YPOSITION));
                int imageID = cursor.getInt(cursor.getColumnIndex(COORDINATES_IMAGEID));

                HomeScreenButton button = new HomeScreenButton(canvas, id, description, xPosition, yPosition, imageID);
                buttonList.add(button);

            } while (cursor.moveToNext());

            cursor.close();
        }

        db.close();
        return buttonList;
    }

    /**
     * Add a new language into the table language.
     *
     * @param id
     *            String like "en" or "de"
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
        db.close();

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

        cursor.close();
        db.close();
        return id;
    }

    /**
     * Update the current location into the database.
     * 
     * @param location
     *            The current location of the user.
     */
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

    /**
     * Gets the last known location from the database.
     * 
     * @return A list with all locations.
     */
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

    /**
     * UNKNOWN.
     * 
     * @return UNKNOWN
     */
    public int getLocationCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOCATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int i = cursor.getCount();
        cursor.close();
        db.close();

        // return count
        return i;
    }

}
