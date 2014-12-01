package de.uni_stuttgart.riot.data.sqlquerydao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import javax.sql.DataSource;

import de.uni_stuttgart.riot.data.DAO;
import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.storable.Storable;

/**
 * 
 * Superclass for all SQL-Query based {@link DAO}s. 
 * @author Jonas Tangermann
 *
 * @param <T>
 */
public abstract class SqlQueryDAO<T extends Storable> implements DAO<T> {

    private final DataSource ds;
    private final QueryBuilder<T> queryBuilder;
    private final ObjectBuilder<T> objectBuilder;

    /**
     * Constructor.
     * @param ds the SQL-Datasource that should be used.
     * @param queryBuilder the querybuilder for T
     * @param objectBuilder the objectbuilder for T
     */
    public SqlQueryDAO(DataSource ds, QueryBuilder<T> queryBuilder, ObjectBuilder<T> objectBuilder) {
        this.ds = ds;
        this.queryBuilder = queryBuilder;
        this.objectBuilder = objectBuilder;
    }

    @Override
    public void delete(T t) throws DatasourceDeleteException {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            PreparedStatement stmt = this.queryBuilder.buildDelete(t, connection);
            stmt.execute();
        } catch (SQLException e) {
            throw new DatasourceDeleteException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatasourceDeleteException(e.getMessage());
            }
        }
    }

    @Override
    public void insert(T t) throws DatasourceInsertException {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            PreparedStatement stmt = this.queryBuilder.buildInsert(t, connection);
            stmt.execute();
        } catch (SQLException e) {
            throw new DatasourceInsertException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatasourceInsertException(e.getMessage());
            }
        }
    }

    @Override
    public void update(T t) throws DatasourceUpdateException {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            PreparedStatement stmt = this.queryBuilder.buildUpdate(t, connection);
            int res = stmt.executeUpdate();
            if (res == 0) {
                throw new DatasourceUpdateException("Nothing to update!");
            }
        } catch (SQLException e) {
            throw new DatasourceUpdateException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatasourceUpdateException(e.getMessage());
            }
        }
    }

    @Override
    public T findBy(long id) throws DatasourceFindException {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            PreparedStatement stmt = this.queryBuilder.buildFindByID(id, connection);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                return this.objectBuilder.build(resultSet);
            }
        } catch (SQLException e) {
            throw new DatasourceFindException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatasourceFindException(e.getMessage());
            }
        }
        throw new DatasourceFindException(DatasourceFindException.OBJECT_DOES_NOT_EXIST_IN_DATASOURCE);
    }

    @Override
    public Collection<T> findBy(Collection<SearchParameter> searchParams, boolean or) throws DatasourceFindException {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            PreparedStatement stmt = this.queryBuilder.buildFindBySearchParam(searchParams, connection, or);
            ResultSet resultSet = stmt.executeQuery();
            LinkedList<T> res = new LinkedList<T>();
            while (resultSet.next()) {
                res.add(this.objectBuilder.build(resultSet));
            }
            return res;
        } catch (SQLException e) {
            throw new DatasourceFindException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatasourceFindException(e.getMessage());
            }
        }
    }

    @Override
    public Collection<T> findAll() throws DatasourceFindException {
        Connection connection = null;
        try {
            connection = ds.getConnection();
            PreparedStatement stmt = this.queryBuilder.buildFindAll(connection);
            ResultSet resultSet = stmt.executeQuery();
            LinkedList<T> res = new LinkedList<T>();
            while (resultSet.next()) {
                res.add(this.objectBuilder.build(resultSet));
            }
            return res;
        } catch (SQLException e) {
            throw new DatasourceFindException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new DatasourceFindException(e.getMessage());
            }
        }
    }
}
