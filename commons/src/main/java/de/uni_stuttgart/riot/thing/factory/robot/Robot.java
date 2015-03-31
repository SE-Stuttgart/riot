package de.uni_stuttgart.riot.thing.factory.robot;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a Robot at the factory.
 */
public class Robot extends Thing {

    static final long ACTION_DURATION = 10000;

    /**
     * The on/off switch, where false is off.
     */
    private final WritableProperty<Boolean> powerSwitch = newWritableProperty("powerSwitch", Boolean.class, false, UIHint.toggleButton());

    /**
     * The robot status. It is initially WAITING.
     */
    private final Property<RobotStatus> status = newProperty("status", RobotStatus.class, RobotStatus.WAITING, UIHint.dropDown(RobotStatus.class));

    /**
     * Action for refilling material tank of a machine.
     */
    private final Action<ActionInstance> pressRefillMaterialTank = newAction("pressRefillMaterialTank");

    /**
     * Action for emptying the processed pieces tank of a factory machine.
     */
    private final Action<ActionInstance> pressEmptyProcessedPiecesTank = newAction("pressEmptyProcessedPiecesTank");

    /**
     * The transportation duration need from the robot to execute an action (fill material tank of factory machine, ...).
     */
    private final Property<Integer> transportDuration = newProperty("transportDuration", Integer.class, 0, UIHint.integralSlider(0, ACTION_DURATION));

    /**
     * An event signaling that the robot has emptied the factory machine tank of processed pieces.
     */
    private final Event<EventInstance> processedPiecesTankIsEmpty = newEvent("processedPiecesTankIsEmpty");

    /**
     * An event signaling that the robot has filled the factory machine tank of material.
     */
    private final Event<EventInstance> materialTankIsFilled = newEvent("materialTankIsFilled");

    /**
     * Instantiates a new robot.
     *
     * @param behavior
     *            the behavior of the robot
     */
    public Robot(ThingBehavior behavior) {
        super(behavior);
    }

    public boolean isPowerOn() {
        return powerSwitch.get();
    }

    public void setPowerSwitch(boolean newValue) { // NOCS
        powerSwitch.set(newValue);
    }

    /**
     * Presses the button for refilling material tank at a factory machine.
     */
    public void pressRefillMaterialTank() {
        pressRefillMaterialTank.fire(new ActionInstance(pressRefillMaterialTank));
    }

    public Action<ActionInstance> getPressRefillMaterialTankAction() {
        return pressRefillMaterialTank;
    }

    /**
     * Presses the button for refilling material tank at a factory machine.
     */
    public void pressEmptyProcessedPiecesTank() {
        pressEmptyProcessedPiecesTank.fire(new ActionInstance(pressEmptyProcessedPiecesTank));
    }

    public Action<ActionInstance> getPressEmptyProcessedPiecesTankAction() {
        return pressEmptyProcessedPiecesTank;
    }

    public RobotStatus getRobotStatus() {
        return status.get();
    }

    public Property<RobotStatus> getRobotStatusProperty() {
        return status;
    }

    public int getTransportDuration() {
        return transportDuration.get();
    }

    public Property<Integer> getTransportDurationProperty() {
        return transportDuration;
    }

    public Event<EventInstance> getProcessedPiecesTankIsEmptyEvent() {
        return processedPiecesTankIsEmpty;
    }

    public Event<EventInstance> getMaterialTankIsFilled() {
        return materialTankIsFilled;
    }
}
