package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.sql2o.Connection;
import org.sql2o.Query;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Storable;
import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;

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
    private final QueryBuilder<T> queryBuilder;
    private final boolean transaction;

    /**
     * Constructor.
     * 
     * @param ds
     *            the SQL-Datasource that should be used.
     * @param queryBuilder
     *            the querybuilder for T
     * @param objectBuilder
     *            the objectbuilder for T
     */
    public SqlQueryDAO(QueryBuilder<T> queryBuilder, ObjectBuilder<T> objectBuilder) {
        this.connection = null;
        this.queryBuilder = queryBuilder;
        this.transaction = false;
    }

    protected abstract Class<T> getMyClazz();

    protected SqlQueryDAO(Connection connection, QueryBuilder<T> queryBuilder, ObjectBuilder<T> objectBuilder) throws SQLException {
        this.transaction = true;
        this.connection = connection;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public void delete(T t) throws DatasourceDeleteException {
        try {
            VoidQueryExecuter ex = new VoidQueryExecuter(this.connection) {
                public void executeInternal() throws Exception {
                    Query stmt = SqlQueryDAO.this.queryBuilder.buildDelete(t, SqlQueryDAO.this.connection);
                    stmt.executeUpdate();
                    stmt.close();
                }
            };
            ex.execute(this.isTransaction());
        } catch (Exception e) {
            throw new DatasourceDeleteException(e.getMessage());
        }
    }

    @Override
    public void insert(T t) throws DatasourceInsertException {
        try {
            VoidQueryExecuter ex = new VoidQueryExecuter(this.connection) {
                public void executeInternal() throws Exception {
                    try (Query stmt = SqlQueryDAO.this.queryBuilder.buildInsert(t, SqlQueryDAO.this.connection)) {
                        long key = stmt.executeUpdate().getKey(Long.class);
                        if (key == 0) {
                            throw new DatasourceInsertException("Error on inserting new value");
                        }
                        t.setId(key);
                    }
                }
            };
            ex.execute(this.isTransaction());
        } catch (Exception e) {
            throw new DatasourceInsertException(e);
        }
    }

    @Override
    public void update(T t) throws DatasourceUpdateException {
        try {
            VoidQueryExecuter ex = new VoidQueryExecuter(this.connection) {
                public void executeInternal() throws Exception {
                    Query stmt = SqlQueryDAO.this.queryBuilder.buildUpdate(t, SqlQueryDAO.this.connection);
                    int res = stmt.executeUpdate().getResult();
                    if (res == 0) {
                        throw new DatasourceUpdateException("Nothing to update!");
                    }
                    stmt.close();
                }
            };
            ex.execute(this.isTransaction());
        } catch (Exception e) {
            throw new DatasourceUpdateException(e);
        }
    }

    @Override
    public T findBy(long id) throws DatasourceFindException {
        try {
            SingleQueryExecuter<T> exc = new SingleQueryExecuter<T>(this.connection) {
                public T executeInternal() throws Exception {
                    Query stmt = SqlQueryDAO.this.queryBuilder.buildFindByID(id, SqlQueryDAO.this.connection);
                    return stmt.executeAndFetchFirst(SqlQueryDAO.this.getMyClazz());
                }
            };
            return exc.execute(this.isTransaction());
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
        try {
            CollectionQueryExecuter<T> exc = new CollectionQueryExecuter<T>(this.connection) {
                public Collection<T> executeInternal() throws Exception {
                    Query stmt = SqlQueryDAO.this.queryBuilder.buildFindBySearchParam(searchParams, SqlQueryDAO.this.connection, or);
                    return stmt.executeAndFetch(SqlQueryDAO.this.getMyClazz());
                }
            };
            return exc.execute(this.isTransaction());
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public Collection<T> findAll() throws DatasourceFindException {
        try {
            CollectionQueryExecuter<T> exc = new CollectionQueryExecuter<T>(this.connection) {
                public Collection<T> executeInternal() throws Exception {
                    Query stmt = SqlQueryDAO.this.queryBuilder.buildFindAll(SqlQueryDAO.this.connection);
                    return stmt.executeAndFetch(SqlQueryDAO.this.getMyClazz());
                }
            };
            return exc.execute(this.isTransaction());
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    @Override
    public T findByUniqueField(SearchParameter searchParameter) throws DatasourceFindException {
        try {
            SingleQueryExecuter<T> exc = new SingleQueryExecuter<T>(this.connection) {
                public T executeInternal() throws Exception {
                    Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
                    searchParams.add(searchParameter);
                    Query stmt = SqlQueryDAO.this.queryBuilder.buildFindBySearchParam(searchParams, SqlQueryDAO.this.connection, false);
                    return stmt.executeAndFetchFirst(SqlQueryDAO.this.getMyClazz());
                }
            };
            return exc.execute(this.isTransaction());
        } catch (Exception e) {
            throw new DatasourceFindException(e);
        }
    }

    public boolean isTransaction() {
        return transaction;
    }

}
