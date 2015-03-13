package de.uni_stuttgart.riot.rule.test;

import de.uni_stuttgart.riot.references.ResolveReferenceException;
import de.uni_stuttgart.riot.rule.ReferenceParameter;
import de.uni_stuttgart.riot.rule.Rule;
import de.uni_stuttgart.riot.rule.RuleParameter;
import de.uni_stuttgart.riot.thing.PropertyListener;
import de.uni_stuttgart.riot.thing.test.TestThing;

/**
 * A test rule that operates on {@link TestThing}s. It observes the value of the {@link TestThing#getIntProperty()} of a given input thing
 * and adds a constant value {@link #intAdd} and writes the result to the output thing.
 * 
 * @author Philipp Keck
 */
public class TestAdditionRule extends Rule {

    private final RuleParameter<Integer> intAdd = newParameter("intAdd", Integer.class);
    private final ReferenceParameter<TestThing> inputThing = newReferenceParameter("inputThing", TestThing.class);
    private final ReferenceParameter<TestThing> outputThing = newReferenceParameter("outputThing", TestThing.class);

    private final PropertyListener<Integer> listener = onPropertyChange(this::recalculate);

    @Override
    protected void initialize() throws ResolveReferenceException {
        TestThing input = inputThing.getTarget();
        input.getIntProperty().register(listener);
        recalculate(input.getInt());
    }

    @Override
    protected void shutdown() throws ResolveReferenceException {
        inputThing.getTarget().getIntProperty().unregister(listener);
    }

    /**
     * Performs the actual addition.
     * 
     * @param inputValue
     *            The current value of the input property.
     * @throws ResolveReferenceException
     *             When resolving the target fails.
     */
    private void recalculate(int inputValue) throws ResolveReferenceException {
        outputThing.getTarget().setInt(inputThing.getTarget().getInt() + intAdd.get());
    }

}
