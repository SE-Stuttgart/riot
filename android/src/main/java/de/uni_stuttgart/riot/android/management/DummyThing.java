package de.uni_stuttgart.riot.android.management;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

import de.uni_stuttgart.riot.commons.model.OnlineState;
import de.uni_stuttgart.riot.commons.rest.data.Storable;

/**
 * Dummy class.
 *
 * @author Benny
 */
public class DummyThing extends Storable {

    private static final AtomicLong THING_ID = new AtomicLong(1);

    private ArrayList<DummyProperty> properties;
    private String name;

    /**
     * .
     *
     * @param name .
     */
    public DummyThing(String name) {
        super(generateViewId());
        this.name = name;
        this.properties = new ArrayList<DummyProperty>();
    }

    /**
     * .
     *
     * @param property .
     */
    public void addProperty(DummyProperty property) {
        this.properties.add(property);
    }

    /**
     * .
     *
     * @return .
     */
    public ArrayList<DummyProperty> getProperties() {
        return this.properties;
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
     * Generate a value suitable for use in View.setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private static Long generateViewId() {
        for (; ; ) {
            final Long result = THING_ID.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            Long newValue = result + 1;
            if (THING_ID.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

//    /**
//     * Constructor.
//     *
//     * @param id        .
//     * @param thingName .
//     */
//    public DummyThing(long id, String thingName) {
//        super(thingName, new ThingBehavior() {
//            @Override
//            protected <A extends ActionInstance> void userFiredAction(A a) {
//
//            }
//        });
//        setId(id);
//        properties = new ArrayList<DummyProperty>();
//    }

//    /**
//     * .
//     *
//     * @param properties .
//     */
//    public void setProperties(ArrayList<DummyProperty> properties) {
//        this.properties = properties;
//    }
//
//    /**
//     * .
//     *
//     * @return .
//     */
//    public ArrayList<DummyProperty> getDummyProperties() {
//        return this.properties;
//    }
//
//    /**
//     * .
//     *
//     * @param property .
//     * @param value    .
//     */
//    public void updateProperty(DummyProperty property, Object value) {
//        int index = this.properties.indexOf(property);
//        if (index != -1) {
//            property.setValue(value);
//            this.properties.set(index, property);
//        } else {
//            // ToDo problem -- property not found!
//        }
//    }

    /**
     * Returns the image resource id to display the online state.
     *
     * @return .
     */
    public OnlineState getOnlineState() {
        final int number = 3;
        return OnlineState.getEnumById(this.getId() % number);
    }
}
