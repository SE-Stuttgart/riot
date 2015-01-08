package de.uni_stuttgart.riot.commons.model;

/**
 * 
 * Storables are simple data objects which are used for serialization and deserialization in the REST API and for all classes that should be
 * stored at some point.
 * 
 * All storables must have an id field, so that it can be identified uniquely.
 * 
 * @author Jonas Tangermann
 *
 */
public interface Storable {

    /**
     * Returns the unique id of this object.
     * 
     * @return unique id
     */
    long getId();

    /**
     * Setter for the unique id.
     * 
     * @param id
     *            the new id
     */
    void setId(long id);

}
