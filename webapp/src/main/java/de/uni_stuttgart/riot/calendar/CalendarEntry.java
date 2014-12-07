package de.uni_stuttgart.riot.calendar;

import java.util.Date;

import de.uni_stuttgart.riot.rest.ResourceModel;
/**
 * The Class CalendarEntry.
 */
public class CalendarEntry implements ResourceModel {

    /** The id. */
    private long id;

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
     * Instantiates a new calendar entry.
     *
     * @param id
     *            the id
     * @param title
     *            the title
     * @param body
     *            the body
     */
    public CalendarEntry(long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = new Date();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ResourceModel#getId()
     */
    @Override
    public long getId() {
        return this.id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.rest.ResourceModel#setId(int)
     */
    @Override
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title
     *            the new title
     */
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

}
