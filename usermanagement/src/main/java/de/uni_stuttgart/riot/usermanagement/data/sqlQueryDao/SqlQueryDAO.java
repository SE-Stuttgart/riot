package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import javax.sql.DataSource;

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

    private final DataSource ds;
    private final Connection connection;
    private final QueryBuilder<T> queryBuilder;
    private final ObjectBuilder<T> objectBuilder;
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
    public SqlQueryDAO(DataSource ds, QueryBuilder<T> queryBuilder, ObjectBuilder<T> objectBuilder) {
        this.ds = ds;
        this.connection = null;
        this.queryBuilder = queryBuilder;
        this.objectBuilder = objectBuilder;
        this.transaction = false;
    }

    protected SqlQueryDAO(Connection connection, QueryBuilder<T> queryBuilder, ObjectBuilder<T> objectBuilder) throws SQLException {
        this.ds = null;
        this.transaction = true;
        this.connection = connection;
        this.connection.setAutoCommit(false);
        this.queryBuilder = queryBuilder;
        this.objectBuilder = objectBuilder;
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            return this.ds.getConnection();
        } else {
            return connection;
        }
    }

    @Override
    public void delete(T t) throws DatasourceDeleteException {
        try {
            VoidQueryExecuter ex = new VoidQueryExecuter(this.getConnection()) {
                public void executeInternal() throws Exception {
                    PreparedStatement stmt = SqlQueryDAO.this.queryBuilder.buildDelete(t, this.getConnection());
                    stmt.execute();
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
            VoidQueryExecuter ex = new VoidQueryExecuter(this.getConnection()) {
                public void executeInternal() throws Exception {
                    try (PreparedStatement stmt = SqlQueryDAO.this.queryBuilder.buildInsert(t, this.getConnection())) {
                        if (stmt.executeUpdate() == 0) {
                            throw new DatasourceInsertException("Error on inserting new value");
                        }
                        try (ResultSet rSet = stmt.getGeneratedKeys()) {
                            if (rSet.next()) {
                                t.setId(rSet.getLong(1));
                            } else {
                                throw new DatasourceInsertException("Error on getting the id-value");
                            }
                            rSet.close();
                        }
                        stmt.close();

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
            VoidQueryExecuter ex = new VoidQueryExecuter(this.getConnection()) {
                public void executeInternal() throws Exception {
                    PreparedStatement stmt = SqlQueryDAO.this.queryBuilder.buildUpdate(t, this.getConnection());
                    int res = stmt.executeUpdate();
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
            SingleQueryExecuter<T> exc = new SingleQueryExecuter<T>(this.getConnection()) {
                public T executeInternal() throws Exception {
                    PreparedStatement stmt = SqlQueryDAO.this.queryBuilder.buildFindByID(id, this.getConnection());
                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        T result = SqlQueryDAO.this.objectBuilder.build(resultSet);
                        stmt.close();
                        return result;
                    } else {
                        stmt.close();
                        throw new DatasourceFindException(DatasourceFindException.OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
                    }
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
            CollectionQueryExecuter<T> exc = new CollectionQueryExecuter<T>(this.getConnection()) {
                public Collection<T> executeInternal() throws Exception {
                    PreparedStatement stmt = SqlQueryDAO.this.queryBuilder.buildFindBySearchParam(searchParams, this.getConnection(), or);
                    ResultSet resultSet = stmt.executeQuery();
                    LinkedList<T> res = new LinkedList<T>();
                    while (resultSet.next()) {
                        res.add(SqlQueryDAO.this.objectBuilder.build(resultSet));
                    }
                    stmt.close();
                    return res;
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
            CollectionQueryExecuter<T> exc = new CollectionQueryExecuter<T>(this.getConnection()) {
                public Collection<T> executeInternal() throws Exception {
                    PreparedStatement stmt = SqlQueryDAO.this.queryBuilder.buildFindAll(this.getConnection());
                    ResultSet resultSet = stmt.executeQuery();
                    ArrayList<T> res = new ArrayList<T>();
                    while (resultSet.next()) {
                        res.add(SqlQueryDAO.this.objectBuilder.build(resultSet));
                    }
                    stmt.close();
                    return res;
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
            SingleQueryExecuter<T> exc = new SingleQueryExecuter<T>(this.getConnection()) {
                public T executeInternal() throws Exception {
                    Collection<SearchParameter> searchParams = new ArrayList<SearchParameter>();
                    searchParams.add(searchParameter);
                    PreparedStatement stmt = SqlQueryDAO.this.queryBuilder.buildFindBySearchParam(searchParams, this.getConnection(), false);
                    ResultSet resultSet = stmt.executeQuery();
                    if (resultSet.next()) {
                        T result = SqlQueryDAO.this.objectBuilder.build(resultSet);
                        stmt.close();
                        return result;
                    } else {
                        stmt.close();
                        throw new DatasourceFindException("No such Element");
                    }
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
