package de.uni_stuttgart.riot.rule.factory;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.rule.IllegalRuleConfigurationException;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleParameter;
import de.uni_stuttgart.riot.rule.ThingParameter;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.factory.machine.Machine;
import de.uni_stuttgart.riot.thing.factory.machine.OutOfMaterial;
import de.uni_stuttgart.riot.thing.factory.robot.Robot;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A rule for the factory machine. It automatically starts the robot to refill the material tank of the machine, after a given delay.
 */
public class RefillMaterialRule extends Rule {

    @Parameter(ui = UIHint.ThingDropDown.class)
    private ThingParameter<Machine> machine;

    @Parameter(ui = UIHint.ThingDropDown.class)
    private ThingParameter<Robot> robot;

    @Parameter(ui = UIHint.EditNumber.class, min = 0)
    private RuleParameter<Integer> delay;

    @Parameter(ui = UIHint.IntegralSlider.class, min = 0, max = Machine.MATERIAL_TANK_SIZE)
    private RuleParameter<Integer> refillMaterialAmount;

    // machine events
    private EventListener<OutOfMaterial> outOfMAterialListener = onEvent(this::onMachineOutOfMaterial);

    // robot events
    private EventListener<EventInstance> materialTankIsFilledListener = onEvent(this::onRobotFilledMaterialTank);

    @Override
    protected void initialize() throws ResolveReferenceException, IllegalRuleConfigurationException {
        Machine factoryMachine = machine.getTarget();
        factoryMachine.getOutOfMaterialEvent().register(outOfMAterialListener);

        Robot factoryRobot = robot.getTarget();
        factoryRobot.getMaterialTankIsFilledEvent().register(materialTankIsFilledListener);
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        Machine factoryMachine = machine.getTarget();
        factoryMachine.getOutOfMaterialEvent().unregister(outOfMAterialListener);

        Robot factoryRobot = robot.getTarget();
        factoryRobot.getMaterialTankIsFilledEvent().unregister(materialTankIsFilledListener);
    }

    private void onMachineOutOfMaterial() {
        delay(delay.get(), () -> {
            robot.getTarget().pressRefillMaterialTank();
        });
    }

    private void onRobotFilledMaterialTank() {
        delay(delay.get(), () -> {
            machine.getTarget().refillMaterial(refillMaterialAmount.get());
        });
    }
}
