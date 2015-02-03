package de.uni_stuttgart.riot.thing.client;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.commons.Property;
import de.uni_stuttgart.riot.thing.commons.RemoteThing;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.Action;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;
import de.uni_stuttgart.riot.thing.commons.event.Event;
import de.uni_stuttgart.riot.thing.commons.event.EventInstance;
import de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEventInstance;

public abstract class ClientThing extends Thing implements Runnable {

    private final long delay;
    private boolean started;
    private final ThingClient thingClient;

    protected abstract void handleActionInstances(ActionInstance actionInstance);
    protected abstract Collection<Event> initEvents();
    protected abstract Collection<Action> initActions();
    protected abstract Map<String,Property> initProperties();


    public void register() throws RequestException{
        RemoteThing r = new RemoteThing(this.getName(), 0);
        Collection<Action> actions = this.initActions();
        r.setActions(actions);
        this.setActions(actions);
        Collection<Event> events = this.initEvents();
        r.setEvents(events);
        this.setEvents(events);
        Map<String, Property> properties = this.initProperties();
        r.setProperties(properties);
        this.setProperties(properties);
        r = this.thingClient.registerThing(r);
        this.setId(r.getId());
    }
    
    
    public void start(){
        new Thread(this).start();
    }
    
    public void deRegister() throws RequestException{
        this.thingClient.deregisterThing(this.getId());
    }

    public ClientThing(ThingClient thingClient, String name, long delay) {
        super(name);
        this.delay = delay;
        this.started = true;
        this.thingClient = thingClient;
    }

    @Override
    public void run() {
        try {
            while (started) {
                System.out.println("TEST");
                for (ActionInstance actionInstance : this.getActionInstances()) {
                    this.handleActionInstances(actionInstance);
                }
                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    private Queue<ActionInstance> getActionInstances() throws RequestException {
            return this.thingClient.getActionInstances(this.getId());
    }

    public void stop() {
        this.started = false;
    }
    
    protected void notifyEvent(EventInstance instance) throws RequestException {
        this.thingClient.notifyEvent(instance);
    }

}
