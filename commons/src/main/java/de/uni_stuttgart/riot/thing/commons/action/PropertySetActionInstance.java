package de.uni_stuttgart.riot.thing.commons.action;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * Instance of {@link PropertySetAction}.
 * @param <T>
 */
public class PropertySetActionInstance<T> extends ActionInstance {

    private Property<T> property;

   /**
    * Constructor.
    * @param property .
    * @param thingid . 
    * @param time .
    */
    public PropertySetActionInstance(Property<T> property, long thingid, Timestamp time) {
        super(time, thingid);
        this.setProperty(property);
    }
    
    /**
     * Default constructor.
     */
    public PropertySetActionInstance() {
        super(new Timestamp(0), -1);
    }

    public Property<T> getProperty() {
        return property;
    }

    public void setProperty(Property<T> property) {
        this.property = property;
    }

    @Override
    public void accept(ActionInstanceVisitor actionInstanceVisitor) {
        actionInstanceVisitor.handle(this);
    }
}
