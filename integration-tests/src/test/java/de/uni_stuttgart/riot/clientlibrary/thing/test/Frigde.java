package de.uni_stuttgart.riot.clientlibrary.thing.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.client.ClientThing;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstanceVisitor;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.Event;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent;

/**
 * The Class represent the concrete Thing "Frigde".
 */
public class Frigde extends ClientThing {

    public Property<Boolean> state = new Property<Boolean>("State", false);
    public Property<Integer> temp = new Property<Integer>("Temp", 4);

    public PropertyChangeEvent<Boolean> stateChangeEvent = new PropertyChangeEvent<Boolean>("State");
    public PropertyChangeEvent<Integer> tempChangeEvent = new PropertyChangeEvent<Integer>("Temp");

    public PropertySetAction<Boolean> setStateAction = new PropertySetAction<Boolean>("State");
    public PropertySetAction<Integer> setTempAction = new PropertySetAction<Integer>("Temp");

    public Frigde(ThingClient thingClient, String name, long delay) {
        super(thingClient, name, delay);
    }

    @Override
    protected void handleActionInstances(ActionInstance actionInstance) {
        // System.out.println(actionInstance);
        actionInstance.accept(new ActionInstanceVisitor() {
            public <T> void handle(PropertySetActionInstance<T> propertySetActionInstance) {
                if (propertySetActionInstance.getProperty().getName().equals("State")) {
                    Boolean newVal = (Boolean) propertySetActionInstance.getProperty().getValue();
                    Frigde.this.state.setValue(newVal);
                    try {
                        Frigde.this.notifyEvent(stateChangeEvent.createInstance(newVal, Frigde.this.getId()));
                    } catch (RequestException e) {
                        // FIXME Handle exception: retry?
                    }
                } else if (propertySetActionInstance.getProperty().getName().equals("Temp")) {
                    Integer newVal = (Integer) propertySetActionInstance.getProperty().getValue();
                    Frigde.this.temp.setValue(newVal);
                    try {
                        Frigde.this.notifyEvent(tempChangeEvent.createInstance(newVal, Frigde.this.getId()));
                    } catch (RequestException e) {
                        // FIXME Handle exception: retry?
                    }
                }
            }
        });
    }

    @Override
    protected Collection<Event> initEvents() {
        ArrayList<Event> events = new ArrayList<Event>();
        events.add(stateChangeEvent);
        events.add(tempChangeEvent);
        return events;
    }

    @Override
    protected Collection<Action> initActions() {
        ArrayList<Action> actions = new ArrayList<Action>();
        actions.add(setStateAction);
        actions.add(setTempAction);
        return actions;
    }

    @Override
    protected Map<String, Property> initProperties() {
        HashMap<String, Property> properties = new HashMap<String, Property>();
        properties.put("State", state);
        properties.put("Temp", temp);
        return properties;
    }
}
