package de.uni_stuttgart.riot.thing.car;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.commons.GPSPosition;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * This Thing represents a car. It has the following properties:
 * <ul>
 * <li>Position as longitude and latitude
 * <li>Tire pressure for every tire
 * <li>Heating switch
 * <li>configured temperature in degree Celsius
 * <li>actual interior temperature in degree Celsius
 * <li>tank fill level in liter
 * <li>lock switch
 * <li>engine state
 * <li>milage in KM
 * </ul>
 */
public class Car extends Thing {

    /**
     * The default interior temperature configuration in degree Celsius.
     */
    static final Double DEFAULT_TEMP = 18.0;

    /**
     * Tank max fill level in liter.
     */
    static final double TANK_MAX_FILL_LEVEL = 100.0;

    /**
     * Group for all temp and aircondition.
     */
    private static final int TEMP_GROUP = 3;

    /**
     * Group for gps position.
     */
    private static final int GPS_GROUP = 2;

    /**
     * Default tire pressure in bar.
     */
    private static final Double DEFAULT_TIRE_PRESSURE = 3.0;

    /**
     * Default longitude (in front of the university building).
     */
    private static final Double DEFAULT_LONGITUDE = 9.106539487838745;

    /**
     * Default latitude (in front of the university building).
     */
    private static final Double DEFAULT_LATITUDE = 48.74473604096915;

    /**
     * By default the heating is switched off.
     */
    private static final Boolean DEFAULT_HEATING = false;

    /**
     * Default tank fill level in liter
     */
    private static final Double DEFAULT_TANK_FILL_LEVEL = 40.0;

    /**
     * Default lock state (true=locked)
     */
    private static final Boolean DEFAULT_LOCK_STATE = true;

    /**
     * Default engine state (true = car is started)
     */
    private static final Boolean DEFAULT_ENGINE_STATE = false;

    /**
     * Default milage in KM
     */
    private static final Double DEFAULT_MILAGE = 42000.0;

    /**
     * Default battery state
     */
    private static final BatteryState DEFAULT_BATTERY_STATE = BatteryState.FULL;

    /**
     * Group for all four tire pressues.
     */
    private static final int TIRE_PRESSURE_GRUOP = 1;

    /**
     * Property for the tank fill level.
     */
    private final Property<BatteryState> batteryState = newProperty("batteryState", BatteryState.class, DEFAULT_BATTERY_STATE, UIHint.dropDown(BatteryState.class));

    /**
     * Property for the tank fill level.
     */
    private final Property<Double> milage = newProperty("milage", Double.class, DEFAULT_MILAGE, UIHint.editNumber());

    /**
     * Property for the tank fill level.
     */
    private final Property<Boolean> engineState = newProperty("engineState", Boolean.class, DEFAULT_ENGINE_STATE, UIHint.toggleButton());

    /**
     * Changeable Property lock state.
     */
    private final WritableProperty<Boolean> lock = newWritableProperty("lock", Boolean.class, DEFAULT_LOCK_STATE, UIHint.toggleButton());

    /**
     * Property for the tank fill level.
     */
    private final Property<Double> tankFillLevel = newProperty("tankFillLevel", Double.class, DEFAULT_TANK_FILL_LEVEL, UIHint.fractionalSlider(0.0, TANK_MAX_FILL_LEVEL));

    /**
     * Property for the tire pressure of the front left tire.
     */
    private final Property<Double> tirePressureFrontLeft = newProperty("tirePressureFrontLeft", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber().groupAndOrder(TIRE_PRESSURE_GRUOP));

    /**
     * Property for the tire pressure of the front right tire.
     */
    private final Property<Double> tirePressureFrontRight = newProperty("tirePressureFrontRight", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber().groupAndOrder(TIRE_PRESSURE_GRUOP));

    /**
     * Property for the tire pressure of the back left tire.
     */
    private final Property<Double> tirePressureBackLeft = newProperty("tirePressureBackLeft", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber().groupAndOrder(TIRE_PRESSURE_GRUOP));

    /**
     * Property for the tire pressure of the back right tire.
     */
    private final Property<Double> tirePressureBackRight = newProperty("tirePressureBackRight", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber().groupAndOrder(TIRE_PRESSURE_GRUOP));

    /**
     * Property for the longitude as part of the gps data.
     */
    private final Property<Double> longitude = newProperty("longitude", Double.class, DEFAULT_LONGITUDE, UIHint.editNumber().groupAndOrder(GPS_GROUP));

    /**
     * Property for the latitude as part of the gps data.
     */
    private final Property<Double> latitude = newProperty("latitude", Double.class, DEFAULT_LATITUDE, UIHint.editNumber().groupAndOrder(GPS_GROUP));

    /**
     * Changeable Property for the heating switch.
     */
    private final Property<Boolean> airCondition = newProperty("airCondition", Boolean.class, DEFAULT_HEATING, UIHint.toggleButton().groupAndOrder(TEMP_GROUP));

    /**
     * Changeable Property for the configured heating temperature in degree Celsius.
     */
    private final WritableProperty<Double> airConditionTemp = newWritableProperty("airConditionTemp", Double.class, DEFAULT_TEMP, UIHint.editNumber().groupAndOrder(TEMP_GROUP));

