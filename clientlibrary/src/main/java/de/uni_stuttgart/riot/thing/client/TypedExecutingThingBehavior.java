package de.uni_stuttgart.riot.thing.client;

import de.uni_stuttgart.riot.thing.Thing;

/**
 * A typed executing thing behavior is responsible for a very specific kind of thing. It only works with one type of thing (any possibly all
 * subtypes).
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The type of the thing.
 */
public abstract class TypedExecutingThingBehavior<T extends Thing> extends ExecutingThingBehavior {

    private final Class<T> thingType;
    private final boolean allowSubtypes;

    /**
     * Creates a new typed ExecutingThingBehavior.
     * 
     * @param thingClient
     *            The {@link ThingClient} responsible for this behavior.
     * @param thingType
     *            The expected type of the thing.
     */
    public TypedExecutingThingBehavior(ThingClient thingClient, Class<T> thingType) {
        this(thingClient, thingType, false);
    }

    /**
     * Creates a new typed ExecutingThingBehavior.
     * 
     * @param thingClient
     *            The {@link ThingClient} responsible for this behavior.
     * @param thingType
     *            The expected type of the thing.
     * @param allowSubtypes
     *            Whether subtypes of <tt>thingType</tt> are allowed.
     */
    public TypedExecutingThingBehavior(ThingClient thingClient, Class<T> thingType, boolean allowSubtypes) {
        super(thingClient);
        if (thingType == null) {
            throw new IllegalArgumentException("thingType must not be null!");
        }
        this.thingType = thingType;
        this.allowSubtypes = allowSubtypes;
    }

    @Override
    protected synchronized void register(Thing newThing) {
        if (!matchesType(newThing)) {
            throw new IllegalArgumentException(getClass().getName() + " can only be used with things of type " + thingType.getName());
        }
        super.register(newThing);
    }

    /**
     * Checks if the given thing matches the expected type for this behavior.
     * 
     * @param thing
     *            The thing to check.
     * @return True if the thing matches the expected type.
     */
    private boolean matchesType(Thing thing) {
        if (thing == null) {
            throw new IllegalArgumentException("thing must not be null!");
        }
        if (allowSubtypes) {
            return thingType.isInstance(thing);
        } else {
            return thingType == thing.getClass();
        }
    }

    @Override
    public T getThing() {
        return thingType.cast(super.getThing());
    }

}
