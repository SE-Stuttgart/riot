package de.uni_stuttgart.riot.calendar;

import java.util.Date;

import de.uni_stuttgart.riot.rest.ResourceModel;

// TODO: Auto-generated Javadoc
/**
 * The Class CalendarEntry.
 */
public class CalendarEntry implements ResourceModel {

    /** The id. */
    private int id;
    
    /** The date. */
    private Date date;
    
    /** The title. */
    private String title;
    
    /** The body. */
    private String body;

    /**
     * Instantiates a new calendar entry.
     */
    public CalendarEntry() {
        this.date = new Date();
    }

    /**
     * Instantiates a new calendar entry.
     *
     * @param id the id
     * @param title the title
     * @param body the body
     */
    public CalendarEntry(int id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = new Date();
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.rest.ResourceModel#getId()
     */
    @Override
    public int getId() {
        return this.id;
    }

    /* (non-Javadoc)
     * @see de.uni_stuttgart.riot.rest.ResourceModel#setId(int)
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
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
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body.
     *
     * @param body the new body
     */
    public void setBody(String body) {
        this.body = body;
    }

}
