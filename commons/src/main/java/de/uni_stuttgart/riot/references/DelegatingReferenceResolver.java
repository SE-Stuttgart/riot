package de.uni_stuttgart.riot.references;

import java.util.HashMap;
import java.util.Map;

/**
 * A delegating reference resolver aggregates multiple {@link TypedReferenceResolver}s and chooses the right one for each request based on
 * the target type.
 * 
 * @author Philipp Keck
 */
public class DelegatingReferenceResolver implements ReferenceResolver {

    private final Map<Class<? extends Referenceable<?>>, TypedReferenceResolver<?>> resolvers = new HashMap<Class<? extends Referenceable<?>>, TypedReferenceResolver<?>>();

    @Override
    public <R extends Referenceable<? super R>> R resolve(Long targetId, Class<R> targetType) throws ResolveReferenceException {
        if (targetId == null) {
            return null;
        }
        TypedReferenceResolver<?> resolver = resolvers.get(targetType);
        if (resolver == null) {
            throw new UnsupportedReferenceTypeException(targetType);
        }

        // The signature of addResolver(..) ensures that this works.
        return targetType.cast(resolver.resolve(targetId));
    }

    /**
     * Adds a new resolver.
     * 
     * @param <R>
     *            The type of entities handled by this resolver.
     * @param targetType
     *            The type of entities handled by this resolver.
     * @param resolver
     *            The resolver.
     */
    public <R extends Referenceable<R>> void addResolver(Class<R> targetType, TypedReferenceResolver<R> resolver) {
        resolvers.put(targetType, resolver);
    }

}
