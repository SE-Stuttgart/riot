package de.uni_stuttgart.riot.thing.factory.robot;

import java.io.IOException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.clientlibrary.NotFoundException;
import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.factory.ThingStatusEvent;
import de.uni_stuttgart.riot.thing.factory.machine.FullProcessedPiecesTank;
import de.uni_stuttgart.riot.thing.factory.machine.Machine;
import de.uni_stuttgart.riot.thing.factory.machine.OutOfMaterial;

/**
 * Simulates the behavior of a factory robot.
 */
public class RobotSimulator extends Simulator<Robot> {

    private final Logger logger = LoggerFactory.getLogger(RobotSimulator.class);

    /**
     * The thingId to subscribe.
     */
    private int thingToSubscribeId;

    // sends robot to refill material tank
    private EventListener<OutOfMaterial> outOfMaterialListener = new EventListener<OutOfMaterial>() {

        @Override
        public void onFired(Event<? extends OutOfMaterial> event, OutOfMaterial eventInstance) {
            RobotSimulator.this.getThing().pressRefillMaterialTank();
        }
    };

    // sends robot to empty processed pieces tank
    private EventListener<FullProcessedPiecesTank> fullTankListener = new EventListener<FullProcessedPiecesTank>() {

        @Override
        public void onFired(Event<? extends FullProcessedPiecesTank> event, FullProcessedPiecesTank eventInstance) {
            RobotSimulator.this.getThing().pressEmptyProcessedPiecesTank();
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
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {

        if (getThing().getRobotStatus() != RobotStatus.IDLE || !getThing().isPowerOn()) {
            // Ignore if not idle.
            return;
        }
        if (action == getThing().getPressRefillMaterialTankAction()) { // refill material tank

            changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.ON_THE_WAY_FILL_MODUS);
            fireStatusChangedEvent();

            final int stepCount = 10;
            long stepDuration = Robot.ACTION_DURATION / stepCount;
            linearChange(getThing().getTransportDurationProperty(), Robot.ACTION_DURATION, stepDuration, stepCount);

            delay(Robot.ACTION_DURATION, () -> {
                // fire events on the factory machine
                    executeEvent(new EventInstance(getThing().getMaterialTankIsFilledEvent()));

                    // updates robot status
                    changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.IDLE);
                    fireStatusChangedEvent();
                    changePropertyValue(getThing().getTransportDurationProperty(), 0);
                });

        } else if (action == getThing().getPressEmptyProcessedPiecesTankAction()) { // empty processed pieces

            changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.ON_THE_WAY_EMPTY_MODUS);
            fireStatusChangedEvent();

            final int stepCount = 10;
            long stepDuration = Robot.ACTION_DURATION / stepCount;
            linearChange(getThing().getTransportDurationProperty(), Robot.ACTION_DURATION, stepDuration, stepCount);

            delay(Robot.ACTION_DURATION, () -> {

                // fire events on the factory machine
                    executeEvent(new EventInstance(getThing().getProcessedPiecesTankIsEmptyEvent()));
                    // updates robot status
                    changePropertyValue(getThing().getRobotStatusProperty(), RobotStatus.IDLE);
                    fireStatusChangedEvent();
                    changePropertyValue(getThing().getTransportDurationProperty(), 0);
                });
        }
    }

    /**
     * Fires event for a robot status change.
     */
    private void fireStatusChangedEvent() {
        executeEvent(new ThingStatusEvent(getThing().getStatusChangedEvent(), getThing().getRobotStatus().toString()));
    }

    /**
     * Subscribe to events fired by the thing with the given id.
     *
     * @param thingId
     *            the thing to subscribe id
     */
    protected void subscribeEvents(int thingId) {
        if (thingId <= 0) {
            return;
        }
        this.thingToSubscribeId = thingId;
        try {
            Machine machine = super.getOtherThing(this.thingToSubscribeId, Machine.class);

            // updates values of material tank
            machine.getOutOfMaterialEvent().register(outOfMaterialListener);

            // updates value of processed pieces tank
            machine.getFullProcessedPiecesTankEvent().register(fullTankListener);

        } catch (NotFoundException | IOException e) {
            logger.debug("Could not register to machine events");
        }

    }

    /**
     * Unsubscribe events.
     */
    protected void unsubscribeEvents() {
        if (this.thingToSubscribeId <= 0) {
            return;
        }
        try {
            Machine machine = super.getOtherThing(this.thingToSubscribeId, Machine.class);
            machine.getOutOfMaterialEvent().unregister(outOfMaterialListener);
            machine.getFullProcessedPiecesTankEvent().unregister(fullTankListener);

        } catch (NotFoundException | IOException e) {
            logger.debug("Could not unregister from machine events");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.uni_stuttgart.riot.simulation_client.Simulator#stopSimulation()
     */
    @Override
    public void stopSimulation() {
        super.stopSimulation();
        unsubscribeEvents();
    }

}
