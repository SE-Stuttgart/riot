package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

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
    public void setId(Long id);

    /**
     * Returns all search parameters that should be inspected
     * 
     * @return all search parameters
     */
    public Collection<SearchParameter> getSearchParam();

}
