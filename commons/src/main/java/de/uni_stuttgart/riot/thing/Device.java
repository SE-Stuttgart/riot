package de.uni_stuttgart.riot.thing;

/**
 * A device is a thing that is used by the user to access the RIoT system, that is, it has a user interface that is able to display the
 * system status, login information, other things, etc.
 * 
 * @author Philipp Keck
 */
public class Device extends Thing {

    /**
     * Creates a new device.
     * 
     * @param name
     *            The name of the device.
     * @param behavior
     *            Its behavior.
     */
    public Device(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

}
