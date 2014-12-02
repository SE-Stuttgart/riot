package de.uni_stuttgart.riot.userManagement.data.memorydao;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import de.uni_stuttgart.riot.userManagement.data.DAO;
import de.uni_stuttgart.riot.userManagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.userManagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.userManagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.userManagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.userManagement.data.sqlQueryDao.SearchParameter;
import de.uni_stuttgart.riot.userManagement.data.storable.Storable;

/**
 * 
 * DAO that uses the memory as datasource. 
 * <br>
 * <b>This DAO is for test usage only!</b>
 * 
 * @author Jonas Tangermann
 *
 * @param <T> Subtype of {@link Storable} that should be stored.
 */
public class MemoryDAO<T extends Storable> implements DAO<T> {

    /**
     * Structure for storing the objects.
     * <br>
     * Key = object id ( {@link Storable#getID()})
     */
    private final HashMap<Long, T> store;

    /**
     * Default-Constructor.
     */
    public MemoryDAO() {
        this.store = new HashMap<Long, T>();
    }

    /**
     * Returns the structure which holds all stored objects.
     * @return
     *      store structure
     */
    public HashMap<Long, T> getStore() {
        return store;
    }

    @Override
    public void delete(T t) throws DatasourceDeleteException {
        if (!this.getStore().remove(t.getID(), t)) {
            throw new DatasourceDeleteException(DatasourceDeleteException.OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
        }
    }

    @Override
    public void insert(T t) throws DatasourceInsertException {
        this.getStore().put(t.getID(), t);
    }

    @Override
    public void update(T t) throws DatasourceUpdateException {
        this.getStore().put(t.getID(), t);
    }

    @Override
    public T findBy(long id) throws DatasourceFindException {
        T result = this.getStore().get(id);
        if (null == result) {
            throw new DatasourceFindException(DatasourceFindException.OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
        }
        return result;
    }

    @Override
    public Collection<T> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
        LinkedList<T> result = new LinkedList<T>();
        for (T t : this.getStore().values()) {
            if (or) {
                this.orSearch(t, result, searchParams);
            } else {
                this.andSearch(t, result, searchParams);
            }
        }
        return result;
    }

    /**
     * Adds t into result if and only if all searchParams could be found in {@link Storable#getSearchParam()}. 
     */
    private void andSearch(T t, LinkedList<T> result, Collection<SearchParameter> searchParams) {
        if (t.getSearchParam().containsAll(searchParams)) {
            result.add(t);
        }
    }

    /**
     * Adds t into result if and only if at least one searchParams could could be found in {@link Storable#getSearchParam()}. 
     */
    private void orSearch(T t, LinkedList<T> result, Collection<SearchParameter> searchParams) {
        Collection<SearchParameter> tParam = t.getSearchParam();
        for (SearchParameter p : searchParams) {
            if (tParam.contains(p)) {
                result.add(t);
                return;
            }
        }
    }

    @Override
    public Collection<T> findAll() throws DatasourceFindException {
        return this.getStore().values();
    }

}
