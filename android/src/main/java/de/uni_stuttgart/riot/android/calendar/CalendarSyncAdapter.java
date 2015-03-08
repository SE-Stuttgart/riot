package de.uni_stuttgart.riot.android.calendar;

import java.io.IOException;
import java.util.HashSet;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import de.uni_stuttgart.riot.android.account.AuthConstants;
import de.uni_stuttgart.riot.android.communication.AndroidConnectionProvider;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.UnauthenticatedException;
import de.uni_stuttgart.riot.clientlibrary.client.CalendarClient;
import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;

/**
 * SyncAdapter for periodical syncs of the calendar data.
 */
public class CalendarSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "CalendarSyncAdapter";

    /**
     * Constructor.
     *
     * @param context
     *            the android context
     */
    public CalendarSyncAdapter(Context context) {
        super(context, true);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Performing sync for authority " + authority);
        CalendarClient client = new CalendarClient(AndroidConnectionProvider.getConnector(getContext()));

        Calendar cal = null;
        // For each calendar, display all the events from the previous week to
        // the end of next week.
        for (Long calendarId : getCalendarIds(provider)) {
            try {
                Log.d(TAG, "Get all events from calendar " + calendarId);

                Cursor eventCursor = getEvents(account.name, provider, calendarId);

                Log.v("SyncAdapter", "eventCursor count=" + eventCursor.getCount());
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    AndroidCalendarEventEntry evt = new AndroidCalendarEventEntry(eventCursor);

                    // getId returns the id of the server, not of the local database
                    if ((evt.getId() == 0) && evt.isDirty()) {
                        Log.v(TAG, "Create on Server");
                        createEventOnServer(client, provider, evt, account.name);
                    } else if ((evt.getId() > 0) && evt.isDirty()) {
                        Log.v(TAG, "Update on Server");
                        updateEventOnServer(client, provider, evt, account.name);
                    } else if ((evt.getId() > 0) && evt.isDeleted()) {
                        Log.v(TAG, "Delete on Server");
                        deleteEventOnServer(client, provider, evt, account.name);
                    } else {
                        Log.v(TAG, "Nothing to update");
                    }
                }

                // For each entry on the server check if a local one exists

                for (CalendarEntry entry : client.getEvents()) {
                    // search for id in local calendar database
                    if (!checkIfEventExists(provider, calendarId, entry.getId(), account.name)) {
                        if (cal == null) {
                            cal = new Calendar(account, provider, calendarId);
                        }
                        // create this event
                        AndroidCalendarEventEntry event = new AndroidCalendarEventEntry(entry);
                        cal.addEvent(event);
                    }
                }
            } catch (UnauthenticatedException e) {
                Log.e(TAG, "RequestException", e);
                syncResult.stats.numAuthExceptions++;
            } catch (RequestException e) {
                Log.e(TAG, "RequestException", e);
                syncResult.stats.numParseExceptions++;
            } catch (IOException e) {
                Log.e(TAG, "RequestException", e);
                syncResult.stats.numIoExceptions++;
            }
        }
    }

    /**
     * createEventOnServer.
     * 
     * @param client
     *            The CalendarClient to communicate with the server.
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     * @param accountName
     *            The name of the account.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws IOException
     *             When a network error occured.
     */
    public void createEventOnServer(CalendarClient client, ContentProviderClient contentResolver, AndroidCalendarEventEntry entry, String accountName) throws IOException, RequestException {
        CalendarEntry ret = client.createEvent(entry);
        if (ret.getId() > 0) {
            entry.setDirty(false);
            updateEventSyncID(contentResolver, entry, accountName);
        }
    }

    /**
     * updateEventOnServer.
     * 
     * @param client
     *            The CalendarClient to communicate with the server.
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     * @param accountName
     *            The name of the account.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws IOException
     *             When a network error occured.
     */
    public void updateEventOnServer(CalendarClient client, ContentProviderClient contentResolver, AndroidCalendarEventEntry entry, String accountName) throws RequestException, IOException {
        CalendarEntry ret = client.updateEvent(entry);
        if (ret.getId() > 0) {
            entry.setDirty(false);
            updateEventSyncID(contentResolver, entry, accountName);
        }
    }

    /**
     * deleteEventOnServer.
     * 
     * @param client
     *            The CalendarClient to communicate with the server.
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     * @param accountName
     *            The name of the account.
     * @throws RequestException
     *             When the request could not be executed.
     * @throws IOException
     *             When a network error occured.
     */
    public void deleteEventOnServer(CalendarClient client, ContentProviderClient contentResolver, AndroidCalendarEventEntry entry, String accountName) throws RequestException, IOException {
        client.deleteEvent(entry);
        entry.setDirty(false);
        updateEventSyncID(contentResolver, entry, accountName);
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     * @return
     */
    HashSet<Long> getCalendarIds(ContentProviderClient contentResolver) {
        // Fetch a list of all calendars synced with the device, their display
        // names and whether the
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Calendars.CONTENT_URI, (new String[] { BaseColumns._ID, Calendars.NAME, Calendars.ACCOUNT_TYPE, Calendars.ACCOUNT_NAME }), null, null, null);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        HashSet<Long> calendarIds = new HashSet<Long>();
        try {
            Log.v("SyncAdapter", "Found #" + cursor.getCount() + " calendars");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String cId = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    String accType = cursor.getString(2);
                    String accName = cursor.getString(3);

                    Log.v("SyncAdapter", "Id: " + cId + " Display Name: " + displayName + " Acc: " + accType + " - " + accName);
                    calendarIds.add(Long.valueOf(cId));
                }
            }
        } catch (AssertionError e) {
            throw new RuntimeException(e);
        }
        cursor.close();
        return calendarIds;
    }

    /**
     * Reads the calendar events from the database.
     * 
     * @param contentResolver
     *            the content resolver for data acces
     * @param calendarId
     *            the id of the calender of which the events are read
     * @return A cursor to the events.
     */
    private Cursor getEvents(String accountName, ContentProviderClient contentResolver, long calendarId) {
        Uri url = CalendarContract.Events.CONTENT_URI.buildUpon() //
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true") //
                .appendQueryParameter(Calendars.ACCOUNT_NAME, accountName) //
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, AuthConstants.ACCOUNT_TYPE) //
                .build();

        Cursor eventCursor = null;
        try {
            eventCursor = contentResolver.query(url, new String[] { BaseColumns._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.ALL_DAY, CalendarContract.Events.DIRTY, CalendarContract.Events._SYNC_ID, CalendarContract.Events.DELETED }, // projection
                    CalendarContract.Events.CALENDAR_ID + " = ?", // selection
                    new String[] { Long.toString(calendarId) }, // selection
                    // args
                    CalendarContract.Events.DTSTART + " ASC");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return eventCursor;
    }

    private boolean checkIfEventExists(ContentProviderClient contentResolver, long calendarId, long syncId, String accountName) {
        Uri url = CalendarContract.Events.CONTENT_URI.buildUpon() //
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true") //
                .appendQueryParameter(Calendars.ACCOUNT_NAME, accountName) //
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, AuthConstants.ACCOUNT_TYPE) //
                .build();
        try {
            Cursor eventCursor = contentResolver.query(url, new String[] { BaseColumns._ID, CalendarContract.Events.DELETED }, // projection
                    CalendarContract.Events.CALENDAR_ID + " = ?" + CalendarContract.Events._SYNC_ID + " = ?", // selection
                    new String[] { Long.toString(calendarId), Long.toString(syncId) }, // selection args
                    null);
            return eventCursor.getCount() > 0;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     * @param accountName
     *            The name of the account.
     * @return
     */
    private boolean updateEventSyncID(ContentProviderClient contentResolver, AndroidCalendarEventEntry entry, String accountName) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events._SYNC_ID, entry.getId());
        cv.put(CalendarContract.Events.DIRTY, entry.isDirty());
        String[] cs = new String[] { Long.toString(entry.getAndroidId()) };

        Uri calendarsURI = CalendarContract.Events.CONTENT_URI.buildUpon() //
                .appendQueryParameter(Calendars.ACCOUNT_NAME, accountName) //
                .appendQueryParameter(Calendars.ACCOUNT_TYPE, AuthConstants.ACCOUNT_TYPE) //
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true") //
                .build();
        try {
            int ret = contentResolver.update(calendarsURI, cv, BaseColumns._ID + " = ? ", cs);
            Log.v(TAG, "Event id(" + entry.getAndroidId() + ") updated sid:" + entry.getId() + " done:" + ret);
            return ret >= 1;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

}
