package de.uni_stuttgart.riot.rule.factory;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.rule.IllegalRuleConfigurationException;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleParameter;
import de.uni_stuttgart.riot.rule.ThingParameter;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.factory.machine.FullProcessedPiecesTank;
import de.uni_stuttgart.riot.thing.factory.machine.Machine;
import de.uni_stuttgart.riot.thing.factory.robot.Robot;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A rule for the factory machine. It automatically starts the robot to empty the processed pieces tank of the machine, after a given delay.
 */
public class EmptyFullTankRule extends Rule {

    @Parameter(ui = UIHint.ThingDropDown.class)
    private ThingParameter<Machine> machine;

    @Parameter(ui = UIHint.ThingDropDown.class)
    private ThingParameter<Robot> robot;

    @Parameter(ui = UIHint.EditNumber.class, min = 0)
    private RuleParameter<Integer> delay;

    // machine events
    private EventListener<FullProcessedPiecesTank> fullProcessedPiecesTankListener = onEvent(this::onMachineFullProcessedPiecesTank);

    // robot events
    private EventListener<EventInstance> processedPiecesTankIsEmptyListener = onEvent(this::onRobotEmptiedPiecesTank);

    @Override
    protected void initialize() throws ResolveReferenceException, IllegalRuleConfigurationException {
        Machine factoryMachine = machine.getTarget();
        factoryMachine.getFullProcessedPiecesTankEvent().register(fullProcessedPiecesTankListener);

        Robot factoryRobot = robot.getTarget();
        factoryRobot.getProcessedPiecesTankIsEmptyEvent().register(processedPiecesTankIsEmptyListener);
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        Machine factoryMachine = machine.getTarget();
        factoryMachine.getFullProcessedPiecesTankEvent().unregister(fullProcessedPiecesTankListener);

        Robot factoryRobot = robot.getTarget();
        factoryRobot.getProcessedPiecesTankIsEmptyEvent().unregister(processedPiecesTankIsEmptyListener);
    }

    private void onMachineFullProcessedPiecesTank() {
        delay(delay.get(), () -> {
            robot.getTarget().pressEmptyProcessedPiecesTank();
        });
    }

    private void onRobotEmptiedPiecesTank() {
        delay(delay.get(), () -> {
            machine.getTarget().emptyProcessedPiecesTank();
        });
    }
}
