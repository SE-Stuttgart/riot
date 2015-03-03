package de.uni_stuttgart.riot.thing;

/**
 * This class represents an action that can be executed by a {@link Thing}.
 *
 * @param <A>
 *            The type of the action's instances.
 */
public class Action<A extends ActionInstance> {

    private final transient Thing thing;
    private final String name;
    private final Class<A> instanceType;

    /**
     * Creates a new action. Note that the constructor is internal. Things are required to instantiate their actions through
     * {@link Thing#newAction(String, Class)}.
     * 
     * @param thing
     *            The thing that this action belongs to.
     * @param name
     *            The name of the action.
     * @param instanceType
     *            The type of instances that will be fired by this action.
     */
    Action(Thing thing, String name, Class<A> instanceType) {
        this.thing = thing;
        this.name = name;
        this.instanceType = instanceType;
    }

    /**
     * Gets the containing thing.
     * 
     * @return The thing that this action belongs to.
     */
    public Thing getThing() {
        return thing;
    }

    /**
     * Gets the name.
     * 
     * @return The name of this action.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type.
     * 
     * @return The type of instances that will be fired by this action.
     */
    public Class<A> getInstanceType() {
        return instanceType;
    }

    /**
     * Fires the action. This method is to be called by the user. Subclasses may facilitate the calling of this method by creating
     * respective overloaded methods. It is important to note that this method <b>does not actually fire</b> the action. Instead, it
     * transports the {@link ActionInstance} to the computer where the thing is actually executed. The action will be fired there and the
     * results and effects will be transported back in the form of property changes and events.
     * 
     * @param instance
     *            The parameters instance.
     */
    public void fire(A instance) {
        if (instance.getThingId() != this.getThing().getId() || !name.equals(instance.getName())) {
            throw new IllegalArgumentException("The passed ActionInstance does not belong to this action!");
        }
        thing.getBehavior().userFiredAction(instance);
    }

    // CHECKSTYLE:OFF (Generated Code)
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Action<?> other = (Action<?>) obj;
        if (instanceType == null) {
            if (other.instanceType != null) {
                return false;
            }
        } else if (!instanceType.equals(other.instanceType)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
