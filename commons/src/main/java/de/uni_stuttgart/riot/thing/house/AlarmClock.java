package de.uni_stuttgart.riot.thing.house;

import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a alarm clock.
 *
 */
public class AlarmClock extends Thing {

    private static final int MAX_MINUTE = 59;
    private static final int MAX_HOUR = 24;
    private static final Integer DEFAULT_HOUR = 12;
    private static final Integer DEFAULT_MINUTE = 0;

    private final WritableProperty<Integer> hour = newWritableProperty("hour", Integer.class, DEFAULT_HOUR, UIHint.integralSlider(1, MAX_HOUR));

    private final WritableProperty<Integer> minute = newWritableProperty("minute", Integer.class, DEFAULT_MINUTE, UIHint.integralSlider(0, MAX_MINUTE));

    private final WritableProperty<Boolean> activated = newWritableProperty("activated", Boolean.class, false, UIHint.toggleButton());

    private final WritableProperty<Boolean> alarm = newWritableProperty("alarm", Boolean.class, false, UIHint.toggleButton());

    private final NotificationEvent<EventInstance> alarmEvent = newNotification("alarm", NotificationSeverity.INFO_NOW);

    /**
     * Constructor for the {@link AlarmClock}.
     * 
     * @param behavior
     *            behavior be be used
     */
    public AlarmClock(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the hour property.
     * 
     * @return the hour
     */
    public WritableProperty<Integer> getHourProperty() {
        return hour;
    }

    /**
     * Getter for the minute property.
     * 
     * @return the minute
     */
    public WritableProperty<Integer> getMinuteProperty() {
        return minute;
    }

    /**
     * Getter for the hour value.
     * 
     * @return the hour (1 - 24)
     */
    public Integer getHour() {
        return hour.get();
    }

    /**
     * Getter for the minute value.
     * 
     * @return the minute (0-59)
     */
    public Integer getMinute() {
        return minute.get();
    }

    /**
     * Getter for the activated property.
     * 
     * @return the activated property
     */
    public WritableProperty<Boolean> getActivatedProperty() {
        return activated;
    }

    /**
     * Getter for the activated value.
     * 
     * @return the activated
     */
    public Boolean isActivated() {
        return activated.get();
    }

    /**
     * Getter for the alarm property.
     * 
     * @return the alarm
     */
    public WritableProperty<Boolean> getAlarmProperty() {
        return alarm;
    }

    /**
     * Getter for the alarm value.
     * 
     * @return the alarm
     */
    public Boolean isAlarm() {
        return alarm.get();
    }

    /**
     * Getter for the alarm event.
     * 
     * @return the alarmEvent
     */
    public Event<EventInstance> getAlarmEvent() {
        return alarmEvent;
    }

    /**
     * Sets the {@link AlarmClock} to the given time.
     * 
     * @param nHour
     *            hour as int from 1 to 24
     * @param nMinute
     *            minute as int from 0 bis 59
     */
    public void setAlarmTime(int nHour, int nMinute) {
        this.hour.set(nHour);
        this.minute.set(nMinute);
    }

}
