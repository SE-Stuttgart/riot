package de.uni_stuttgart.riot.android.calendar;

import android.database.Cursor;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;

//CHECKSTYLE:OFF FIXME PLEASE FIX THE CHECKSTYLE ERRORS IN THIS FILE AND DONT COMMIT FILES THAN CONTAIN CHECKSTYLE ERRORS
/**
 * Created by dirkmb on 1/17/15.
 */
public class AndroidCalendarEventEntry extends CalendarEntry
{
    AndroidCalendarEventEntry()
    {}

    public AndroidCalendarEventEntry(CalendarEntry entry)
    {
        super(entry);
        dirty = false;
        deleted = false;
    }

    public AndroidCalendarEventEntry(Cursor eventCursor)
    {
        android_id = eventCursor.getLong(0);
        this.setTitle(eventCursor.getString(1));
        this.setStartTime(new java.util.Date(eventCursor.getLong(2)));
        this.setEndTime(new java.util.Date(eventCursor.getLong(3)));
        this.setDescription(eventCursor.getString(4));
        this.setLocation(eventCursor.getString(5));
        this.setAllDayEvent(eventCursor.getInt(6)==1);
        dirty = eventCursor.getInt(7)==1;
        this.setId(eventCursor.getLong(8));
        deleted = eventCursor.getInt(9)==1;
        System.out.println("Title:"+getTitle()+" dirty:"+dirty+" AID:"+android_id+" ["+getId()+"]");
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public long getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(long android_id) {
        this.android_id = android_id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    private boolean dirty;
    private long android_id;
    private boolean deleted;
}
