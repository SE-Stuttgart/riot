package de.uni_stuttgart.riot.references;

/**
 * A special kind of {@link ReferenceResolver} that is only able to resolve references of a certain type.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of target entities that can be resolved.
 */
public interface TypedReferenceResolver<R extends Referenceable<R>> {

    /**
     * Resolves an entity reference. Note that calling this method is usually expensive and might involve a number of server and/or database
     * queries.
     * 
     * @param targetId
     *            The ID of the target entity.
     * @return The resolved reference, never <tt>null</tt>.
     * @throws ResolveReferenceException
     *             When resolving the reference fails. See the subclasses of {@link ResolveReferenceException} for details on possible
     *             causes.
     */
    R resolve(long targetId) throws ResolveReferenceException;

}
