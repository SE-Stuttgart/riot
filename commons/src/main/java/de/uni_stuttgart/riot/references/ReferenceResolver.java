package de.uni_stuttgart.riot.references;

/**
 * A reference resolver resolves {@link Reference} by querying some data source for the given {@link Reference#getId()} and returning the
 * resulting target entity.
 * 
 * @author Philipp Keck
 */
public interface ReferenceResolver {

    /**
     * Resolves an entity reference. Note that calling this method is usually expensive and might involve a number of server and/or database
     * queries.
     * 
     * @param <R>
     *            The expected type of the target entity.
     * @param targetId
     *            The ID of the target entity, may be <tt>null</tt>.
     * @param targetType
     *            The expected type of the target entity.
     * @return The resolved reference. This method returns <tt>null</tt> if and only if <tt>targetId</tt> was <tt>null</tt>.
     * @throws ResolveReferenceException
     *             When resolving the reference fails. See the subclasses of {@link ResolveReferenceException} for details on possible
     *             causes.
     */
    <R extends Referenceable<? super R>> R resolve(Long targetId, Class<R> targetType) throws ResolveReferenceException;

}
