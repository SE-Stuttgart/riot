package de.uni_stuttgart.riot.thing.car;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
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
 * <ul>
 */
public class Car extends Thing {

    /**
     * Default tire pressure in bar.
     */
    private final Double DEFAULT_TIRE_PRESSURE = 3.0;

    /**
     * Default longitude (in front of the university building).
     */
    private final Double DEFAULT_LONGITUDE = 9.106539487838745;

    /**
     * Default latitude (in front of the university building).
     */
    private final Double DEFAULT_LATITUDE = 48.74473604096915;

    /**
     * By default the heating is switched off.
     */
    private final Boolean DEFAULT_HEATING = false;

    /**
     * The default interior temperature configuration in degree Celsius.
     */
    private final Double DEFAULT_HEATING_TEMP = 22.0;

    /**
     * Default tank fill level in liter
     */
    private final Double DEFAULT_TANK_FILL_LEVEL = 40.0;

    /**
     * Tank max fill level in liter
     */
    static final double TANK_MAX_FILL_LEVEL = 100.0;

    /**
     * Default lock state (true=locked)
     */
    private final Boolean DEFAULT_LOCK_STATE = true;

    /**
     * Default engine state (true = car is started)
     */
    private final Boolean DEFAULT_ENGINE_STATE = false;

    /**
     * Default milage in KM
     */
    private final Double DEFAULT_MILAGE = 42000.0;

    /**
     * Default battery state
     */
    private final BatteryState DEFAULT_BATTERY_STATE = BatteryState.FULL;

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
    private final Property<Double> tirePressureFrontLeft = newProperty("tirePressureFrontLeft", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber());

    /**
     * Property for the tire pressure of the front right tire.
     */
    private final Property<Double> tirePressureFrontRight = newProperty("tirePressureFrontRight", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber());

    /**
     * Property for the tire pressure of the back left tire.
     */
    private final Property<Double> tirePressureBackLeft = newProperty("tirePressureBackLeft", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber());

    /**
     * Property for the tire pressure of the back right tire.
     */
    private final Property<Double> tirePressureBackRight = newProperty("tirePressureBackRight", Double.class, DEFAULT_TIRE_PRESSURE, UIHint.editNumber());

    /**
     * Property for the longitude as part of the gps data.
     */
    private final Property<Double> longitude = newProperty("longitude", Double.class, DEFAULT_LONGITUDE, UIHint.editNumber());

    /**
     * Property for the latitude as part of the gps data.
     */
    private final Property<Double> latitude = newProperty("latitude", Double.class, DEFAULT_LATITUDE, UIHint.editNumber());

    /**
     * Changeable Property for the heating switch.
     */
    private final WritableProperty<Boolean> heating = newWritableProperty("heating", Boolean.class, DEFAULT_HEATING, UIHint.toggleButton());

    /**
     * Changeable Property for the configured heating temperature in degree Celsius.
     */
    private final WritableProperty<Double> heatingTemp = newWritableProperty("heatingTemp", Double.class, DEFAULT_HEATING_TEMP, UIHint.toggleButton());

    /**
     * Property of the actual interior temperature in degree Celsius.
     */
    private final Property<Double> temp = newProperty("temp", Double.class, DEFAULT_HEATING_TEMP, UIHint.toggleButton());

    private final Action<ActionInstance> refuel = newAction("refuel");

    private final Event<OutOfGasoline> outOfGasoline = newEvent("outOfGasoline", OutOfGasoline.class);

    /**
     * Constructor for a car thing.
     * 
     * @param name
     *            name of the car.
     * @param behavior
     *            behavior to be used.
     */
    public Car(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

    /**
     * Getter for the state of the heating (true=heating is switched on, false otherwise).
     * 
     * @return true if the heating is switched on, false otherwise.
     */
    public boolean isHeatingOn() {
        return heating.getValue();
    }

    /**
     * Getter for the engine state (true=heating is switched on, false otherwise).
     * 
     * @return true if the engine is switched on, false otherwise.
     */
    public boolean isEngineStarted() {
        return engineState.getValue();
    }

    /**
     * Getter for the tire pressure of the front right tire.
     * @return pressure in bar.
     */
    public Double getTirePressureFrontRight() {
        return tirePressureFrontRight.getValue();
    }

    /**
     * Getter for the tire pressure of the back left tire.
     * @return pressure in bar.
     */
    public Double getTirePressureBackLeft() {
        return tirePressureBackLeft.getValue();
    }

    /**
     * Getter for the tire pressure of the back right tire.
     * @return pressure in bar.
     */
    public Double getTirePressureBackRight() {
        return tirePressureBackRight.getValue();
    }

    /**
     * Getter for the tire pressure of the front left tire.
     * @return pressure in bar.
     */
    public Double getTirePressureFrontLeft() {
        return tirePressureFrontLeft.getValue();
    }
    
    /**
     * Getter for the tank fill level.
     * @return fill level in liter.
     */
    public Double getTankFillLevel(){
        return tankFillLevel.getValue();
    }
    
    /**
     * Getter for the actual interior temperature.
     * @return actual interior temperature in degree celsius.
     */
    public Double getInteriorTemperature(){
        return temp.getValue();
    }
    
    /**
     * Getter for the configured Temperature of the heating
     * @return configured temperature in degree celsius.
     */
    public Double getConfiguredTemperature(){
        return heatingTemp.getValue();
    }
    
    /**
     * True if the car is locked, false otherwise.
     * @return state of the car lock.
     */
    public boolean isLocked(){
        return lock.getValue();
    }
    
    /**
     * Getter for the milage.
     * @return milage in KM.
     */
    public Double getMilage(){
        return milage.getValue();
    }
    
    /**
     * Getter for the battery state
     */
    public BatteryState getBatteryState(){
        return batteryState.getValue();
    }
    
    /**
     * Getter for position as GPS DATA.
     * @return  
     */
    public GPSPosition getPosition(){
        return new GPSPosition(longitude.getValue(), latitude.getValue());
    }
    
    /**
     * Refuel the car.
     * @param amount amount in liter
     */
    public void refuel(Double amount){
        refuel.fire(new ActionInstance(refuel));
    }
    
}
