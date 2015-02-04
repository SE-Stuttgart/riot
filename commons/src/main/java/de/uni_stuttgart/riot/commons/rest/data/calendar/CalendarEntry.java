package de.uni_stuttgart.riot.commons.rest.data.calendar;

import java.util.Date;

import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * The Class CalendarEntry.
 */
public class CalendarEntry extends Storable {

    /** The start time. */
    private Date startTime;

    /** The end time. */
    private Date endTime;

    /** The all day event. */
    private boolean allDayEvent;

    /** The description. */
    private String description;

    /** The location. */
    private String location;

    /** The title. */
    private String title;

    /**
     * Instantiates a new calendar entry.
     */
    public CalendarEntry() {
        this.startTime = new Date();
    }

    /**
     * Copy Constructor.
     *
     * @param entry
     *            the entry to copy
     */
    public CalendarEntry(CalendarEntry entry) {
        super(entry.getId());
        this.title = entry.getTitle();
        this.description = entry.getDescription();
        this.startTime = entry.getStartTime();
        this.endTime = entry.getEndTime();
        this.location = entry.getLocation();
        this.allDayEvent = entry.isAllDayEvent();
    }


    /**
     * Instantiates a new calendar entry.
     *
     * @param id
     *            the id
     * @param title
     *            the title
     * @param description
     *            the body
     */
    public CalendarEntry(long id, String title, String description) {
        super(id);
        this.title = title;
        this.description = description;
        this.startTime = new Date();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        this.allDayEvent = allDayEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // CHECKSTYLE:OFF (auto-generated code only)
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (allDayEvent ? 1231 : 1237);
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CalendarEntry other = (CalendarEntry) obj;
        if (allDayEvent != other.allDayEvent)
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (endTime == null) {
            if (other.endTime != null)
                return false;
        } else if (!endTime.equals(other.endTime))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

}
