package de.uni_stuttgart.riot.thing.commons.action;

/**
 *  {@link ActionInstance} Visitor.
 */
public interface ActionInstanceVisitor {
    
    /**
     * Handles a {@link PropertySetActionInstance}.
     * @param propertySetActionInstance the {@link PropertySetActionInstance}.
     * @param <T> .
     */
    <T> void handle(PropertySetActionInstance<T> propertySetActionInstance); 

}
