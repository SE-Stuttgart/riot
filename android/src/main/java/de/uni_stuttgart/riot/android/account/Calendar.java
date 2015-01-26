package de.uni_stuttgart.riot.android.account;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.CalendarContract;
import android.util.Log;

//CHECKSTYLE:OFF FIXME Please fix the checkstyle errors in this file and remove this comment.
import de.uni_stuttgart.riot.android.calendar.AndroidCalendarEventEntry;

/**
 * Created by dirkmb on 12/7/14.
 */
public class Calendar {
    private static final String TAG = "Calendar";
    private long calendarId;
    private Account account;
    private ContentProviderClient client;

    /**
     * Creates a new Calendar.
     *
     * @param account
     *            The account.
     * @param client
     *            The content provider for the database querys
     * @param calendarId
     *            The id of the calendar.
     */
    public Calendar(Account account, ContentProviderClient client, long calendarId) {
        this.account = account;
        this.client = client;
        this.calendarId = calendarId;
    }

    /**
     * Creates a new Calendar.
     *
     * @param account
     *            The account.
     * @param client
     *            The content provider for the database querys
     * @param calendarName
     *            The name of the calendar.
     */
    public Calendar(Account account, ContentProviderClient client, String calendarName) {
        this.account = account;
        this.client = client;
        calendarId = getCalendar(calendarName);
        if (calendarId == -1) {
            addCalendar(calendarName, calendarName);
        }
    }

    private long getCalendar(String calendarName) {
        Cursor cur = null;
        Uri calendarsURI = CalendarContract.Calendars.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type).appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();

        try {
            cur = client.query(calendarsURI, new String[] { CalendarContract.Calendars._ID }, null, null, null);
            if (cur != null && cur.moveToFirst()) {
                String myCalendarID = cur.getString(0);
                Log.v(TAG, "Got Calendar id: " + myCalendarID);
                return Long.parseLong(myCalendarID.trim());
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        Log.v(TAG, "Nothing found for: " + calendarsURI);
        return -1;
    }

    public void changeColor(int color) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, color & 0xFFFFFFFF);
        Uri calendarsURI = CalendarContract.Calendars.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type).appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();
        try {
            client.update(calendarsURI, values, "", null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private long addCalendar(String calendarName, String calendarTitle) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, account.name);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, account.type);
        values.put(CalendarContract.Calendars.NAME, calendarName);
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarTitle);
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xFFC3EA6E); // NOCS FIXME
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, account.name);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        // values.put(Calendars.ALLOWED_REMINDERS, Reminders.METHOD_ALERT);

        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1);
        values.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1);

        Uri calendarsURI = CalendarContract.Calendars.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type).appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();

        Log.i(TAG, "Inserting calendar: " + values.toString() + " -> " + calendarsURI.toString());
        Uri uri = null;
        try {
            uri = client.insert(calendarsURI, values);
            if (uri != null) {
                long id = ContentUris.parseId(uri);
                Log.d(TAG, "Created test Calendar with ID " + id);
                return id;
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    /**
     * Adds an event with the given name.
     *
     * @param eventName
     *            The name of the event.
     * @return The ID of the created event.
     */
    public long addEvent(String eventName) {
        return addEvent(eventName, java.util.Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Creates a new event with a default duration of 2 minutes.
     *
     * @param eventName
     *            The name of the event.
     * @param dtstart
     *            The start time of the event.
     * @return The ID of the created event.
     */
    public long addEvent(String eventName, long dtstart) {
        // default end is 2min after start
        return addEvent(eventName, dtstart, java.util.Calendar.getInstance().getTimeInMillis() + 120000);
    }

    /**
     * Creates a new event.
     *
     * @param eventName
     *            The name of the event.
     * @param dtstart
     *            The start time of the event.
     * @param dtend
     *            The end time of the event.
     * @return The ID of the created event.
     */
    public long addEvent(String eventName, long dtstart, long dtend) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        cv.put(CalendarContract.Events.TITLE, eventName);
        cv.put(CalendarContract.Events.ALL_DAY, 0);
        cv.put(CalendarContract.Events.DTSTART, dtstart);
        cv.put(CalendarContract.Events.DTEND, dtend);
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");
        cv.put(CalendarContract.Events.DIRTY, 1);

        // cv.put(CalendarContract.Events.EVENT_LOCATION, "RIOT-LOCATION");
        // cv.put(CalendarContract.Events.DESCRIPTION, "RIOT-DESCRIPTION");

        Uri calendarsURI = CalendarContract.Events.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type).appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();
        try {
            Uri uri = null;
            uri = client.insert(calendarsURI, cv);
            Log.v(TAG, "Event added");
            long id = ContentUris.parseId(uri);
            Log.d(TAG, "Created test Event[" + calendarId + "] with ID " + id);
            return id;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public long addEvent(AndroidCalendarEventEntry entry) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        cv.put(CalendarContract.Events.TITLE, entry.getTitle());
        cv.put(CalendarContract.Events.ALL_DAY, entry.isAllDayEvent());
        cv.put(CalendarContract.Events.DTSTART, entry.getStartTime().getTime());
        cv.put(CalendarContract.Events.DTEND, entry.getEndTime().getTime());
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");
        cv.put(CalendarContract.Events.EVENT_LOCATION, entry.getLocation());
        cv.put(CalendarContract.Events.DESCRIPTION, entry.getDescription());

        cv.put(CalendarContract.Events._SYNC_ID, entry.getId());
        cv.put(CalendarContract.Events.DIRTY, entry.isDirty());

        Uri calendarsURI = CalendarContract.Events.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type).appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();
        try {
            Uri uri = client.insert(calendarsURI, cv);
            Log.v(TAG, "Event added");
            long id = ContentUris.parseId(uri);
            Log.d(TAG, "Created test Event[" + calendarId + "] with ID " + id);
            return id;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
