package de.uni_stuttgart.riot.android.management;


import de.uni_stuttgart.riot.thing.ui.UIHint;

//import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * Created by Benny on 16.02.2015.
 */
public class DummyProperty { // extends Property {

//    private PropertyType propertyType;
//    private ArrayList<DummyProperty> subList;

//    private String name;
//    private Object value;

//    private boolean instantUpdate;

    private UIHint uiHint;
    private String name;
    private PropertyType valueType;
    private Object value;

    /**
     * .
     *
     * @param uiHint    .
     * @param name      .
     * @param valueType .
     * @param value     .
     */
    public DummyProperty(UIHint uiHint, String name, PropertyType valueType, Object value) {
        this.uiHint = uiHint;
        this.name = name;
        this.valueType = valueType;
        this.value = value;
    }

    /**
     * .
     *
     * @param uiHint    .
     * @param name      .
     * @param valueType .
     */
    public DummyProperty(UIHint uiHint, String name, PropertyType valueType) {
        this(uiHint, name, valueType, null);
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
    public UIHint getUiHint() {
        return this.uiHint;
    }

    /**
     * .
     *
     * @return .
     */
    public PropertyType getValueType() {
        return this.valueType;
    }

    /**
     * .
     *
     * @return .
     */
    public Object getValue() {
        return this.value;
    }


//    /**
//     * Constructor.
//     *
//     * @param name         .
//     * @param propertyType .
//     * @param value        .
//     * @param subList      .
//     */
//    public DummyProperty(String name, PropertyType propertyType, Object value, ArrayList<DummyProperty> subList) {
//        //super(new DummyThing(), name, value);
//        this.propertyType = propertyType;
//        this.subList = subList;
//
//        this.name = name;
//        this.value = value;
//
//        this.instantUpdate = false;
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param propertyType .
//     * @param name         .
//     * @param value        .
//     */
//    public DummyProperty(String name, PropertyType propertyType, Object value) {
//        this(name, propertyType, value, null);
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param name         .
//     * @param propertyType .
//     * @param subList      .
//     */
//    public DummyProperty(String name, PropertyType propertyType, ArrayList<DummyProperty> subList) {
//        this(name, propertyType, null, subList);
//    }
//
//    /**
//     * .
//     *
//     * @return .
//     */
//    public PropertyType getPropertyType() {
//        return propertyType;
//    }
//
//    /**
//     * .
//     *
//     * @param property .
//     */
//    public void addSubListItem(DummyProperty property) {
//        this.subList.add(property);
//    }
//
//    /**
//     * .
//     *
//     * @return .
//     */
//    public ArrayList<DummyProperty> getSubList() {
//        return this.subList;
//    }
//
//    /**
//     * .
//     *
//     * @return .
//     */
//    public String getName() {
//        return this.name;
//    }
//
//    /**
//     * .
//     *
//     * @return .
//     */
//    public Object getValue() {
//        return this.value;
//    }
//
//    /**
//     * .
//     *
//     * @param value .
//     */
//    public void setValue(Object value) {
//        this.value = value;
//    }
//
//    /**
//     * .
//     *
//     * @param instantUpdate .
//     */
//    public void setInstantUpdate(boolean instantUpdate) {
//        this.instantUpdate = instantUpdate;
//    }
//
//    /**
//     * .
//     *
//     * @return .
//     */
//    public boolean isInstantUpdate() {
//        return this.instantUpdate;
//    }
}
