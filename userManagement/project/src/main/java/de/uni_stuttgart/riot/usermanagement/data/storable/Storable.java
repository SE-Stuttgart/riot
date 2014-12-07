package de.uni_stuttgart.riot.usermanagement.data.storable;

import java.util.Collection;

import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SearchParameter;

public interface Storable {

    /**
     * Returns the unique id of this object.
     * 
     * @return unique id
     */
    public long getId();

    /**
     * Returns all search parameters that should be inspected
     * 
     * @return all search parameters
     */
    public Collection<SearchParameter> getSearchParam();

}
