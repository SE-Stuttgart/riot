package de.uni_stuttgart.riot.thing.client;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEventInstance;

/**
 * Rest Client for handling Device operations.
 */
public class DeviceClient extends ThingClient {

    /**
     * Constructor.
     * 
     * @param loginClient
     *            the {@link LoginClient} to be used
     */
    public DeviceClient(LoginClient loginClient) {
        super(loginClient);
    }

    /**
     * TODO.
     *
     * @return the my thing
     */
    public Collection<RemoteThing> getMyThing() {
        ArrayList<RemoteThing> dummy = new ArrayList<RemoteThing>();
        ArrayList<Action> actions = new ArrayList<Action>();
        Map<String, Property> properties = new HashMap<String, Property>();
        actions.add(new PropertySetAction<String>("name"));
        properties.put("name", new Property<String>("name", "MyAndroid"));
        RemoteThing thingInfo1 = new RemoteThing("Smartphone", 1);
        thingInfo1.setProperties(properties);
        thingInfo1.setActions(actions);
        dummy.add(thingInfo1);
        return dummy;
    }

    /**
     * Gets the event instances.
     *
     * @param thingName
     *            the thing name
     * @return the event instances
     */
    public Collection<EventInstance> getEventInstances(String thingName) {
        ArrayList<EventInstance> dummy = new ArrayList<EventInstance>();
        dummy.add(new PropertyChangeEventInstance<String>(new Property<String>("name", "MyIphone"), 1, new Timestamp(0)));
        return dummy;
    }

    /**
     * Fires the action Instance on the given thing..
     * 
     * @param thing
     *            thing that should execute the action instances.
     * @param actionInstance
     *            action instances to be fired.
     */
    public void fireAction(RemoteThing thing, ActionInstance actionInstance) {
        // FIXME
    }
}
