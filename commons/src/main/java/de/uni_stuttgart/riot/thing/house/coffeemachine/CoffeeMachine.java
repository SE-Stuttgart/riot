package de.uni_stuttgart.riot.thing.house.coffeemachine;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a coffee machine.
 * 
 * @author Philipp Keck
 */
public class CoffeeMachine extends Thing {

    /**
     * The size of the water tank in milliliters.
     */
    static final double WATER_TANK_SIZE = 1000;

    /**
     * The size of the bean tank (in number of beans).
     */
    static final int BEAN_TANK_SIZE = 500;

    /**
     * The on/off switch, where false is off.
     */
    private final WritableProperty<Boolean> powerSwitch = newWritableProperty("powerSwitch", Boolean.class, false, UIHint.toggleButton());

    /**
     * The amount of water in the water tank, measured in milliliters. The tank is initially full.
     */
    private final Property<Double> waterTank = newProperty("waterTank", Double.class, WATER_TANK_SIZE, UIHint.fractionalSlider(0, WATER_TANK_SIZE));

    /**
     * An event signalling that there is only little or no water left.
     */
    private final NotificationEvent<OutOfWater> outOfWater = newNotification("outOfWater", OutOfWater.class);

    /**
     * The action of refilling the water tank. It will be filled to its maximum.
     */
    private final Action<ActionInstance> refillWater = newAction("refillWater");

    /**
     * The number of beans in the bean tank. The tank is initially half full.
     */
    private final Property<Integer> beanTank = newProperty("beanTank", Integer.class, BEAN_TANK_SIZE, UIHint.integralSlider(0, BEAN_TANK_SIZE));

    /**
     * An event signaling that there are only little or no beans left.
     */
    private final NotificationEvent<OutOfBeans> outOfBeans = newNotification("outOfBeans", OutOfBeans.class);

    /**
     * The action of refilling a (specific number) of beans to the tank.
     */
    private final Action<RefillBeans> refillBeans = newAction("refillBeans", RefillBeans.class);

    /**
     * The amount of waste in the drip tray of the machine. We assume that the tray is unlimited.
     */
    private final Property<Double> dripTray = newProperty("dripTray", Double.class, 0.0, UIHint.editNumber());

    /**
     * The action of emptying the drip tray.
     */
    private final Action<ActionInstance> emptyDripTray = newAction("emptyDripTray");

    /**
     * A percentage value indicating the brew strength for coffee that is made with the machine.
     */
    private final WritableProperty<Double> brewStrength = newWritableProperty("brewStrength", Double.class, 0.5, UIHint.percentageSlider());

    /**
     * The state of the cup size switch.
     */
    private final WritableProperty<CupSize> cupSize = newWritableProperty("cupSize", CupSize.class, CupSize.MEDIUM, UIHint.dropDown(CupSize.class));

    /**
     * The busy state of the machine, where <tt>true</tt> means that it is working.
     */
    private final Property<Boolean> busy = newProperty("busy", Boolean.class, false, UIHint.toggleButton());

    /**
     * Starts the coffee making with the current setting of {@link #brewStrength}. If the machine is already busy, the action will be
     * ignored.
     */
    private final Action<ActionInstance> pressStart = newAction("pressStart");

    /**
     * Starts cleaning the machine. If the machine is already busy, the action will be ignored.
     */
    private final Action<ActionInstance> clean = newAction("clean");

    /**
     * An event that is raised when the coffee making finished. If the coffee making fails, this event is not raised (possibly another one,
     * indicating the failure).
     */
    private final NotificationEvent<EventInstance> coffeeFinished = newNotification("coffeeFinished");

    /**
     * Creates a new coffee machine.
     * 
     * @param behavior
     *            The thing behavior.
     */
    public CoffeeMachine(ThingBehavior behavior) {
        super(behavior);
    }

    public boolean isPowerOn() {
        return powerSwitch.get();
    }

    public void setPowerSwitch(boolean newValue) { // NOCS
        powerSwitch.set(newValue);
    }

    public double getWaterTank() {
        return waterTank.get();
    }

    public Property<Double> getWaterTankProperty() {
        return waterTank;
    }

    public Event<OutOfWater> getOutOfWaterEvent() {
        return outOfWater;
    }

    public Action<ActionInstance> getRefillWaterAction() {
        return refillWater;
    }

    /**
     * Fires the action for refilling the water tank.
     */
    public void refillWater() {
        refillWater.fire(new ActionInstance(refillWater));
    }

    public int getBeanTank() {
        return beanTank.get();
    }

    public Property<Integer> getBeanTankProperty() {
        return beanTank;
    }

    public Event<OutOfBeans> getOutOfBeansEvent() {
        return outOfBeans;
    }

    public Action<RefillBeans> getRefillBeansAction() {
        return refillBeans;
    }

    /**
     * Fires the action for refilling the beans.
     * 
     * @param amount
     *            The amount of beans to refill.
     */
    public void refillBeans(int amount) {
        refillBeans.fire(new RefillBeans(refillBeans, amount));
    }

    public double getDripTray() {
        return dripTray.get();
    }

    public Property<Double> getDripTrayProperty() {
        return dripTray;
    }

    public Action<ActionInstance> getEmptyDripTrayAction() {
        return emptyDripTray;
    }

    /**
     * Fires the action for emptying the drip tray.
     */
    public void emptyDripTray() {
        emptyDripTray.fire(new ActionInstance(emptyDripTray));
    }

    public double getBrewStrength() {
        return brewStrength.get();
    }

    public void setBrewStrength(double newValue) { // NOCS
        brewStrength.set(newValue);
    }

    public WritableProperty<Double> getBrewStrengthProperty() {
        return brewStrength;
    }

    public CupSize getCupSize() {
        return cupSize.get();
    }

    public void setCupSize(CupSize newValue) { // NOCS
        cupSize.set(newValue);
    }

    public WritableProperty<CupSize> getCupSizeProperty() {
        return cupSize;
    }

    public boolean isBusy() {
        return busy.get();
    }

    public Property<Boolean> getBusyProperty() {
        return busy;
    }

    public Action<ActionInstance> getPressStartAction() {
        return pressStart;
    }

    /**
     * Presses the start button for making coffee.
     */
    public void pressStart() {
        pressStart.fire(new ActionInstance(pressStart));
    }

    public Action<ActionInstance> getCleanAction() {
        return clean;
    }

    /**
     * Starts the cleaning.
     */
    public void clean() {
        clean.fire(new ActionInstance(clean));
    }

    public Event<EventInstance> getCoffeeFinishedEvent() {
        return coffeeFinished;
    }

}
