package de.uni_stuttgart.riot.thing.event;


public class PropertyChangeEvent<T extends FiredEvent> extends Event<T>{
    
    public class PropertyChange<X> extends FiredEvent {
        private X oldValue;
        private X newValue;
        
        public PropertyChange(X oldVlaue, X newValue) {
            this.setNewValue(newValue);
            this.setOldValue(oldVlaue);
        }
        public X getOldValue() {
            return oldValue;
        }
        public void setOldValue(X oldValue) {
            this.oldValue = oldValue;
        }
        public X getNewValue() {
            return newValue;
        }
        public void setNewValue(X newValue) {
            this.newValue = newValue;
        }
    }

}
