package de.uni_stuttgart.riot.server.commons.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.commons.rest.data.FilteredRequest;
import de.uni_stuttgart.riot.commons.rest.data.Storable;
import de.uni_stuttgart.riot.commons.rest.data.TableName;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.server.commons.db.exception.NotFoundException;
import de.uni_stuttgart.riot.server.commons.rest.PaginatedCollection;

/**
 * 
 * Superclass for all SQL-Query based {@link DAO}s.
 * 
 * @author Jonas Tangermann
 *
 * @param <T>
 */
public abstract class SqlQueryDAO<T extends Storable> implements DAO<T> {

    private final QueryBuilderImpl queryBuilder;

    /**
     * Constructor.
     * 
     */
    public SqlQueryDAO() {
        this.queryBuilder = new QueryBuilderImpl();
    }

    /**
     * Opens or retrieves the connection to be used by this DAO. Please always use this method since it might change to use transactions in
     * the future.
     * 
     * @return The connection.
     * @throws SQLException
     *             When opening a connection fails.
     */
    protected Connection getConnection() throws SQLException {
        return ConnectionMgr.openConnection();
    }

    private String getTableName() {
        TableName tableName = getMyClazz().getAnnotation(TableName.class);
        if (tableName == null) {
            return getMyClazz().getSimpleName();
        }
        return tableName.value();
    }
    
    /**
     * gets the class type.
     * 
     * @return the Class type.
     */
    protected abstract Class<T> getMyClazz();

    @Override
    public void delete(T t) throws DatasourceDeleteException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildDelete(getTableName(), t, connection);
            int res = stmt.executeUpdate().getResult();
            if (res == 0) {
                throw new DatasourceDeleteException("Nothing to delete!");
            }
            stmt.close();
        } catch (SQLException e) {
            throw new DatasourceDeleteException(e);
        }
    }

    @Override
    public void delete(long id) throws DatasourceDeleteException {
        try {
            T t = getMyClazz().newInstance();
            t.setId(id);
            delete(t);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new DatasourceDeleteException(e);
        }

    }

    @Override
    public void insert(T t) throws DatasourceInsertException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildInsert(getTableName(), t, connection);
            long key = stmt.executeUpdate().getKey(Long.class);
            if (key == 0) {
                throw new DatasourceInsertException("Error on inserting new value");
            }
            t.setId(key);

        } catch (SQLException e) {
            throw new DatasourceInsertException(e);
        }
    }

    @Override
    public void update(T t) throws DatasourceUpdateException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildUpdate(getTableName(), t, connection);
            int res = stmt.executeUpdate().getResult();
            if (res == 0) {
                throw new DatasourceUpdateException("Nothing to update!");
            }
            stmt.close();
        } catch (SQLException e) {
            throw new DatasourceUpdateException(e);
        }
    }

    @Override
    public T findBy(long id) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindByID(getTableName(), id, connection);
            T result = stmt.executeAndFetchFirst(getMyClazz());
            if (result == null) {
                throw new NotFoundException();
            }
            return result;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindBySearchParam(getTableName(), searchParams, connection, or);
            return stmt.executeAndFetch(getMyClazz());
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findAll() throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindAll(getTableName(), connection);
            return stmt.executeAndFetch(getMyClazz());
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findAll(final int offset, final int limit) throws DatasourceFindException {
        if (offset < 0 || limit < 1) {
            throw new DatasourceFindException("Invalid parameter value");
        }
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindWithPagination(getTableName(), connection, offset, limit);
            // pagination
            PaginatedCollection<T> resultWithPag = new PaginatedCollection<T>(stmt.executeAndFetch(getMyClazz()));
            resultWithPag.setLimit(limit);
            resultWithPag.setOffset(offset);

            stmt = queryBuilder.buildGetTotal(getTableName(), connection); // FIXME This class needs refactoring. For example, all stmts
                                                                           // must be closed.
            resultWithPag.setTotal(stmt.executeAndFetchFirst(Integer.class));

            return resultWithPag;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findAll(FilteredRequest filter) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Query stmt = queryBuilder.buildFindWithFiltering(getTableName(), connection, filter, getMyClazz());
            Collection<T> result = stmt.executeAndFetch(getMyClazz());

            // pagination
            if (filter.getLimit() > 0 && filter.getOffset() >= 0) {
                PaginatedCollection<T> resultWithPag = new PaginatedCollection<T>(result);
                resultWithPag.setLimit(filter.getLimit());
                resultWithPag.setOffset(filter.getOffset());
                stmt = queryBuilder.buildTotalFoundElements(getTableName(), connection, filter, getMyClazz());

                resultWithPag.setTotal(stmt.executeAndFetchFirst(Integer.class));
                return resultWithPag;
            } else {
                return result;
            }

        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public T findByUniqueField(SearchParameter searchParameter) throws DatasourceFindException {
        try (Connection connection = getConnection()) {
            Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
            searchParams.add(searchParameter);
            Query stmt = queryBuilder.buildFindBySearchParam(getTableName(), searchParams, connection, false);
            T result = stmt.executeAndFetchFirst(getMyClazz());
            if (result == null) {
                throw new NotFoundException();
            }
            return result;
        } catch (SQLException e) {
            throw new DatasourceFindException(e);
        }
    }
}
