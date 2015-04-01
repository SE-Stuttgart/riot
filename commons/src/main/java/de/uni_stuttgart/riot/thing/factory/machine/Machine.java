package de.uni_stuttgart.riot.thing.factory.machine;

import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.NotificationEvent;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.factory.ThingStatusEvent;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Represents a factory machine.
 */
public class Machine extends Thing {

    /**
     * The size of the material tank (in number of pieces).
     */
    public static final int MATERIAL_TANK_SIZE = 50;

    /**
     * The size of the processed pieces tank (in number of pieces).
     */
    static final int PROCESSED_PIECES_TANK_SIZE = 10;

    /**
     * The on/off switch, where false is off.
     */
    private final WritableProperty<Boolean> powerSwitch = newWritableProperty("powerSwitch", Boolean.class, false, UIHint.toggleButton());

    /**
     * The machine status. It is initially IDLE.
     */
    private final Property<MachineStatus> status = newProperty("status", MachineStatus.class, MachineStatus.IDLE, UIHint.dropDown(MachineStatus.class));

    /**
     * The machine speed in which it processes the material.
     */
    private final WritableProperty<ProcessingSpeed> processingSpeed = newWritableProperty("processingSpeed", ProcessingSpeed.class, ProcessingSpeed.SLOW, UIHint.dropDown(ProcessingSpeed.class));

    /**
     * The number of material pieces in the material tank. The tank is initially full.
     */
    private final Property<Integer> materialTank = newProperty("materialTank", Integer.class, MATERIAL_TANK_SIZE, UIHint.integralSlider(0, MATERIAL_TANK_SIZE));

    /**
     * An event signaling that there is only little or no material left.
     */
    private final NotificationEvent<OutOfMaterial> outOfMaterial = newNotification("outOfMaterial", OutOfMaterial.class, NotificationSeverity.WARNING);

    /**
     * The action of refilling the material tank. It will be filled to its maximum.
     */
    private final Action<RefillMaterial> refillMaterial = newAction("refillMaterial", RefillMaterial.class);

    /**
     * The number of processed pieces in the tank. The tank is initially empty.
     */
    private final Property<Integer> processedPiecesTank = newProperty("processedPiecesTank", Integer.class, 0, UIHint.integralSlider(0, PROCESSED_PIECES_TANK_SIZE));

    /**
     * An event signaling that the tank of processed pieces is full.
     */
    private final NotificationEvent<FullProcessedPiecesTank> fullProcessedPiecesTank = newNotification("fullProcessedPiecesTank", FullProcessedPiecesTank.class, NotificationSeverity.WARNING);

    /**
     * The action of emptying the processed pieces tank.
     */
    private final Action<ActionInstance> emptyProcessedPiecesTank = newAction("emptyProcessedPiecesTank");

    /**
     * An event signaling that the status of the machine changed.
     */
    private final NotificationEvent<ThingStatusEvent> statusChanged = newNotification("statusChanged", ThingStatusEvent.class, NotificationSeverity.INFO);

    /**
     * Starts the processing at the machine with the current setting of {@link #processingSpeed}. If the machine is already busy, the action
     * will be ignored.
     */
    private final Action<ActionInstance> pressStart = newAction("pressStart");

    /**
     * Stops the processing at the machine.
     */
    private final Action<ActionInstance> pressStop = newAction("pressStop");

    /**
     * Instantiates a new machine.
     *
     * @param behavior
     *            the behavior of the machine
     */
    public Machine(ThingBehavior behavior) {
        super(behavior);
    }

    public boolean isPowerOn() {
        return powerSwitch.get();
    }

    public void setPowerSwitch(boolean newValue) { // NOCS
        powerSwitch.set(newValue);
    }

    public int getMaterialTank() {
        return materialTank.get();
    }

    public Property<Integer> getMaterialTankProperty() {
        return materialTank;
    }

    public Event<OutOfMaterial> getOutOfMaterialEvent() {
        return outOfMaterial;
    }

    public Action<RefillMaterial> getRefillMaterialAction() {
        return refillMaterial;
    }

    /**
     * Fires the action for refilling the material tank.
     *
     * @param amount
     *            the amount
     */
    public void refillMaterial(int amount) {
        refillMaterial.fire(new RefillMaterial(refillMaterial, amount));
    }

    public int getProcessedPiecesTank() {
        return processedPiecesTank.get();
    }

    public Property<Integer> getProcessedPiecesTankProperty() {
        return processedPiecesTank;
    }

    public Event<FullProcessedPiecesTank> getFullProcessedPiecesTankEvent() {
        return fullProcessedPiecesTank;
    }

    public Action<ActionInstance> getEmptyProcessedPiecesTankAction() {
        return emptyProcessedPiecesTank;
    }

    /**
     * Fires the action for emptying the processed pieces tank.
     */
    public void emptyProcessedPiecesTank() {
        emptyProcessedPiecesTank.fire(new ActionInstance(emptyProcessedPiecesTank));
    }

    public ProcessingSpeed getProcessingSpeed() {
        return processingSpeed.get();
    }

    public void setProcessingSpeed(ProcessingSpeed newValue) { // NOCS
        processingSpeed.set(newValue);
    }

    public WritableProperty<ProcessingSpeed> getProcessingSpeedProperty() {
        return processingSpeed;
    }

    public MachineStatus getMachineStatus() {
        return status.get();
    }

    public Property<MachineStatus> getMachineStatusProperty() {
        return status;
    }

    public Action<ActionInstance> getPressStartAction() {
        return pressStart;
    }

    /**
     * Presses the start button for starting process at the machine.
     */
    public void pressStart() {
        pressStart.fire(new ActionInstance(pressStart));
    }

    public Action<ActionInstance> getPressStopAction() {
        return pressStop;
    }

    /**
     * Presses the start button for stopping process at the machine.
     */
    public void pressStop() {
        pressStop.fire(new ActionInstance(pressStop));
    }

    public Event<ThingStatusEvent> getStatusChangedEvent() {
        return statusChanged;
    }
}
