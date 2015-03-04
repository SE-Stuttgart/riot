package de.uni_stuttgart.riot.android.management;

import java.util.ArrayList;

//import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * Created by Benny on 16.02.2015.
 */
public class DummyProperty { // extends Property {

    private PropertyType propertyType;
    private ArrayList<DummyProperty> subList;

    private String name;
    private Object value;

    private boolean instantUpdate;

    /**
     * Constructor.
     *
     * @param name         .
     * @param propertyType .
     * @param value        .
     * @param subList      .
     */
    public DummyProperty(String name, PropertyType propertyType, Object value, ArrayList<DummyProperty> subList) {
        //super(new DummyThing(), name, value);
        this.propertyType = propertyType;
        this.subList = subList;

        this.name = name;
        this.value = value;

        this.instantUpdate = false;
    }

    /**
     * Constructor.
     *
     * @param propertyType .
     * @param name         .
     * @param value        .
     */
    public DummyProperty(String name, PropertyType propertyType, Object value) {
        this(name, propertyType, value, null);
    }

    /**
     * Constructor.
     *
     * @param name         .
     * @param propertyType .
     * @param subList      .
     */
    public DummyProperty(String name, PropertyType propertyType, ArrayList<DummyProperty> subList) {
        this(name, propertyType, null, subList);
    }

    /**
     * .
     *
     * @return .
     */
    public PropertyType getPropertyType() {
        return propertyType;
    }

    /**
     * .
     *
     * @param property .
     */
    public void addSubListItem(DummyProperty property) {
        this.subList.add(property);
    }

    /**
     * .
     *
     * @return .
     */
    public ArrayList<DummyProperty> getSubList() {
        return this.subList;
    }

    /**
     * .
     *
     * @return .
     */
    public String getName() {
        return this.name;
    }

    /**
     * .
     *
     * @return .
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * .
     *
     * @param value .
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * .
     *
     * @param instantUpdate .
     */
    public void setInstantUpdate(boolean instantUpdate) {
        this.instantUpdate = instantUpdate;
    }

    /**
     * .
     *
     * @return .
     */
    public boolean isInstantUpdate() {
        return this.instantUpdate;
    }
}
