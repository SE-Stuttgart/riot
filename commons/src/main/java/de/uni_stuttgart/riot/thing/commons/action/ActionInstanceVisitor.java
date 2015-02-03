package de.uni_stuttgart.riot.thing.commons.action;

public interface ActionInstanceVisitor {
    
    public <T> void handle(PropertySetActionInstance<T> propertySetActionInstance); 

}
