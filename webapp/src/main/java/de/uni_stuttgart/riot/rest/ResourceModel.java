package de.uni_stuttgart.riot.rest;

/**
 * ResourceModels are simple data objects which are used for
 * serialization and deserialization in the REST API.
 * 
 * All resource models must have an id field, so that resource models
 * can be identified uniquely via URIs.
 */
public interface ResourceModel {

    /**
     * Gets the id.
     *
     * @return the id of the instance
     */
    int getId();

    /**
     * Sets the id for th einstance.
     *
     * @param id
     *            the new id
     */
    void setId(int id);
}
