package de.uni_stuttgart.riot.thing.factory.robot;

import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.WritableReferenceProperty;
import de.uni_stuttgart.riot.thing.factory.ThingStatusEvent;
import de.uni_stuttgart.riot.thing.factory.machine.Machine;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a Robot at the factory.
 */
public class Robot extends Thing {

    static final long ACTION_DURATION = 10000;

    /**
     * The machine that the robot is responsible for. All robot actions will have no effect if this machine is not set.
     */
    private final WritableReferenceProperty<Machine> machine = newWritableReferenceProperty("machine", Machine.class, UIHint.thingDropDown(Machine.class, ThingPermission.READ, ThingPermission.CONTROL));

    /**
     * The robot fires this notification if it feels lonely. It was asked to do something, but the machine to work for has not been set.
     */
    private final NotificationEvent<EventInstance> machineMissing = newNotification("machineMissing", NotificationSeverity.ERROR);

    /**
     * The on/off switch, where false is off. All robot actions will have no effect if this is set to off.
     */
    private final WritableProperty<Boolean> powerSwitch = newWritableProperty("powerSwitch", Boolean.class, false, UIHint.toggleButton());

    /**
     * The robot status. It is initially IDLE.
     */
    private final Property<RobotStatus> status = newProperty("status", RobotStatus.class, RobotStatus.IDLE, UIHint.dropDown(RobotStatus.class));

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
    private final NotificationEvent<EventInstance> processedPiecesTankIsEmpty = newNotification("processedPiecesTankIsEmpty", NotificationSeverity.INFO);

    /**
     * An event signaling that the robot has filled the factory machine tank of material.
     */
    private final NotificationEvent<EventInstance> materialTankIsFilled = newNotification("materialTankIsFilled", NotificationSeverity.INFO);

    /**
     * An event signaling that the status of the robot changed.
     */
    private final NotificationEvent<ThingStatusEvent> statusChanged = newNotification("statusChanged", ThingStatusEvent.class, NotificationSeverity.INFO);

    /**
     * Instantiates a new robot.
     *
     * @param behavior
     *            the behavior of the robot
     */
    public Robot(ThingBehavior behavior) {
        super(behavior);
    }

    /**
     * Gets the machine that this robot is responsible for.
     * 
     * @return The machine.
     * @throws ResolveReferenceException
     *             If the machine could not be resolved.
     */
    public Machine getMachine() throws ResolveReferenceException {
        return machine.getTarget();
    }

    /**
     * Sets the machine that this robot is responsible for.
     * 
     * @param machine
     *            The machine.
     */
    public void setMachine(Machine machine) {
        if (this.getRobotStatus() != RobotStatus.IDLE) {
            throw new IllegalStateException("Can only change machine when idle!");
        }
        this.machine.setTarget(machine);
    }

    /**
     * Determines if the machine is set.
     * 
     * @return True if the robot has a machine to work for.
     */
    public boolean hasMachine() {
        return this.machine.getId() != null;
    }

    public WritableReferenceProperty<Machine> getMachineProperty() {
        return machine;
    }

    public NotificationEvent<EventInstance> getMachineMissingEvent() {
        return machineMissing;
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

    public Event<EventInstance> getMaterialTankIsFilledEvent() {
        return materialTankIsFilled;
    }

    public Event<ThingStatusEvent> getStatusChangedEvent() {
        return statusChanged;
    }
}