    /**
     * Property of the actual interior temperature in degree Celsius.
     */
    private final Property<Double> temp = newProperty("temp", Double.class, DEFAULT_TEMP, UIHint.editNumber().groupAndOrder(TEMP_GROUP));

    /**
     * Action to refuel the car.
     */
    private final Action<Refuel> refuel = newAction("refuel", Refuel.class);

    /**
     * Action to switch the heating on and off.
     */
    private final Action<ActionInstance> airConditionAction = newAction("airConditionAction");

    /**
     * Action to switch the engine on and off.
     */
    private final Action<ActionInstance> engineAction = newAction("engineAction");

    /**
     * Event that can be fired if the tank is almost empty;
     */
    private final Event<OutOfGasoline> outOfGasoline = newEvent("outOfGasoline", OutOfGasoline.class);

    /**
     * Event that is fired if refueling had lead to an tank overflow.
     */
    private final Event<EventInstance> tankOverflow = newEvent("tankOverflow");

    /**
     * Constructor for a car thing.
     * 
     * @param behavior
     *            behavior to be used.
     */
    public Car(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Getter for the state of the heating (true=heating is switched on, false otherwise).
     * 
     * @return true if the heating is switched on, false otherwise.
     */
    public boolean isAirConditionOn() {
        return airCondition.get();
    }

    /**
     * Getter for the engine state (true=heating is switched on, false otherwise).
     * 
     * @return true if the engine is switched on, false otherwise.
     */
    public boolean isEngineStarted() {
        return engineState.get();
    }

    /**
     * Getter for the tire pressure of the front right tire.
     * 
     * @return pressure in bar.
     */
    public Double getTirePressureFrontRight() {
        return tirePressureFrontRight.get();
    }

    /**
     * Getter for the tire pressure of the back left tire.
     * 
     * @return pressure in bar.
     */
    public Double getTirePressureBackLeft() {
        return tirePressureBackLeft.get();
    }

    /**
     * Getter for the tire pressure of the back right tire.
     * 
     * @return pressure in bar.
     */
    public Double getTirePressureBackRight() {
        return tirePressureBackRight.get();
    }

    /**
     * Getter for the tire pressure of the front left tire.
     * 
     * @return pressure in bar.
     */
    public Double getTirePressureFrontLeft() {
        return tirePressureFrontLeft.get();
    }

    /**
     * Getter for the tank fill level.
     * 
     * @return fill level in liter.
     */
    public Double getTankFillLevel() {
        return tankFillLevel.get();
    }

    /**
     * Getter for the actual interior temperature.
     * 
     * @return actual interior temperature in degree celsius.
     */
    public Double getInteriorTemperature() {
        return temp.get();
    }

    /**
     * Getter for the actual interior temperature {@link Property}.
     * 
     * @return actual interior temperature in degree celsius.
     */
    public Property<Double> getInteriorTemperatureProperty() {
        return temp;
    }

    /**
     * Getter for the configured Temperature of the heating.
     * 
     * @return configured temperature in degree celsius.
     */
    public Double getConfiguredTemperature() {
        return airConditionTemp.get();
    }

    /**
     * True if the car is locked, false otherwise.
     * 
     * @return state of the car lock.
     */
    public boolean isLocked() {
        return lock.get();
    }

    /**
     * Getter for the milage.
     * 
     * @return milage in KM.
     */
    public Double getMilage() {
        return milage.get();
    }

    /**
     * Getter for the battery state.
     * 
     * @return the battery state
     */
    public BatteryState getBatteryState() {
        return batteryState.get();
    }

    /**
     * Getter for position as GPS DATA.
     * 
     * @return the GPS Position
     */
    public GPSPosition getPosition() {
        return new GPSPosition(longitude.get(), latitude.get());
    }

    /**
     * Refuel the car.
     * 
     * @param amount
     *            amount in liter
     */
    public void refuel(Double amount) {
        refuel.fire(new Refuel(refuel, amount));
    }

    /**
     * Getter for the {@link OutOfGasoline} event.
     * 
     * @return the event
     */
    public Event<OutOfGasoline> getOutOfGasoline() {
        return outOfGasoline;
    }

    /**
     * Getter for the Refuel action.
     * 
     * @return the action
     */
    public Action<Refuel> getRefuelAction() {
        return refuel;
    }

    /**
     * Getter for the tank property.
     * 
     * @return tank property.
     */
    public Property<Double> getTankProperty() {
        return tankFillLevel;
    }

    /**
     * Getter for the engine property.
     * 
     * @return engine property.
     */
    public Property<Boolean> getEngineProperty() {
        return engineState;
    }

    /**
     * Getter for the Heating action (is used to turn the heating on and off).
     * 
     * @return the action
     */
    public Action<ActionInstance> getAirConditionAction() {
        return airConditionAction;
    }

    /**
     * Getter for the heating property that is the heating state (on or off).
     * 
     * @return the property
     */
    public Property<Boolean> getAirConditionProperty() {
        return airCondition;
    }

    /**
     * Getter for the tankOverflow event.
     * 
     * @return the event
     */
    public Event<EventInstance> getTankOverflow() {
        return tankOverflow;
    }

    /**
     * Getter for the engineAction.
     * 
     * @return the action
     */
    public Action<ActionInstance> getEngineAction() {
        return engineAction;
    }

}
