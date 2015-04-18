package de.uni_stuttgart.riot.rule;

import java.util.Calendar;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.house.AlarmClock;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.house.light.DimmableLight;
import de.uni_stuttgart.riot.thing.house.roller_shutter.RollerShutter;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Rule that lets the "house" wake up when you do.
 *
 */
public class GoodMorningRule extends Rule {

    private static final double DIM_LEVEL = 0.5;

    private static final int END_OF_RULE_TIME = 24;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.READ)
    private ThingParameter<AlarmClock> alarmClock;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.CONTROL)
    private ThingParameter<RollerShutter> rollerShutter;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.CONTROL)
    private ThingParameter<CoffeeMachine> coffeeMachine;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.CONTROL)
    private ThingParameter<DimmableLight> dimmableLight;

    private final EventListener<EventInstance> listener = onEvent(() -> {
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) <= END_OF_RULE_TIME) {
            GoodMorningRule.this.doGoodMorningActions();
        }
    });

    @Override
    protected void initialize() throws ResolveReferenceException, IllegalRuleConfigurationException {
        AlarmClock clock = alarmClock.getTarget();
        clock.getAlarmEvent().register(listener);
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        this.alarmClock.getTarget().getAlarmEvent().unregister(listener);
    }

    private void doGoodMorningActions() throws ResolveReferenceException {
        this.openRollerShutter();
        this.turnLightOn();
        this.makeCoffee();
    }

    private void makeCoffee() throws ResolveReferenceException {
        CoffeeMachine cM = this.coffeeMachine.getTarget();
        cM.setPowerSwitch(true);
        cM.pressStart();
    }

    private void turnLightOn() throws ResolveReferenceException {
        DimmableLight dL = this.dimmableLight.getTarget();
        dL.setDimLevel(DIM_LEVEL);
        dL.setOn(true);
    }

    private void openRollerShutter() throws ResolveReferenceException {
        RollerShutter rS = this.rollerShutter.getTarget();
        rS.adjustShutter(0.0);
    }

}
