package de.uni_stuttgart.riot.thing.client;

import java.util.ArrayList;
import java.util.Collection;

import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.ThingInfo;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.action.PropertySetAction;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChange;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent;

public class DeviceClient extends ThingClient {
    
    public Collection<ThingInfo> getMyThing(){
        ArrayList<ThingInfo> dummy = new ArrayList<ThingInfo>();
        ArrayList<Action> actions = new ArrayList<Action>();
        ArrayList<Property> properties = new ArrayList<Property>();
        actions.add(new PropertySetAction<String>("name"));
        properties.add(new Property<String>("name","MyAndroid"));
        ThingInfo thingInfo1 = new ThingInfo("Smartphone", properties, actions);
        dummy.add(thingInfo1);
        return dummy;
    }
    
    public Collection<EventInstance> getEventInstances(String thingName){
        ArrayList<EventInstance> dummy = new ArrayList<EventInstance>();
        dummy.add(new PropertyChange<String>(new Property<String>("name","MyIphone")));
        return dummy;
    }
    
    public void fireAction(ThingInfo thing, ActionInstance actionInstance){
    }

}
