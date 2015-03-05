package de.uni_stuttgart.riot.thing;


/**
 * Thing for a Android Device that is used to monitor all Things a user has access to.
 *
 */
public class AndroidThing extends Thing {

    /**
     * Constructor.
     * 
     * @param name
     *            name of the Device
     * @param behavior
     *            its behavior (normaly {@link DeviceBehavior})
     */
    public AndroidThing(String name, ThingBehavior behavior) {
        super(name, behavior);
    }

}
