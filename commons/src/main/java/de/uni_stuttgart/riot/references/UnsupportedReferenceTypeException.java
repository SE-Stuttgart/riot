package de.uni_stuttgart.riot.references;

/**
 * This kind of exception is thrown when a {@link ReferenceResolver} does not support the given type of referenced entity.
 * 
 * @author Philipp Keck
 */
public class UnsupportedReferenceTypeException extends ResolveReferenceException {

    private static final long serialVersionUID = 1182375069868647185L;

    /**
     * Constructor for UnsupportedReferenceTypeException.
     * 
     * @param targetType
     *            The requested target type.
     */
    public UnsupportedReferenceTypeException(Class<? extends Referenceable<?>> targetType) {
        super("Resolving references of type " + targetType + " is not supported!");
    }

}
