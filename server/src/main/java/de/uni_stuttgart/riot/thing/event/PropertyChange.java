package de.uni_stuttgart.riot.thing.event;

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
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PropertyChange [oldValue=");
        builder.append(oldValue);
        builder.append(", newValue=");
        builder.append(newValue);
        builder.append("]");
        return builder.toString();
    }
    public X getNewValue() {
        return newValue;
    }
    public void setNewValue(X newValue) {
        this.newValue = newValue;
    }
}