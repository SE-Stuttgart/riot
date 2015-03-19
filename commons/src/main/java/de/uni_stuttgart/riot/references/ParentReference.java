package de.uni_stuttgart.riot.references;

import de.uni_stuttgart.riot.thing.Thing;

/**
 * A {@link ParentReference} is used by a {@link Thing} to refer to its parent. The reference is optional and can be modified by the user.
 * It must be ensured that the overall directed network of parent relationships is a tree, i.e., that it is acyclic. To prevent cycles, it
 * suffices to check upon every insertion/change of an edge to check if the referencing node is reachable from itself. That's because every
 * node can have at most one parent and because every possible cycle would need one of its edges to be inserted as the last one. Both
 * adjacent Things of this last edge are part of the circle, so it suffices to start the cycle check from there.
 * 
 * @author Philipp Keck
 */
public class ParentReference implements WritableReference<Thing>, ResolvableReference<Thing> {

    /**
     * The (child) Thing that this reference belongs to.
     */
    private final Thing thing;

    /**
     * The ID of the referenced parent Thing.
     */
    private Long id;

    /**
     * Creates a new instance.
     * 
     * @param thing
     *            The thing that this references belongs to, i.e., the thing that references the target.
     */
    public ParentReference(Thing thing) {
        if (thing == null) {
            throw new IllegalArgumentException("thing must not be null!");
        }
        this.thing = thing;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long newId) {
        this.id = newId;
    }

    @Override
    public void setTarget(Thing target) {
        setId(target == null ? null : target.getId());
    }

    @Override
    public Thing getTarget() throws ResolveReferenceException {
        if (id == null) {
            return null;
        }
        return thing.getBehavior().resolve(id, Thing.class);
    }

}
