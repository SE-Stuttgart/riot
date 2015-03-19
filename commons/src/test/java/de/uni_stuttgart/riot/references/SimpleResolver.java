package de.uni_stuttgart.riot.references;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple resolver that contains a fixed set of elements. This is mostly useful for tests.
 * 
 * @author Philipp Keck
 *
 * @param <R>
 *            The type of the elements.
 */
public class SimpleResolver<R extends Referenceable<R>> implements TypedReferenceResolver<R> {

    private final Map<Long, R> elements = new HashMap<Long, R>();

    private SimpleResolver() {
    }

    @Override
    public R resolve(long targetId) throws ResolveReferenceException {
        if (elements.containsKey(targetId)) {
            return elements.get(targetId);
        } else {
            throw new ResolveReferenceException("Element with ID " + targetId + " does not exist!");
        }
    }

    public static <R extends Referenceable<R>, E extends R> SimpleResolver<R> create(E... elements) {
        SimpleResolver<R> result = new SimpleResolver<R>();
        for (E element : elements) {
            result.elements.put(element.getId(), element);
        }
        return result;
    }

    public static <R extends Referenceable<R>, E extends R> DelegatingReferenceResolver create(Class<R> targetType, E... elements) {
        DelegatingReferenceResolver result = new DelegatingReferenceResolver();
        result.addResolver(targetType, SimpleResolver.<R, E> create(elements));
        return result;
    }

}
