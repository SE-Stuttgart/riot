package de.uni_stuttgart.riot.thing.house.heating;

import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a radiator, which is a part of a heating system.
 */
public class Radiator extends Thing {

    private static final Double DEFAULT_CONFIGURED_TEMP = 22.0;

    private static final Double DEFAULT_THRESHOLD = 3.0;

    /**
     * The configured temperature for this radiator.
     */
    private final WritableProperty<Double> configuredTemp = newWritableProperty("configuredTemp", Double.class, DEFAULT_CONFIGURED_TEMP, UIHint.editNumber());

    /**
     * The currently measured temperature by the radiator.
     */
    private final Property<Double> measuredTemp = newProperty("measuredTemp", Double.class, DEFAULT_CONFIGURED_TEMP, UIHint.editNumber());

    /**
     * True if the {@link Radiator} is currently heating.
     */
    private final Property<Boolean> heating = newProperty("heating", Boolean.class, false, UIHint.toggleButton());

    /**
     * The configured temperature for this radiator.
     */
    private final WritableProperty<Double> threshold = newWritableProperty("threshold", Double.class, DEFAULT_THRESHOLD, UIHint.editNumber());

    /**
     * {@link NotificationEvent} to inform about a state change to heating.
     */
    private final NotificationEvent<EventInstance> onEvent = newNotification("onEvent", NotificationSeverity.INFO_NOW);

    /**
     * {@link NotificationEvent} to inform about a state change to not heating.
     */
    private final NotificationEvent<EventInstance> offEvent = newNotification("offEvent", NotificationSeverity.INFO_NOW);

    /**
     * Constructor for the {@link Radiator}.
     * 
     * @param behavior
     *            behavior to be used
     */
    public Radiator(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the configured temperature property.
     * 
     * @return the property
     */
    public WritableProperty<Double> getConfiguredTempProperty() {
        return configuredTemp;
    }

    /**
     * Getter for the measured temperature property.
     * 
     * @return the measuredTemp in degree celsius
     */
    public Property<Double> getMeasuredTempProperty() {
        return measuredTemp;
    }

    /**
     * Getter for the configured temperature value in degree celsius.
     * 
     * @return the value
     */
    public Double getConfiguredTemp() {
        return configuredTemp.get();
    }

    /**
     * Sets the configured temperature.
     * 
     * @param temp
     *            The temperature.
     */
    public void setConfiguredTemp(double temp) {
        configuredTemp.set(temp);
    }

    /**
     * Getter for the measured temperature value.
     * 
     * @return the measuredTemp in degree celsius
     */
    public Double getMeasuredTemp() {
        return measuredTemp.get();
    }

    /**
     * Getter for the heating {@link Property}.
     * 
     * @return the heating
     */
    public Property<Boolean> getHeatingProperty() {
        return heating;
    }

    /**
     * Getter for the heating value.
     * 
     * @return the heating value (true -> heating on, false otherwise)
     */
    public Boolean getHeating() {
        return heating.get();
    }

    /**
     * Getter for the theshold property.
     * 
     * @return the threshold
     */
    public WritableProperty<Double> getThresholdProperty() {
        return threshold;
    }

    /**
     * Getter for the theshold value.
     * 
     * @return the threshold value in degree celsius.
     */
    public Double getThreshold() {
        return threshold.get();
    }

    /**
     * Getter for the On Event.
     * 
     * @return the onEvent
     */
    public NotificationEvent<EventInstance> getOnEvent() {
        return onEvent;
    }

    /**
     * Getter for the off event.
     * 
     * @return the offEvent
     */
    public NotificationEvent<EventInstance> getOffEvent() {
        return offEvent;
    }

    /**
     * Setter for the threshold value, this value is used as follows: if the the measured temperature is less than configured temperature -
     * threshold the readiator should turn on.
     * 
     * @param thresholdValue
     *            the new value
     */
    public void setThreshold(double thresholdValue) {
        this.threshold.set(thresholdValue);
    }

}
