package de.uni_stuttgart.riot.thing;

import java.util.ArrayList;
import java.util.List;

import de.uni_stuttgart.riot.thing.server.ServerThingBehavior;

/**
 * Utility methods for running tests with things that use the {@link ServerThingBehavior}.
 * 
 * @author Philipp Keck
 */
public abstract class ThingTestUtils {

    /**
     * Cannot instantiate.
     */
    private ThingTestUtils() {
    }

    /**
     * Executes all {@link PropertySetAction}s for the given things (discarding the rest) and repeats this until nothing happens anymore.
     * 
     * @param things
     *            The things.
     */
    public static void flushAllActions(Thing... things) {
        while (flushActions(things)) {
            // Nothing there. Just keep repeating until it returns false.
            things.toString();
        }
    }

    /**
     * Executes all {@link PropertySetAction}s for the given things and discards the rest.
     * 
     * @param things
     *            The things.
     * @return True if some property value changed.
     */
    public static boolean flushActions(Thing... things) {
        boolean changed = false;
        for (Thing thing : things) {
            ServerThingBehavior behavior = (ServerThingBehavior) thing.getBehavior();
            for (ActionInstance instance : behavior.getUpdates().getOutstandingActions()) {
                if (instance instanceof PropertySetAction.Instance) {
                    PropertySetAction<?> setAction = (PropertySetAction<?>) thing.getAction(instance.getName());
                    executePropertySetAction(setAction, setAction.getInstanceType().cast(instance));
                    changed = true;
                }
            }
        }
        return changed;
    }

    /**
     * Executes all {@link PropertySetAction}s locally (as if the thing was being executed on this machine) and returns all other actions.
     * 
     * @param thing
     *            The thing.
     * @return The actions that were executed since the last call.
     */
    public static List<ActionInstance> flushActions(Thing thing) {
        ServerThingBehavior behavior = (ServerThingBehavior) thing.getBehavior();
        List<ActionInstance> result = new ArrayList<>();
        for (ActionInstance instance : behavior.getUpdates().getOutstandingActions()) {
            if (instance instanceof PropertySetAction.Instance) {
                PropertySetAction<?> setAction = (PropertySetAction<?>) thing.getAction(instance.getName());
                executePropertySetAction(setAction, setAction.getInstanceType().cast(instance));
            } else {
                result.add(instance);
            }
        }
        return result;
    }

    /**
     * Executes a property set action.
     * 
     * @param setAction
     *            The set action.
     * @param instance
     *            The action instance.
     */
    public static <V> void executePropertySetAction(PropertySetAction<V> setAction, PropertySetAction.Instance<?> instance) {
        Property<V> property = setAction.getProperty();
        PropertySetAction.Instance<V> typedInstance = setAction.getInstanceType().cast(instance);
        property.getThing().getBehavior().notifyListeners(property.getChangeEvent(), new PropertyChangeEvent.Instance<>(property.getChangeEvent(), property.get(), typedInstance.getNewValue()));
    }

}
