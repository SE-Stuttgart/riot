package de.uni_stuttgart.riot.thing.test;

import de.uni_stuttgart.riot.thing.Action;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehavior;
import de.uni_stuttgart.riot.thing.WritableProperty;
import de.uni_stuttgart.riot.thing.ui.UIHint;

/**
 * A thing type with a couple of actions/events/properties for testing purposes.
 * 
 * @author Philipp Keck
 */
public class TestThing extends Thing {

    private final WritableProperty<Integer> intProperty = newWritableProperty("int", Integer.class, 42, UIHint.integralSlider(0, 10000));
    private final WritableProperty<Long> longProperty = newWritableProperty("long", Long.class, 4242L, UIHint.editNumber());
    private final WritableProperty<Double> percentProperty = newWritableProperty("percent", Double.class, 0.5, UIHint.percentageSlider());
    private final Property<String> readonlyStringProperty = newProperty("readonlyString", String.class, "InitialString", UIHint.editText());

    private final Event<EventInstance> simpleEvent = newEvent("simpleEvent");
    private final Event<TestEventInstance> parameterizedEvent = newEvent("parameterizedEvent", TestEventInstance.class);

    private final Action<ActionInstance> simpleAction = newAction("simpleAction");
    private final Action<TestActionInstance> parameterizedAction = newAction("parameterizedAction", TestActionInstance.class);

    public TestThing(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

    public int getInt() {
        return intProperty.getValue();
    }

    public void setInt(int newValue) {
        intProperty.set(newValue);
    }

    public long getLong() {
        return longProperty.getValue();
    }

    public void setLong(long newValue) {
        longProperty.set(newValue);
    }

    public double getPercent() {
        return percentProperty.getValue();
    }

    public void setPercent(double newValue) {
        percentProperty.set(newValue);
    }

    public String getReadonlyString() {
        return readonlyStringProperty.getValue();
    }

    public WritableProperty<Integer> getIntProperty() {
        return intProperty;
    }

    public WritableProperty<Long> getLongProperty() {
        return longProperty;
    }

    public WritableProperty<Double> getPercentProperty() {
        return percentProperty;
    }

    public Property<String> getReadonlyStringProperty() {
        return readonlyStringProperty;
    }

    public Event<EventInstance> getSimpleEvent() {
        return simpleEvent;
    }

    public Event<TestEventInstance> getParameterizedEvent() {
        return parameterizedEvent;
    }

    public Action<ActionInstance> getSimpleAction() {
        return simpleAction;
    }

    public Action<TestActionInstance> getParameterizedAction() {
        return parameterizedAction;
    }

}
