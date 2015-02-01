package de.uni_stuttgart.riot.thing.commons.event;

import java.sql.Timestamp;

import de.uni_stuttgart.riot.thing.commons.Property;

/**
 * The Change Event of a {@link Property}.
 * 
 * @param <T>
 */
public class PropertyChangeEvent<T> extends Event<PropertyChangeEventInstance<T>> {

    private String propertyName;

    /**
     * Constructor.
     * 
     * @param propertyName
     *            .
     */
    public PropertyChangeEvent(String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * Constructor.
     */
    public PropertyChangeEvent() {
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    public PropertyChangeEventInstance<T> createInstance(T newValue, long thingId) {
        return new PropertyChangeEventInstance<T>(new Property<T>(propertyName,newValue),thingId,new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public boolean isTypeOf(EventInstance eventInstance) {
        if(eventInstance instanceof PropertyChangeEventInstance){
            PropertyChangeEventInstance eI = (PropertyChangeEventInstance) eventInstance;
            return eI.getNewProperty().getName().equals(this.propertyName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((propertyName == null) ? 0 : propertyName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PropertyChangeEvent other = (PropertyChangeEvent) obj;
        if (propertyName == null) {
            if (other.propertyName != null)
                return false;
        } else if (!propertyName.equals(other.propertyName))
            return false;
        return true;
    }
}
