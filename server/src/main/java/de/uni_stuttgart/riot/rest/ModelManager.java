package de.uni_stuttgart.riot.rest;

import java.util.Collection;

import de.uni_stuttgart.riot.db.DaoException;

/**
 * The ModelManager bridges the persistence layer and the client facing REST API.
 *
 * @param <E>
 *            Each model manager should manage one ResourceModel.
 */
public interface ModelManager<E extends ResourceModel> {

    /**
     * Gets the model by its identifier.
     *
     * @param id
     *            the model id
     * @return the ResourceModel if found, null otherwise
     * @throws DaoException
     *             when retrieving the data fails.
     */
    E getById(long id) throws DaoException;

    /**
     * Gets the collection of models.
     *
     * @return the collection of models.
     * @throws DaoException
     *             when retrieving the data fails.
     */
    Collection<E> get() throws DaoException;

    /**
     * Creates a new model if the provided object is valid.
     *
     * @param model
     *            the model to be created
     * @return the newly created model (including its set id)
     * @throws DaoException
     *             when writing the data fails.
     */
    E create(E model) throws DaoException;

    /**
     * Delete a model by id.
     *
     * @param id
     *            the id
     * @return true if the specified row was deleted, false if no such row exists.
     * @throws DaoException
     *             when deleting fails.
     */
    boolean delete(long id) throws DaoException;

    /**
     * Update/replace a model.
     *
     * @param model
     *            the new model
     * @return true if the specified row was updated, false otherwise.
     * @throws DaoException
     *             when updating the data fails.
     */
    boolean update(E model) throws DaoException;

}
