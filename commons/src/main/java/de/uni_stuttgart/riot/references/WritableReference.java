package de.uni_stuttgart.riot.references;

/**
 * A reference to an entity of type <tt>T</tt> that can be changed.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the referenced entity.
 */
public interface WritableReference<R> extends Reference<R> {

    /**
     * Sets a new target ID.
     * 
     * @param newId
     *            The ID of the new target entity. A value of <tt>null</tt> indicates that no target entity is set.
     */
    void setId(Long newId);

    /**
     * Sets a new target entity.
     * 
     * @param target
     *            The new target entity, may be <tt>null</tt>.
     */
    void setTarget(R target);

}
