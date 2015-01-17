package de.uni_stuttgart.riot.thing.commons;

import de.uni_stuttgart.riot.commons.rest.data.Storable;


public class Property<T> extends Storable{

    private String name;
    private transient T typedVal;
    private String val;
    private String valType;
    private long thingID;
    
    
    @Override
    public String toString() {
        return "Property [name=" + name + ", typedVal=" + typedVal + ", val=" + val + ", valType=" + valType + ", thingID=" + thingID + "]";
    }

    public Property(String name, T value, Thing owner) {
        this.setName(name);
        this.setValue(value);
        this.setThingID(owner.getId());
        this.setVal(this.getValue().toString());
        this.setValType(value.getClass().getSimpleName()); //FIXME
    }
    
    /**
     * DB Constructor.
     * @param name
     * @param value
     * @param type
     * @param thingID
     */
    public Property(String name, String value, String type, long thingID) {
        this.setName(name);
        this.setVal(value);
        this.setValType(type); //FIXME
        this.setThingID(thingID);
        this.setValue((T)value);
    }
    
    public Property() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public T getValue() {
        return typedVal;
    }

    /**
     * @param value the value to set
     */
    public void setValue(T value) {
        this.typedVal = value;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
        this.typedVal = (T)val;
    }

    public long getThingID() {
        return thingID;
    }

    public void setThingID(long thingID) {
        this.thingID = thingID;
    }

    public String getValType() {
        return valType;
    }

    public void setValType(String valType) {
        this.valType = valType;
    }
    
}
