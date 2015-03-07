package de.uni_stuttgart.riot.android.calendar;

import android.database.Cursor;

import de.uni_stuttgart.riot.commons.rest.data.calendar.CalendarEntry;

/**
 * Created by dirkmb on 1/17/15.
 */
public class AndroidCalendarEventEntry extends CalendarEntry {

    private boolean dirty;
    private long androidId;
    private boolean deleted;

    AndroidCalendarEventEntry() {
    }

    /**
     * AndroidCalendarEventEntry constructor which creates the AndroidCalendarEventEntry based on the values of
     * an normal CalendarEntry.
     * @param entry
     *      the CalendarEntry containing the values
     */
    public AndroidCalendarEventEntry(CalendarEntry entry) {
        super(entry);
        dirty = false;
        deleted = false;
    }

    /**
     * AndroidCalendarEventEntry creates a entry based on the values from an cursor.
     * @param eventCursor
     *          the cursor containing the values
     */
    public AndroidCalendarEventEntry(Cursor eventCursor) {
        //CHECKSTYLE: OFF
        androidId = eventCursor.getLong(0);
        this.setTitle(eventCursor.getString(1));
        this.setStartTime(new java.util.Date(eventCursor.getLong(2)));
        this.setEndTime(new java.util.Date(eventCursor.getLong(3)));
        this.setDescription(eventCursor.getString(4));
        this.setLocation(eventCursor.getString(5));
        this.setAllDayEvent(eventCursor.getInt(6) == 1);
        dirty = eventCursor.getInt(7) == 1;
        this.setId(eventCursor.getLong(8));
        deleted = eventCursor.getInt(9) == 1;
        System.out.println("Title:" + getTitle() + " dirty:" + dirty + " AID:" + androidId + " [" + getId() + "]");
        //CHECKSTYLE: ON
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public long getAndroidId() {
        return androidId;
    }

    public void setAndroidId(long androidId) {
        this.androidId = androidId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
