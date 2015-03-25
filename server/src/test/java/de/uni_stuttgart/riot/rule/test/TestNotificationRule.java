package de.uni_stuttgart.riot.rule.test;

import de.uni_stuttgart.riot.notification.NotificationSeverity;
import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.rule.IllegalRuleParameterException;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleNotification;
import de.uni_stuttgart.riot.rule.RuleParameter;
import de.uni_stuttgart.riot.rule.ThingParameter;
import de.uni_stuttgart.riot.thing.Parameter;
import de.uni_stuttgart.riot.thing.PropertyListener;
import de.uni_stuttgart.riot.thing.rest.ThingPermission;
import de.uni_stuttgart.riot.thing.test.TestThing;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A test rule that operates on a {@link TestThing}. It observes the value of the {@link TestThing#getIntProperty()} of the given
 * {@link #thing} and raises a warning notification if the value exceeds the given {@link #limit}. The notification becomes
 * {@link NotificationSeverity#WARNING_IMPORTANT}, if the limit is exceeded by more than factor 2.
 * 
 * @author Philipp Keck
 */
public class TestNotificationRule extends Rule {

    @Parameter(ui = UIHint.EditNumber.class, min = 1)
    private RuleParameter<Integer> limit;

    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.READ)
    private ThingParameter<TestThing> thing;

    private final RuleNotification testWarning = newNotification("testWarning");
    private final PropertyListener<Integer> listener = onPropertyChange(this::checkLimit);

    @Override
    protected void initialize() throws ResolveReferenceException, IllegalRuleParameterException {
        if (limit.get() < 1) {
            throw new IllegalRuleParameterException(limit, "Must be at least 1 for this rule to work properly!");
        }

        TestThing input = thing.getTarget();
        input.getIntProperty().register(listener);
        checkLimit(0, input.getInt());
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        thing.getTarget().getIntProperty().unregister(listener);
    }

    /**
     * Performs the actual check against the limit and fires the notification if required.
     * 
     * @param inputValue
     *            The current value of the input property.
     * @throws ResolveReferenceException
     *             When resolving the target fails.
     */
    private void checkLimit(int oldInt, int newInt) throws ResolveReferenceException {
        int limitValue = this.limit.get();
        if (newInt > limitValue) {
            if (newInt > limitValue * 2 && oldInt <= limitValue * 2) {
                testWarning.create().forThing(thing).severity(NotificationSeverity.WARNING_IMPORTANT).param("value", newInt).sendToThingUsers();
            } else if (oldInt <= limitValue) {
                testWarning.create().forThing(thing).severity(NotificationSeverity.WARNING).param("value", newInt).sendToThingUsers();
            }
        }
    }

}
