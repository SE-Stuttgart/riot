package de.uni_stuttgart.riot.data;

import java.util.Collection;

import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.sqlquerydao.SearchParameter;
import de.uni_stuttgart.riot.data.storable.Storable;

public interface DAO<T extends Storable> {
    /**
     * Deletes the given object from the datasource.
     * 
     * @param t
     *            Object to be deleted
     */
    public void delete(T t) throws DatasourceDeleteException;

    /**
     * Inserts the given object into the datasource.
     * 
     * @param t
     */
    public void insert(T t) throws DatasourceInsertException;

    /**
     * Updates the given object in the datasource.
     * 
     * @param t
     */
    public void update(T t) throws DatasourceUpdateException;

    /**
     * Returns the object that is identified by ID
     * 
     * @param ID
     *            the unique id
     * @return the object with id ID
     */
    public T findBy(long ID) throws DatasourceFindException;

    /**
     * Returns all object with the given searchParam.
     * 
     * @param searchParam
     *            param the object should match
     * @return all matching objects
     */
    public Collection<T> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException;

    /**
     * Returns all T objects which are stored in the datasource.
     * 
     * @return
     * @throws DatasourceFindException
     */
    public Collection<T> findAll() throws DatasourceFindException;
}
