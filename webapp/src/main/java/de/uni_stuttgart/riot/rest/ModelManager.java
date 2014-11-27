package de.uni_stuttgart.riot.rest;

import java.util.Collection;

/**
 * The ModelManager bridges the persistence layer and the 
 * client facing REST API.
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
     */
    E getById(int id);

    /**
     * Gets the collection of models.
     *
     * @return the collection of models.
     */
    Collection<E> get();

    /**
     * Creates a new model if the provided object is valid.
     *
     * @param model the model to be created
     * @return the newly created model (including its set id)
     */
    E create(E model);

    /**
     * Delete a model by id.
     *
     * @param id
     *            the id
     */
    void delete(int id);

    /**
     * Update/replace a model.
     *
     * @param model
     *            the new model
     * @return the modified model
     */
    E update(E model);

}
