package de.uni_stuttgart.riot.rule.house;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.rule.IllegalRuleConfigurationException;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleParameter;
import de.uni_stuttgart.riot.rule.ThingParameter;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.house.coffeemachine.OutOfBeans;
import de.uni_stuttgart.riot.thing.house.coffeemachine.OutOfWater;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * An example rule for the coffee machine. It automatically refills water and beans when the coffee machine complains, after a given delay.
 * 
 * @author Philipp Keck
 */
public class AutoRefillRule extends Rule {

    @Parameter(ui = UIHint.ThingDropDown.class)
    private ThingParameter<CoffeeMachine> machine;

    @Parameter(ui = UIHint.IntegralSlider.class, min = 0, max = CoffeeMachine.BEAN_TANK_SIZE)
    private RuleParameter<Integer> refillBeansAmount;

    @Parameter(ui = UIHint.EditNumber.class, min = 0)
    private RuleParameter<Integer> delay;

    private EventListener<OutOfWater> outOfWaterListener = onEvent(this::onOutOfWater);
    private EventListener<OutOfBeans> outOfBeansListener = onEvent(this::onOutOfBeans);

    @Override
    protected void initialize() throws ResolveReferenceException, IllegalRuleConfigurationException {
        CoffeeMachine coffeeMachine = machine.getTarget();
        coffeeMachine.getOutOfWaterEvent().register(outOfWaterListener);
        coffeeMachine.getOutOfBeansEvent().register(outOfBeansListener);
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        CoffeeMachine coffeeMachine = machine.getTarget();
        coffeeMachine.getOutOfWaterEvent().unregister(outOfWaterListener);
        coffeeMachine.getOutOfBeansEvent().unregister(outOfBeansListener);
    }

    private void onOutOfWater() {
        delay(delay.get(), () -> {
            machine.getTarget().refillWater();
        });
    }

    private void onOutOfBeans() {
        delay(delay.get(), () -> {
            machine.getTarget().refillBeans(refillBeansAmount.get());
        });
    }

}
