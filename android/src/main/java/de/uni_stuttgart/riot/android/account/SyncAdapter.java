package de.uni_stuttgart.riot.android.account;

import java.util.HashSet;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import de.enpro.android.riot.R;
import de.uni_stuttgart.riot.android.calendar.AndroidCalendarEventEntry;
import de.uni_stuttgart.riot.android.communication.RIOTApiClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.CalendarClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;

//CHECKSTYLE:OFF

/**
 * SyncAdapter for periodical syncs of the calendar data
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = "SyncAdapter";

    CalendarClient mCalendarClient;
    AndroidUser mAndroidUser;

    /**
     * Constructor
     *
     * @param context
     *            the android context
     */
    public SyncAdapter(Context context) {
        super(context, true);
        Log.v(TAG, "SyncAdapter created");
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     */
    public void CreateEventOnServer(ContentProviderClient contentResolver, AndroidCalendarEventEntry entry) {
        try {
            CalendarEntry ret = mCalendarClient.createEvent(entry);
            if (ret.getId() > 0) {
                entry.setDirty(false);
                updateEventSyncID(contentResolver, entry);
            }
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     */
    public void UpdateEventOnServer(ContentProviderClient contentResolver, AndroidCalendarEventEntry entry) {
        try {
            CalendarEntry ret = mCalendarClient.updateEvent(entry);
            if (ret.getId() > 0) {
                entry.setDirty(false);
                updateEventSyncID(contentResolver, entry);
            }
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     */
    public void DeleteEventOnServer(ContentProviderClient contentResolver, AndroidCalendarEventEntry entry) {
        try {
            if (mCalendarClient.deleteEvent(entry)) {
                entry.setDirty(false);
                updateEventSyncID(contentResolver, entry);
            }
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is needed cause the task is not allowd form the main thread
     *
     */
    public class CalendarSyncTask extends AsyncTask<Void, Integer, Long> {

        Account account;
        ContentProviderClient providerClient;

        CalendarSyncTask(Account account, ContentProviderClient providerClient)
        {
            this.account = account;
            this.providerClient = providerClient;
        }

        @Override
        protected Long doInBackground(Void ... voids) {
            mCalendarClient = new CalendarClient(RIOTApiClient.getInstance().getLoginClient());
            mAndroidUser = new AndroidUser(getContext(), account.name);
            syncCalendar(providerClient);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.content.AbstractThreadedSyncAdapter#onPerformSync(android.accounts.Account, android.os.Bundle, java.lang.String,
     * android.content.ContentProviderClient, android.content.SyncResult)
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Performing sync for authority " + authority);
        new CalendarSyncTask(account, provider).execute();
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
            e.printStackTrace();
        }

        HashSet<Long> calendarIds = new HashSet<Long>();
        try {
            System.out.println("Found #" + cursor.getCount() + " calendars");
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String _id = cursor.getString(0);
                    String displayName = cursor.getString(1);
                    String acc_type = cursor.getString(2);
                    String acc_name = cursor.getString(3);

                    System.out.println("Id: " + _id + " Display Name: " + displayName + " Acc: " + acc_type + " - " + acc_name);
                    calendarIds.add(Long.valueOf(_id));
                }
            }
        } catch (AssertionError ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return calendarIds;
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     * @param calendarId
     *            the id of the calender of which the events are read
     * @return
     */
    private Cursor getEvents(ContentProviderClient contentResolver, long calendarId) {
        Account account = mAndroidUser.getAccount();
        Uri url = CalendarContract.Events.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").appendQueryParameter(Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(Calendars.ACCOUNT_TYPE, getContext().getString(R.string.ACCOUNT_TYPE)).build();

        Cursor eventCursor = null;
        try {
            eventCursor = contentResolver.query(url, new String[] { BaseColumns._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Events.ALL_DAY, CalendarContract.Events.DIRTY, CalendarContract.Events._SYNC_ID, CalendarContract.Events.DELETED }, // projection
                    CalendarContract.Events.CALENDAR_ID + " = ?", // selection
                    new String[] { Long.toString(calendarId) }, // selection
                    // args
                    CalendarContract.Events.DTSTART + " ASC");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return eventCursor;
    }

    private boolean checkIfEventExists(ContentProviderClient contentResolver, long calendarId, long sync_id) {
        Account account = mAndroidUser.getAccount();
        Uri url = CalendarContract.Events.CONTENT_URI.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").appendQueryParameter(Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(Calendars.ACCOUNT_TYPE, getContext().getString(R.string.ACCOUNT_TYPE)).build();
        try {
            Cursor eventCursor = contentResolver.query(url, new String[] { BaseColumns._ID, CalendarContract.Events.DELETED }, // projection
                    CalendarContract.Events.CALENDAR_ID + " = ?" + CalendarContract.Events._SYNC_ID + " = ?", // selection
                    new String[] { Long.toString(calendarId), Long.toString(sync_id) }, // selection args
                    null);
            return eventCursor.getCount() > 0;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     * @param entry
     *            the android calender entry which is transmited
     * @return
     */
    private boolean updateEventSyncID(ContentProviderClient contentResolver, AndroidCalendarEventEntry entry) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events._SYNC_ID, entry.getId());
        cv.put(CalendarContract.Events.DIRTY, entry.isDirty());
        String cs[] = new String[] { Long.toString(entry.getAndroid_id()) };

        Account account = mAndroidUser.getAccount();
        Uri calendarsURI = CalendarContract.Events.CONTENT_URI.buildUpon().appendQueryParameter(Calendars.ACCOUNT_NAME, account.name).appendQueryParameter(Calendars.ACCOUNT_TYPE, getContext().getString(R.string.ACCOUNT_TYPE)).appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();
        try {
            int ret = contentResolver.update(calendarsURI, cv, BaseColumns._ID + " = ? ", cs);
            Log.v(TAG, "Event id(" + entry.getAndroid_id() + ") updated sid:" + entry.getId() + " done:" + ret);
            return ret >= 1;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param contentResolver
     *            the content resolver for data acces
     */
    public void syncCalendar(ContentProviderClient contentResolver) {
        Calendar cal = null;
        // For each calendar, display all the events from the previous week to
        // the end of next week.
        for (Long calendar_id : getCalendarIds(contentResolver)) {
            Log.d(TAG, "Get all events from calendar " + calendar_id);

            Cursor eventCursor = getEvents(contentResolver, calendar_id);

            System.out.println("eventCursor count=" + eventCursor.getCount());
            for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                AndroidCalendarEventEntry evt = new AndroidCalendarEventEntry(eventCursor);

                // getId returns the id of the server, not of the local database
                if ((evt.getId() == 0) && evt.isDirty()) {
                    Log.v(TAG, "Create on Server");
                    CreateEventOnServer(contentResolver, evt);
                } else if ((evt.getId() > 0) && evt.isDirty()) {
                    Log.v(TAG, "Update on Server");
                    UpdateEventOnServer(contentResolver, evt);
                } else if ((evt.getId() > 0) && evt.isDeleted()) {
                    Log.v(TAG, "Delete on Server");
                    DeleteEventOnServer(contentResolver, evt);
                } else {
                    Log.v(TAG, "Nothing to update");
                }
            }

            // For each entry on the server check if a local one exists
            try {
                for (CalendarEntry entry : mCalendarClient.getEvents()) {
                    // search for id in local calendar database
                    if (!checkIfEventExists(contentResolver, calendar_id, entry.getId())) {
                        if (cal == null) {
                            Account account = mAndroidUser.getAccount();
                            cal = new Calendar(account, contentResolver, calendar_id);
                        }
                        // create this event
                        AndroidCalendarEventEntry event = new AndroidCalendarEventEntry(entry);
                        cal.addEvent(event);
                    }
                }
            } catch (RequestException e) {
                e.printStackTrace();
            }
        }
    }

}
