package de.uni_stuttgart.riot.model;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a single calendar event.
 * 
 * @author Ana
 *
 */
public class CalendarEvent {

    private String id; // unique ID
    private String title;
    private Date startTime;
    private Date endTime;
    private boolean allDayEvent;
    private String description;
    private String location;

    /**
     * Creates a new calendar event.
     * 
     * @param title
     *            The title/description of the event.
     * @param startTime
     *            The start time.
     * @param endTime
     *            The end time.
     */
    public CalendarEvent(String title, Date startTime, Date endTime) {
        super();
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
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
}
