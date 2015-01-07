package de.uni_stuttgart.riot.commons.model;

import java.util.Collection;

/**
 * 
 * Interface for all classes that should be stored at some point.
 * @author Jonas Tangermann
 *
 */
public interface Storable {

    /**
     * Returns the unique id of this object.
     * 
     * @return unique id
     */
    public long getId();
    
    /**
     * Setter for the unique id
     */
    public void setId(long id);

}
