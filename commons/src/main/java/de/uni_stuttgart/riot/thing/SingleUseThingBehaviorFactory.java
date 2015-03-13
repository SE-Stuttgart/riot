package de.uni_stuttgart.riot.thing;

/**
 * This is a simple implementation of a behavior factory that returns a specified behavior once and then becomes invalid.
 * 
 * @author Philipp Keck
 * @param <B>
 *            The type of the thing behavior created by this factory.
 */
public class SingleUseThingBehaviorFactory<B extends ThingBehavior> implements ThingBehaviorFactory<B> {

    private B behavior;

    /**
     * Creates a new single-use behavior factory.
     * 
     * @param behavior
     *            The behavior to be returned.
     */
    public SingleUseThingBehaviorFactory(B behavior) {
        this.behavior = behavior;
    }

    @Override
    public synchronized B newBehavior(long thingID, String thingName, Class<? extends Thing> thingType) {
        if (behavior == null) {
            throw new IllegalStateException("This factory can only be called once!");
        }
        B theBehavior = behavior;
        behavior = null;
        return theBehavior;
    }

    @Override
    public B existingBehavior(long thingID) {
        return null; // No reuse possible.
    }

    @Override
    public void onThingCreated(Thing thing, B b) {
        // Ignore.
    }

}
