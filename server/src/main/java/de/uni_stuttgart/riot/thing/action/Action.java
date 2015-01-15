package de.uni_stuttgart.riot.thing.action;

public interface Action<T extends ExecutedAction> {
    
    public void execute(T executedAction);

}
