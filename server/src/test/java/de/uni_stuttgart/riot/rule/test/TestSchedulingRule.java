package de.uni_stuttgart.riot.rule.test;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.rule.ReferenceParameter;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.thing.PropertyListener;
import de.uni_stuttgart.riot.thing.test.TestThing;

/**
 * A test rule that operates on a {@link TestThing}. It ensures that the {@link TestThing#getPercentProperty()} reflects the percentage of
 * {@link TestThing#getIntProperty()} to {@link TestThing#getLongProperty()}. However, the value is adjusted with a delay of 10 ms.
 * 
 * @author Philipp Keck
 */
public class TestSchedulingRule extends Rule {

    private static final long DELAY_MILLIS = 10;

    ReferenceParameter<TestThing> thing = newReferenceParameter("thing", TestThing.class);
    private final PropertyListener<Integer> intListener = onPropertyChange(this::recalculate);
    private final PropertyListener<Long> longListener = onPropertyChange(this::recalculate);

    @Override
    protected void initialize() throws ResolveReferenceException {
        TestThing targetThing = thing.getTarget();
        targetThing.getIntProperty().register(intListener);
        targetThing.getLongProperty().register(longListener);
        targetThing.setPercent((double) targetThing.getInt() / (double) targetThing.getLong());
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        TestThing targetThing = thing.getTarget();
        targetThing.getIntProperty().unregister(intListener);
        targetThing.getLongProperty().unregister(longListener);
    }

    /**
     * Waits 10 milliseconds and then recalculates the value for the {@link TestThing#getPercentProperty()}.
     */
    private void recalculate() {
        delay(DELAY_MILLIS, () -> {
            TestThing targetThing = thing.getTarget();
            targetThing.setPercent((double) targetThing.getInt() / (double) targetThing.getLong());
        });
    }

}
