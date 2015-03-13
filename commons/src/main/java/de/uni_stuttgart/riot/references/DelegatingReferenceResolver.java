package de.uni_stuttgart.riot.references;

import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

/**
 * A delegating reference resolver aggregates multiple {@link TypedReferenceResolver}s and chooses the right one for each request based on
 * the target type.
 * 
 * @author Philipp Keck
 */
public class DelegatingReferenceResolver implements ReferenceResolver {

    private final Map<Class<? extends Referenceable<?>>, TypedReferenceResolver<?>> resolvers = new IdentityHashMap<Class<? extends Referenceable<?>>, TypedReferenceResolver<?>>();
    private final Map<Class<? extends Referenceable<?>>, Class<? extends Referenceable<?>>> referenceableParents = new IdentityHashMap<Class<? extends Referenceable<?>>, Class<? extends Referenceable<?>>>();

    @SuppressWarnings("unchecked")
    @Override
    public <Q extends Referenceable<? super Q>> Q resolve(Long targetId, Class<Q> targetType) throws ResolveReferenceException {

        // The referenceable supertype is not known, so we need to go find it.
        @SuppressWarnings("rawtypes")
        Class referenceableType = referenceableParents.get(targetType);
        if (referenceableType == null) {
            Class<?> clazz = targetType;
            while (!ArrayUtils.contains(clazz.getInterfaces(), Referenceable.class)) {
                clazz = clazz.getSuperclass();
                if (clazz == null) {
                    throw new RuntimeException(targetType + " does not have any super class that implements Referenceable!");
                }
            }
            referenceableType = clazz;
            referenceableParents.put(targetType, referenceableType);
        }

        return (Q) resolve(targetId, targetType, referenceableType);
    }

    /**
     * Resolves an entity where the referenceable supertype is given directly.
     * 
     * @param <R>
     *            The referenceable supertype.
     * @param <Q>
     *            The expected type of the target entity.
     * @param targetId
     *            The ID of the target entity.
     * @param targetType
     *            The expected type of the target entity.
     * @param referenceableType
     *            The referenceable supertype.
     * @return The target entity, never <tt>null</tt>.
     * @throws ResolveReferenceException
     *             If resolving the entity failed.
     */
    public <R extends Referenceable<R>, Q extends R> Q resolve(Long targetId, Class<Q> targetType, Class<R> referenceableType) throws ResolveReferenceException {
        if (targetId == null) {
            return null;
        }
        TypedReferenceResolver<?> resolver = resolvers.get(referenceableType);
        if (resolver == null) {
            throw new UnsupportedReferenceTypeException(referenceableType);
        }

        Object entity = resolver.resolve(targetId);
        try {
            return targetType.cast(entity);
        } catch (ClassCastException e) {
            throw new ResolveReferenceException("The resolved entity with ID " + targetId + " is of type " + entity.getClass() + " instead of " + targetType);
        }
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
