package de.uni_stuttgart.riot.data.memoryDao;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import de.uni_stuttgart.riot.data.DAO;
import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.storable.Storable;

public class MemoryDAO<T extends Storable> implements DAO<T> {

	private final HashMap<Long, T> store;
	
	public MemoryDAO() {
		this.store = new HashMap<Long, T>();
	}
	
	public HashMap<Long, T> getStore() {
		return store;
	}

	@Override
	public void delete(T t) throws DatasourceDeleteException {
		if(!this.getStore().remove(t.getID(), t))
			throw new DatasourceDeleteException(DatasourceDeleteException.OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
	}

	@Override
	public void insert(T t) throws DatasourceInsertException {
		this.getStore().put(t.getID(),t);
	}

	@Override
	public void update(T t) throws DatasourceUpdateException {
		this.getStore().put(t.getID(),t);
	}

	@Override
	public T findBy(long ID) throws DatasourceFindException {
		T result = this.getStore().get(ID);
		if(null == result) throw new DatasourceFindException(DatasourceFindException.OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
		return result;
	}

	@Override
	public Collection<T> findBy(Collection<String> searchParams)throws DatasourceFindException {
		LinkedList<T> result = new LinkedList<T>();
		for (T t: this.getStore().values()){
			if(t.getSearchParam().containsAll(searchParams))result.add(t);
		}
		return result;
	}

}
