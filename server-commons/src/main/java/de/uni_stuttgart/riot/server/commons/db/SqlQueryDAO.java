package de.uni_stuttgart.riot.server.commons.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.commons.model.Storable;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;

/**
 * 
 * Superclass for all SQL-Query based {@link DAO}s.
 * 
 * @author Jonas Tangermann
 *
 * @param <T>
 */
public abstract class SqlQueryDAO<T extends Storable> implements DAO<T> {

    private final Connection connection;
    private final QueryBuilderImpl queryBuilder;
    private final boolean transaction;

    /**
     * Constructor.
     * 
     */
    public SqlQueryDAO() {
        this.connection = null;
        this.queryBuilder = new QueryBuilderImpl();
        this.transaction = false;
    }

    /**
     * Constructor.
     * 
     * @param connection
     *            represents a connection to the database
     * @param transaction
     *            true if it is a transaction
     * @throws SQLException .
     */
    protected SqlQueryDAO(Connection connection, boolean transaction) throws SQLException {
        this.connection = connection;
        this.transaction = transaction;
        this.queryBuilder = new QueryBuilderImpl();
    }

    /**
     * gets the class type.
     * 
     * @return the Class type.
     */
    protected abstract Class<T> getMyClazz();

    @Override
    public void delete(T t) throws DatasourceDeleteException {
        try {
            Query stmt = SqlQueryDAO.this.queryBuilder.buildDelete(TableMapper.getTableName(SqlQueryDAO.this.getMyClazz().getSimpleName()), t, SqlQueryDAO.this.connection);
            stmt.executeUpdate();
            stmt.close();
        } catch (Exception e) {
            throw new DatasourceDeleteException(e.getMessage());
        }
    }

    @Override
    public void insert(T t) throws DatasourceInsertException {
        try {
            Query stmt = SqlQueryDAO.this.queryBuilder.buildInsert(TableMapper.getTableName(SqlQueryDAO.this.getMyClazz().getSimpleName()), t, SqlQueryDAO.this.connection);
            long key = stmt.executeUpdate().getKey(Long.class);
            if (key == 0) {
                throw new DatasourceInsertException("Error on inserting new value");
            }
            t.setId(key);

        } catch (Exception e) {
            throw new DatasourceInsertException(e);
        }
    }

    @Override
    public void update(T t) throws DatasourceUpdateException {
        try {
            Query stmt = SqlQueryDAO.this.queryBuilder.buildUpdate(TableMapper.getTableName(SqlQueryDAO.this.getMyClazz().getSimpleName()), t, SqlQueryDAO.this.connection);
            int res = stmt.executeUpdate().getResult();
            if (res == 0) {
                throw new DatasourceUpdateException("Nothing to update!");
            }
            stmt.close();
        } catch (Exception e) {
            throw new DatasourceUpdateException(e);
        }
    }

    @Override
    public T findBy(long id) throws DatasourceFindException {
        try {
            Query stmt = SqlQueryDAO.this.queryBuilder.buildFindByID(TableMapper.getTableName(SqlQueryDAO.this.getMyClazz().getSimpleName()), id, SqlQueryDAO.this.connection);
            T result = stmt.executeAndFetchFirst(SqlQueryDAO.this.getMyClazz());
            if (result == null) {
                throw new DatasourceFindException(DatasourceFindException.OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
            }
            return result;
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
        try {
            Query stmt = SqlQueryDAO.this.queryBuilder.buildFindBySearchParam(TableMapper.getTableName(SqlQueryDAO.this.getMyClazz().getSimpleName()), searchParams, SqlQueryDAO.this.connection, or);
            return stmt.executeAndFetch(SqlQueryDAO.this.getMyClazz());
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findAll() throws DatasourceFindException {
        try {
            Query stmt = SqlQueryDAO.this.queryBuilder.buildFindAll(TableMapper.getTableName(SqlQueryDAO.this.getMyClazz().getSimpleName()), SqlQueryDAO.this.connection);
            return stmt.executeAndFetch(SqlQueryDAO.this.getMyClazz());
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public T findByUniqueField(SearchParameter searchParameter) throws DatasourceFindException {
        try {
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(searchParameter);
            Query stmt = SqlQueryDAO.this.queryBuilder.buildFindBySearchParam(TableMapper.getTableName(SqlQueryDAO.this.getMyClazz().getSimpleName()), searchParams, SqlQueryDAO.this.connection, false);
            return stmt.executeAndFetchFirst(SqlQueryDAO.this.getMyClazz());
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    public boolean isTransaction() {
        return transaction;
    }

}
