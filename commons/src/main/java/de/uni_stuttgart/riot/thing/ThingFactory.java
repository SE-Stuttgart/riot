package de.uni_stuttgart.riot.thing;

/**
 * Contains methods for creating new things.
 * 
 * @author Philipp Keck
 */
public final class ThingFactory {

    /**
     * Cannot instantiate this class.
     */
    private ThingFactory() {
    }

    /**
     * Resolves the given type of a Thing.
     * 
     * @param typeName
     *            The name of the thing type. An IllegalArgumentException will be thrown if this is not the fully qualified class name of a
     *            class that inherits Thing.
     * @return A {@link Class} object representing that type.
     */
    public static Class<? extends Thing> resolveType(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            throw new IllegalArgumentException("typeName must not be empty!");
        }

        Class<?> thingClass;
        try {
            thingClass = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The specified Thing type " + typeName + " was not found!");
        }
        if (!Thing.class.isAssignableFrom(thingClass)) {
            throw new IllegalArgumentException("The specified type " + typeName + " is not a subclass of Thing!");
        }

        @SuppressWarnings("unchecked")
        Class<? extends Thing> result = (Class<? extends Thing>) thingClass;
        return result;
    }

    /**
     * Constructs a thing of the given type.
     * 
     * @param <T>
     *            The type of the thing.
     * @param thingType
     *            The type of the thing.
     * @param thingID
     *            The ID of the new Thing.
     * @param thingName
     *            The name of the new Thing.
     * @param behavior
     *            The behavior of the new thing.
     * @return The new thing.
     */
    public static <T extends Thing> T create(Class<T> thingType, long thingID, String thingName, ThingBehavior behavior) {
        return thingType.cast(create(thingType.getName(), thingID, thingName, behavior));
    }

    /**
     * Constructs a thing of the given type.
     * 
     * @param typeName
     *            The name of the thing type. An IllegalArgumentException will be thrown if this is not the fully qualified class name of a
     *            class that inherits Thing or if there is a problem with the Thing constructors.
     * @param thingID
     *            The ID of the new Thing.
     * @param thingName
     *            The name of the new Thing.
     * @param behavior
     *            The behavior of the new thing.
     * @return The new thing.
     */
    public static Thing create(String typeName, long thingID, String thingName, ThingBehavior behavior) {
        Class<? extends Thing> thingClass = resolveType(typeName);
        Thing thing;
        try {
            thing = thingClass.getConstructor(String.class, ThingBehavior.class).newInstance(thingName, behavior);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class " + typeName + " must have a constructor that accepts the thing's name and behavior as the only two parameters.");
        } catch (Exception e) {
            throw new RuntimeException("Could not call the constructor of " + typeName, e);
        }

        thing.setId(thingID);
        return thing;
    }

    /**
     * Constructs a thing of the given type.
     * 
     * @param <B>
     *            The type of the thing's behavior.
     * @param <T>
     *            The type of the thing.
     * @param thingType
     *            The type of the thing.
     * @param thingID
     *            The ID of the new Thing.
     * @param thingName
     *            The name of the new Thing.
     * @param behaviorFactory
     *            A factory that will produce the behavior for the new thing and that will be notified of the new thing.
     * @return The new thing.
     */
    public static <B extends ThingBehavior, T extends Thing> T create(Class<T> thingType, long thingID, String thingName, ThingBehaviorFactory<B> behaviorFactory) {
        return thingType.cast(create(thingType.getName(), thingID, thingName, behaviorFactory).getThing());
    }

    /**
     * Constructs a thing of the given type. If {@link ThingBehaviorFactory#existingBehavior(long)} returns an existing behavior, this one
     * will be used and the according thing will be returned.
     * 
     * @param <B>
     *            The type of the thing's behavior.
     * @param typeName
     *            The name of the thing type. An IllegalArgumentException will be thrown if this is not the fully qualified class name of a
     *            class that inherits Thing or if there is a problem with the Thing constructors.
     * @param thingID
     *            The ID of the new Thing.
     * @param thingName
     *            The name of the new Thing.
     * @param behaviorFactory
     *            A factory that will produce the behavior for the new thing and that will be notified of the new thing.
     * @return The new thing.
     */
    public static <B extends ThingBehavior> B create(String typeName, long thingID, String thingName, ThingBehaviorFactory<B> behaviorFactory) {
        Class<? extends Thing> thingClass = resolveType(typeName);
        if (thingID > 0) {
            B oldBehavior = behaviorFactory.existingBehavior(thingID);
            if (oldBehavior != null) {
                if (!thingClass.isInstance(oldBehavior.getThing())) {
                    throw new IllegalStateException("The existing thing " + thingID + " is of type " + oldBehavior.getThing().getClass() + " instead of " + thingClass);
                }
                oldBehavior.getThing().setName(thingName);
                return oldBehavior;
            }
        }

        B behavior = behaviorFactory.newBehavior(thingID, thingName, thingClass);
        Thing thing;
        try {
            thing = thingClass.getConstructor(String.class, ThingBehavior.class).newInstance(thingName, behavior);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The class " + typeName + " must have a constructor that accepts the thing's name and behavior as the only two parameters.");
        } catch (Exception e) {
            throw new RuntimeException("Could not call the constructor of " + typeName, e);
        }

        thing.setId(thingID);
        behaviorFactory.onThingCreated(thing, behavior);
        return behavior;
    }
}
