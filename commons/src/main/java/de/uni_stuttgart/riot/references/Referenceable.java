package de.uni_stuttgart.riot.references;

/**
 * A referenceable entity does not need to be referenced directly in memory (as an object reference), but can be reduced to storing/passing
 * a {@link Long} value that identifies the target entity, together with the type of the reference.
 * 
 * @author Philipp Keck
 *
 * @param <T>
 *            The base type of the reference. If a type <tt>X</tt> extends a referenceable type <tt>T</tt>, all references to instances of
 *            <tt>X</tt> must also be resolved as instances of <tt>T</tt>.
 */
public interface Referenceable<T> {

    /**
     * Gets the ID of the referenceable entity.
     * 
     * @return The ID. It may be <tt>null</tt>, in which case the target entity is not yet persisted and thus not referenceable yet.
     */
    Long getId();

}
