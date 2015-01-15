package de.uni_stuttgart.riot.thing.action;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.Property;

public class PropertySetAction<T> implements Action<PropertySetAction<T>.PropertySet<T>> {
    
    private Property<T> property;
    
    public PropertySetAction(Property<T> property){
        this.setProperty(property);
    }
    
    public class PropertySet<X> extends ExecutedAction {

        private X newValue;
        
        public PropertySet(X newValue) {
            super(PropertySetAction.this);
            this.setNewValue(newValue);
        }

        public X getNewValue() {
            return newValue;
        }

        private void setNewValue(X newValue) {
            this.newValue = newValue;
        }
    }

    public PropertySetAction() {
    }
    
    @Override
    public void execute(PropertySetAction<T>.PropertySet<T> executedAction) {
        this.getProperty().setValue(executedAction.getNewValue());
        executedAction.setTime(new Timestamp(System.currentTimeMillis()));
    }

    public Property<T> getProperty() {
        return property;
    }

    private void setProperty(Property<T> property) {
        this.property = property;
    }
}
