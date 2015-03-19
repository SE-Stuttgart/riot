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
     * @param thingType
     *            The type of the thing.
     * @return The new instance.
     */
    B newBehavior(Class<? extends Thing> thingType);

    /**
     * To avoid duplicate instantiation of the same thing, this method retrieves an existing behavior for a thing by its ID.
     * 
     * @param thingID
     *            The ID of the thing.
     * @return The behavior of the thing if it is already there or <tt>null</tt> if it must be freshly created.
     */
    B existingBehavior(long thingID);

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
