package de.uni_stuttgart.riot.server.commons.db;

import java.util.Collection;

import de.uni_stuttgart.riot.commons.model.Storable;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

/**
 * A DAO manages access to persistence data objects.
 * 
 * @param <T>
 *            The type of the objects.
 */
public interface DAO<T extends Storable> {

    /**
     * Deletes the given object from the datasource.
     * 
     * @param t
     *            Object to be deleted
     * @throws DatasourceDeleteException
     *             When the object cannot be deleted.
     */
    void delete(T t) throws DatasourceDeleteException;

    /**
     * Inserts the given object into the datasource.
     * 
     * @param t
     *            Object to be inserted.
     * @throws DatasourceInsertException
     *             When the object cannot be inserted.
     */
    void insert(T t) throws DatasourceInsertException;

    /**
     * Updates the given object in the datasource.
     * 
     * @param t
     *            The object to be updated.
     * @throws DatasourceUpdateException
     *             When the object cannot be updated.
     */
    void update(T t) throws DatasourceUpdateException;

    /**
     * Returns the object that is identified by ID.
     * 
     * @param id
     *            the unique id
     * @return the object with id ID
     * @throws DatasourceFindException
     *             When no object with the given ID exists or some other error occurs upon retrieving the object.
     */
    T findBy(long id) throws DatasourceFindException;

    /**
     * Returns all object with the given searchParam.
     * 
     * @param searchParams
     *            params, which the object should match.
     * @param or
     *            Whether the parameters should be concatenated by OR instead or AND.
     * @return all matching objects
     * @throws DatasourceFindException
     *             When retrieving the objects failed.
     */
    Collection<T> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException;

    /**
     * Returns all T objects which are stored in the datasource.
     * 
     * @return All T objects.
     * @throws DatasourceFindException
     *             When retrieving the objects failed.
     */
    Collection<T> findAll() throws DatasourceFindException;

    /**
     * Returns the fist element that is matching the searchparameter. <b>Should only be used with unique fields</b>
     * 
     * @param searchParameter
     *            parameter, which the element must match.
     * @return the first matching element
     * @throws DatasourceFindException
     *             if there is no such element
     */
    T findByUniqueField(SearchParameter searchParameter) throws DatasourceFindException;

}
