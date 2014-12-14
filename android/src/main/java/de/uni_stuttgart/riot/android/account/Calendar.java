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

/**
 * Created by dirkmb on 12/7/14.
 */
public class Calendar {
	private final static String TAG = "Calendar";
	private long calendarId;
	private Account account;
	private ContentProviderClient client;

	public Calendar(Account account, ContentProviderClient client,
			String calendarName) {
		this.account = account;
		this.client = client;
		calendarId = GetCalendar(calendarName);
		if (calendarId == -1) {
			AddCalendar(calendarName, calendarName);
		}
	}

	private long GetCalendar(String calendarName) {
		Cursor cur = null;
		Uri calendarsURI = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account.name)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, account.type)
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true").build();

		try {
			cur = client.query(calendarsURI,
					new String[] { CalendarContract.Calendars._ID }, null,
					null, null);
			if (cur != null && cur.moveToFirst()) {
				String my_calendar_id = cur.getString(0);
				Log.v(TAG, "Got Calendar id: " + my_calendar_id);
				return Long.parseLong(my_calendar_id.trim());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		Log.v(TAG, "Nothing found for: " + calendarsURI);
		return -1;
	}

	private long AddCalendar(String calendarName, String calendarTitle) {
		ContentValues values = new ContentValues();
		values.put(CalendarContract.Calendars.ACCOUNT_NAME, account.name);
		values.put(CalendarContract.Calendars.ACCOUNT_TYPE, account.type);
		values.put(CalendarContract.Calendars.NAME, calendarName);
		values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarTitle);
		values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0xFFC3EA6E);
		values.put(CalendarContract.Calendars.OWNER_ACCOUNT, account.name);
		values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
		values.put(CalendarContract.Calendars.VISIBLE, 1);
		// values.put(Calendars.ALLOWED_REMINDERS, Reminders.METHOD_ALERT);

		values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
		values.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1);
		values.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1);
		// }

		/*
		 * if (android.os.Build.VERSION.SDK_INT >= 15) {
		 * values.put(Calendars.ALLOWED_AVAILABILITY, Events.AVAILABILITY_BUSY +
		 * "," + Events.AVAILABILITY_FREE + "," +
		 * Events.AVAILABILITY_TENTATIVE);
		 * values.put(Calendars.ALLOWED_ATTENDEE_TYPES, Attendees.TYPE_NONE +
		 * "," + Attendees.TYPE_OPTIONAL + "," + Attendees.TYPE_REQUIRED + "," +
		 * Attendees.TYPE_RESOURCE); }
		 */

		// if (info.getTimezone() != null)
		// values.put(Calendars.CALENDAR_TIME_ZONE, info.getTimezone());

		Uri calendarsURI = CalendarContract.Calendars.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME,
						account.name)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
						account.type)
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
						"true").build();

		Log.i(TAG, "Inserting calendar: " + values.toString() + " -> "
				+ calendarsURI.toString());
		Uri uri = null;
		try {
			uri = client.insert(calendarsURI, values);
			if (uri != null) {
				long id = ContentUris.parseId(uri);
				Log.d(TAG, "Created test Calendar with ID " + id);
				return id;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return -1;
	}


    public long AddEvent(String eventName)
    {
        return AddEvent(eventName, java.util.Calendar.getInstance().getTimeInMillis());
    }

    public long AddEvent(String eventName, long dtstart)
    {
        // default end is 2min after start
        return AddEvent(eventName, dtstart, java.util.Calendar.getInstance().getTimeInMillis()+120000);
    }

	public long AddEvent(String eventName, long dtstart, long dtend) {
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

		Uri calendarsURI = CalendarContract.Events.CONTENT_URI
				.buildUpon()
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME,
						account.name)
				.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
						account.type)
				.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER,
						"true").build();
		try {
			Uri uri = null;
			uri = client.insert(calendarsURI, cv);
			Log.v(TAG, "Event added");
			long id = ContentUris.parseId(uri);
			Log.d(TAG, "Created test Event[" + calendarId + "] with ID " + id);
			return id;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
