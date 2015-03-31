package de.uni_stuttgart.riot.rule;

import java.util.Calendar;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.EventListener;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.house.AdjustShutterPostion;
import de.uni_stuttgart.riot.thing.house.AlarmClock;
import de.uni_stuttgart.riot.thing.house.RollerShutter;
import de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine;
import de.uni_stuttgart.riot.thing.house.light.AdjustDimmLevel;
import de.uni_stuttgart.riot.thing.house.light.DimmableLight;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * Rule that lets the "house" wake up when you do.
 *
 */
public class GoodMorningRule extends Rule {

    private static final int END_OF_RULE_TIME = 11;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.READ)
    private ThingParameter<AlarmClock> alarmClock;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.CONTROL)
    private ThingParameter<RollerShutter> rollerShutter;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.CONTROL)
    private ThingParameter<CoffeeMachine> coffeeMachine;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.CONTROL)
    private ThingParameter<DimmableLight> dimmableLight;

    private final EventListener<EventInstance> listener = new EventListener<EventInstance>() {
        @Override
        public void onFired(Event<? extends EventInstance> event, EventInstance eventInstance) {
            Calendar cal = Calendar.getInstance();
            if (cal.get(Calendar.HOUR_OF_DAY) <= END_OF_RULE_TIME) {
                GoodMorningRule.this.doGoodMorningActions();
            }
        }
    };

    @Override
    protected void initialize() throws ResolveReferenceException, IllegalRuleConfigurationException {
        AlarmClock clock = alarmClock.getTarget();
        clock.getAlarmEvent().register(listener);
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        this.alarmClock.getTarget().getAlarmEvent().unregister(listener);
    }

    private void doGoodMorningActions() {
        try {
            this.openRollerShutter();
            this.trunLightOn();
            this.makeCoffee();
        } catch (ResolveReferenceException e) {
            errorOccured(e);
        }
    }

    private void makeCoffee() throws ResolveReferenceException {
        CoffeeMachine cM = this.coffeeMachine.getTarget();
        cM.getPressStartAction().fire(new ActionInstance(cM.getPressStartAction()));
    }

    private void trunLightOn() throws ResolveReferenceException {
        DimmableLight dL = this.dimmableLight.getTarget();
        dL.getAdjustDimm().fire(new AdjustDimmLevel(dL.getAdjustDimm(), 0.0));
    }

    private void openRollerShutter() throws ResolveReferenceException {
        RollerShutter rS = this.rollerShutter.getTarget();
        rS.getAdjustAction().fire(new AdjustShutterPostion(rS.getAdjustAction(), 0.0));
    }

}
