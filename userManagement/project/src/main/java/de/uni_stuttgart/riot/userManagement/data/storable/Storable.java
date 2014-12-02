package de.uni_stuttgart.riot.userManagement.data.storable;

import java.util.Collection;

import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;

public interface Storable {

    /**
     * Returns the unique id of this object.
     * 
     * @return unique id
     */
    public long getID();

    /**
     * Returns all search parameters that should be inspected
     * 
     * @return all search parameters
     */
    public Collection<SearchParameter> getSearchParam();

}
