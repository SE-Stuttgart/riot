package de.uni_stuttgart.riot.thing;

/**
 * A factory for {@link ThingBehavior} instances that is additionally called for every thing after it has been initialized.
 * 
 * @param <B>
 *            The type of behaviors that this factory creates.
 * @author Philipp Keck
 */
public interface ThingBehaviorFactory<B extends ThingBehavior> {

    /**
     * Called to produce a new behavior instance.
     * 
     * @param thingID
     *            The thing's ID (if known already, 0 otherwise).
     * @param thingName
     *            The name of the thing (if already known, <tt>null</tt> otherwise).
     * @param thingType
     *            The type of the thing.
     * @return The new instance.
     */
    B newBehavior(long thingID, String thingName, Class<? extends Thing> thingType);

    /**
     * Called after a thing has successfully been initialized.
     * 
     * @param thing
     *            The new thing.
     * @param behavior
     *            Its behavior as returned by {@link #newBehavior(long, String, Class)}.
     */
    void onThingCreated(Thing thing, B behavior);

}