package de.uni_stuttgart.riot.thing.client;

import java.util.Queue;

import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.thing.commons.Thing;
import de.uni_stuttgart.riot.thing.commons.action.ActionInstance;

public abstract class ClientThing extends Thing implements Runnable {

    private final long delay;
    private boolean started;
    private final ThingClient thingClient;

    public ClientThing(ThingClient thingClient, String name, long delay) {
        super(name);
        this.delay = delay;
        this.started = true;
        this.thingClient = thingClient;
    }

    protected abstract void initActions();

    protected abstract void initProperties();

    protected abstract void handleActionInstances(ActionInstance actionInstance);

    @Override
    public void run() {
        try {
            while (started) {
                for (ActionInstance actionInstance : this.getActionInstances()) {
                    this.handleActionInstances(actionInstance);
                }
                Thread.sleep(this.delay);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Queue<ActionInstance> getActionInstances() {
        try {
            return this.thingClient.getActionInstances(this.getId());
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void stop() {
        this.started = false;
    }

}
