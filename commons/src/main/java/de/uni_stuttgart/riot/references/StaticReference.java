package de.uni_stuttgart.riot.references;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A static reference to an entity of type <tt>R</tt>. This is essentially a thin wrapper around the ID of the target entity to use it as a
 * {@link Reference}.
 * 
 * @param <R>
 *            The type of the referenced entity (this implementation doesn't care because it never gets to see the target entity).
 */
public class StaticReference<R> implements Reference<R> {

    /**
     * This is a static reference to the null value.
     */
    @SuppressWarnings("rawtypes")
    public static final ResolvableReference NULL_REFERENCE = new ResolvableReference() {
        @Override
        public Long getId() {
            return null;
        }

        @Override
        public Object getTarget() throws ResolveReferenceException {
            return null;
        }
    };

    /**
     * The ID of the referenced entity.
     */
    private final long id;

    /**
     * Creates a new static reference.
     * 
     * @param id
     *            The ID of the referenced entity.
     */
    private StaticReference(long id) {
        if (id == 0) {
            throw new IllegalArgumentException("A reference to ID 0 is not possible!");
        }
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Creates a new static reference.
     * 
     * @param <R>
     *            The type of the referenced entity.
     * @param id
     *            The ID of the referenced entity.
     * @return A static reference.
     */
    public static <R> StaticReference<R> create(long id) {
        return new StaticReference<R>(id);
    }

    /**
     * Creates a new static reference to the given target entity.
     * 
     * @param <R>
     *            The type of the referenced entity.
     * @param target
     *            The entity to be referenced.
     * @return A static reference.
     */
    @SuppressWarnings("unchecked")
    public static <R extends Referenceable<R>> Reference<R> create(R target) {
        if (target == null) {
            return NULL_REFERENCE;
        } else if (target.getId() == null || target.getId() == 0) {
            throw new IllegalArgumentException("A reference to an entity with ID " + target.getId() + " is not possible!");
        } else {
            return create(target.getId());
        }
    }

    /**
     * Creates a new static reference from the given JSON node. The node is expected to be either null or a number.
     * 
     * @param <R>
     *            The type of the referenced entity.
     * @param node
     *            The node to read.
     * @return A static reference representing the node's value.
     */
    @SuppressWarnings("unchecked")
    public static <R> Reference<R> create(JsonNode node) {
        if (node.isNull()) {
            return NULL_REFERENCE;
        } else if (node.isNumber()) {
            if (node.asLong() == 0) {
                return NULL_REFERENCE;
            } else {
                return new StaticReference<R>(node.asLong());
            }
        } else {
            throw new IllegalArgumentException("A JSON node of type " + node.getNodeType() + " cannot be converted to a Reference!");
        }
    }

}
