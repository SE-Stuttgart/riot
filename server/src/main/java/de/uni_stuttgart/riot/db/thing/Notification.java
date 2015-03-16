package de.uni_stuttgart.riot.db.thing;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.thing.NotificationEvent;

/**
 * {@link Notification} represents the notifications of a thing.
 * 
 * @author Niklas Schnabel
 *
 */
@JsonIgnoreProperties("id")
public class Notification extends Storable {

    /** The id of the storable. */
    private Long id;

    /** The thing id. */
    private Long thingID;

    /** The name of the notification. */
    private String name;

    /** The time the notification was fired. */
    private Date time;

    /**
     * Instantiates a new notification storable with invalid values. This class has the suffix <tt>storable</tt> so it gets not mistaken
     * with {@link NotificationEvent}.
     */
    public Notification() {
        this(-1L, "INVALID", new Date());
    }

    /**
     * Instantiates a new notification storable.
     *
     * @param thingID
     *            The id of the thing
     * @param name
     *            The name of the notification
     * @param time
     *            The time the notification was fired
     */
    public Notification(Long thingID, String name, Date time) {
        this(-1L, thingID, name, time);
    }

    /**
     * Instantiates a new notification storable.
     *
     * @param id
     *            The id of the storable
     * @param thingID
     *            The id of the thing
     * @param name
     *            The name of the notification
     * @param time
     *            The time the notification was fired
     */
    public Notification(Long id, Long thingID, String name, Date time) {
        this.id = id;
        this.thingID = thingID;
        this.name = name;
        this.time = time;
    }

    public Long getThingID() {
        return thingID;
    }

    public void setThingID(Long thingID) {
        this.thingID = thingID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    // CHECKSTYLE: OFF
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((thingID == null) ? 0 : thingID.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
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
        Notification other = (Notification) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (thingID == null) {
            if (other.thingID != null)
                return false;
        } else if (!thingID.equals(other.thingID))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        return true;
    }
}
