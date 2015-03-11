package de.uni_stuttgart.riot.references;

/**
 * A resolve reference can retrieve its referenced entity (by calling some resolver internally). In particular, it could cache the value.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the referenced entity.
 */
public interface ResolvableReference<R> extends Reference<R> {

    /**
     * Retrieves the target entity, either from a cache or by resolving it.
     * 
     * @return The target entity or <tt>null</tt> if the reference is not set.
     * @throws ResolveReferenceException
     *             When resolving the reference fails.
     */
    R getTarget() throws ResolveReferenceException;

}
