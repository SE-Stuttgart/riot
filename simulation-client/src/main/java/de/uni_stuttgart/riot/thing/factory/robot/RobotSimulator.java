package de.uni_stuttgart.riot.thing.factory.robot;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.ReferencePropertyListener;
import de.uni_stuttgart.riot.thing.factory.ThingStatusEvent;
import de.uni_stuttgart.riot.thing.factory.machine.FullProcessedPiecesTank;
import de.uni_stuttgart.riot.thing.factory.machine.Machine;
import de.uni_stuttgart.riot.thing.factory.machine.OutOfMaterial;

/**
 * Simulates the behavior of a factory robot.
 */
public class RobotSimulator extends Simulator<Robot> {

    // sends robot to refill material tank
    private EventListener<OutOfMaterial> outOfMaterialListener = (event, instance) -> {
        getThing().pressRefillMaterialTank();
    };

    // sends robot to empty processed pieces tank
    private EventListener<FullProcessedPiecesTank> fullTankListener = (event, instance) -> {
        getThing().pressEmptyProcessedPiecesTank();
    };

    // makes sure the above listeners are registered on the target machine
    private ReferencePropertyListener<Machine> machineChangedListener = new ReferencePropertyListener<Machine>(Machine.class) {
        public void onChange(Property<Long> property, Machine oldValue, Machine newValue) {
            if (oldValue != null) {
                unregisterFromMachine(oldValue);
            }
            if (newValue != null) {
                registerToMachine(newValue);
            }
        }
    };

    /**
     * Creates a new instance.
     * 
     * @param thing
     *            The factory robot.
     * @param scheduler
     *            The scheduler.
     */
    public RobotSimulator(Robot thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    @Override
    public void startSimulation() {
        super.startSimulation();
        if (getThing().hasMachine()) {
            try {
                registerToMachine(getThing().getMachine());
            } catch (ResolveReferenceException e) {
                executeEvent(getThing().getMachineMissingEvent());
            }
        }
        getThing().getMachineProperty().register(machineChangedListener);
    }

    @Override
    public void stopSimulation() {
        getThing().getMachineProperty().unregister(machineChangedListener);
        if (getThing().hasMachine()) {
            try {
                unregisterFromMachine(getThing().getMachine());
            } catch (ResolveReferenceException e) {
                // Ignore
            }
        }
        super.stopSimulation();
    }

    private void registerToMachine(Machine machine) {
        machine.getOutOfMaterialEvent().register(outOfMaterialListener);
        machine.getFullProcessedPiecesTankEvent().register(fullTankListener);
    }

    private void unregisterFromMachine(Machine machine) {
        machine.getOutOfMaterialEvent().unregister(outOfMaterialListener);
        machine.getFullProcessedPiecesTankEvent().unregister(fullTankListener);
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
        if (getThing().getRobotStatus() != RobotStatus.IDLE || !getThing().isPowerOn()) {
            // Ignore if not idle.
            return;
        }
        if (!getThing().hasMachine()) {
            executeEvent(getThing().getMachineMissingEvent());
            return;
        }
        if (action == getThing().getPressRefillMaterialTankAction()) { // refill material tank

            changeRobotStatus(RobotStatus.ON_THE_WAY_FILL_MODUS);

            final int stepCount = 10;
            long stepDuration = Robot.ACTION_DURATION / stepCount;
            linearChange(getThing().getTransportDurationProperty(), Robot.ACTION_DURATION, stepDuration, stepCount);

            delay(Robot.ACTION_DURATION, () -> {

                // Actually fill the tank.
                    try {
                        getThing().getMachine().refillMaterial(Machine.MATERIAL_TANK_SIZE);
                    } catch (ResolveReferenceException e) {
                        executeEvent(getThing().getMachineMissingEvent());
                        return;
                    }

                    // Notify user about the success.
                    executeEvent(getThing().getMaterialTankIsFilledEvent());

                    // Update robot status.
                    changeRobotStatus(RobotStatus.IDLE);
                    changePropertyValue(getThing().getTransportDurationProperty(), 0);
                });

        } else if (action == getThing().getPressEmptyProcessedPiecesTankAction()) { // empty processed pieces

            changeRobotStatus(RobotStatus.ON_THE_WAY_EMPTY_MODUS);

            final int stepCount = 10;
            long stepDuration = Robot.ACTION_DURATION / stepCount;
            linearChange(getThing().getTransportDurationProperty(), Robot.ACTION_DURATION, stepDuration, stepCount);

            delay(Robot.ACTION_DURATION, () -> {

                // Actually empty the tank.
                    try {
                        getThing().getMachine().emptyProcessedPiecesTank();
                    } catch (ResolveReferenceException e) {
                        executeEvent(getThing().getMachineMissingEvent());
                        return;
                    }

                    // Notify user about the success.
                    executeEvent(getThing().getProcessedPiecesTankIsEmptyEvent());

                    // Updates robot status.
                    changeRobotStatus(RobotStatus.IDLE);
                    changePropertyValue(getThing().getTransportDurationProperty(), 0);
                });
        }
    }

    /**
     * Fires event for a robot status change.
     */
    private void changeRobotStatus(RobotStatus newStatus) {
        changePropertyValue(getThing().getRobotStatusProperty(), newStatus);
        executeEvent(new ThingStatusEvent(getThing().getStatusChangedEvent(), newStatus.toString()));
    }

}
