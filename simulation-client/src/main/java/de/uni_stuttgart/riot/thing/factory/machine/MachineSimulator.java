package de.uni_stuttgart.riot.thing.factory.machine;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import de.uni_stuttgart.riot.simulation_client.Simulator;
import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;

//CHECKSTYLE: MagicNumber OFF
/**
 * Simulates the behavior of a factory machine.
 */
public class MachineSimulator extends Simulator<Machine> {

    private static final long BASE_ONE_PIECE_PROCESSING_DURATION = 20000;

    private ScheduledFuture<?> materialFuture;
    private ScheduledFuture<?> processedPiecesFuture;

    /**
     * Creates a new instance.
     * 
     * @param thing
     *            The factory machine.
     * @param scheduler
     *            The scheduler.
     */
    public MachineSimulator(Machine thing, ScheduledThreadPoolExecutor scheduler) {
        super(thing, scheduler);
    }

    private void fireOutOfMaterial() {
        executeEvent(new OutOfMaterial(getThing().getOutOfMaterialEvent(), getThing().getMaterialTank()));
    }

    private void fireFullProcessedPiecesTank() {
        executeEvent(new FullProcessedPiecesTank(getThing().getFullProcessedPiecesTankEvent(), getThing().getProcessedPiecesTank()));
    }

    @Override
    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {

        if (action == getThing().getRefillMaterialAction()) { // refill material tank
            int refillAmount = ((RefillMaterial) actionInstance).getAmount();
            int newValue = Math.min(Machine.MATERIAL_TANK_SIZE, getThing().getMaterialTank() + refillAmount);
            changePropertyValue(getThing().getMaterialTankProperty(), newValue);
            if (getThing().getMachineStatus() == MachineStatus.OUT_OF_MATERIAL) {
                changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.WAITING);
            }

        } else if (action == getThing().getEmptyProcessedPiecesTankAction()) { // empty processed pieces
            changePropertyValue(getThing().getProcessedPiecesTankProperty(), 0);
            if (getThing().getMachineStatus() == MachineStatus.FULL) {
                changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.WAITING);
            }

        } else if (action == getThing().getPressStartAction()) { // pressed start
            startPressed();
        } else if (action == getThing().getPressStopAction()) { // pressed stop
            stopPressed();
        }
    }

    // CHECKSTYLE: OFF
    /**
     * Starts processing at the factory machine.
     */
    private synchronized void startPressed() {
        if (getThing().getMachineStatus() != MachineStatus.WAITING || !getThing().isPowerOn()) {
            // Ignore if not waiting.
            return;
        }

        // Check if we have enough material.
        int materialConsumption = getThing().getProcessingSpeed().getMaterialConsumption();
        if (getThing().getMaterialTank() < materialConsumption) {
            changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.OUT_OF_MATERIAL);
            fireOutOfMaterial();
            return;
        }

        // Check if processed pieces tank is full.
        if (getThing().getProcessedPiecesTank() == Machine.PROCESSED_PIECES_TANK_SIZE) {
            changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.FULL);
            fireFullProcessedPiecesTank();
            return;
        }

        // Start the machine.
        changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.RUNNING);

        // calculates how many pieces can be produced based on material and processed pieces tank
        int nPiecesToProduce = getThing().getMaterialTank();
        if (nPiecesToProduce > Machine.PROCESSED_PIECES_TANK_SIZE - getThing().getProcessedPiecesTank()) {
            nPiecesToProduce = Machine.PROCESSED_PIECES_TANK_SIZE - getThing().getProcessedPiecesTank();
        }

        final int stepCount = nPiecesToProduce;
        long stepDuration = BASE_ONE_PIECE_PROCESSING_DURATION / stepCount / materialConsumption;

        if (materialFuture != null) {
            materialFuture.cancel(false);
        }
        materialFuture = linearChange(getThing().getMaterialTankProperty(), getThing().getMaterialTank() - nPiecesToProduce, stepDuration, stepCount);

        if (processedPiecesFuture != null) {
            processedPiecesFuture.cancel(false);
        }
        processedPiecesFuture = linearChange(getThing().getProcessedPiecesTankProperty(), getThing().getProcessedPiecesTank() + nPiecesToProduce, stepDuration, stepCount);

        delay(stepCount * stepDuration + 1000, () -> {
            // Processing material is done.
                if (getThing().getMaterialTank() == 0) {
                    changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.OUT_OF_MATERIAL);
                    fireOutOfMaterial();
                }
                // Check if processed pieces tank is full.
                if (getThing().getProcessedPiecesTank() >= Machine.PROCESSED_PIECES_TANK_SIZE) {
                    changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.FULL);
                    fireFullProcessedPiecesTank();
                }

                if (getThing().getMachineStatus() == MachineStatus.RUNNING) {
                    changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.WAITING);
                }
                materialFuture = null;
                processedPiecesFuture = null;

            });

    }

    /**
     * Stops processing at the factory machine.
     */
    private synchronized void stopPressed() {
        if (getThing().getMachineStatus() != MachineStatus.RUNNING || !getThing().isPowerOn()) {
            // Ignore if not off.
            return;
        }
        if (processedPiecesFuture != null) {
            processedPiecesFuture.cancel(false);
            processedPiecesFuture = null;
        }

        if (materialFuture != null) {
            materialFuture.cancel(false);
            materialFuture = null;
        }
        // Stops the machine.
        changePropertyValue(getThing().getMachineStatusProperty(), MachineStatus.WAITING);
    }
}
