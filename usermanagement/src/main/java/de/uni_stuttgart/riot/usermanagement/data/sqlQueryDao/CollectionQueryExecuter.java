package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import java.util.Collection;

import org.sql2o.Connection;

public abstract class CollectionQueryExecuter<T> extends QueryExecuter{

    public CollectionQueryExecuter(Connection connection) {
        super(connection);
    }

    public abstract Collection<T> executeInternal() throws Exception;

    public Collection<T> execute(boolean transaction) throws Exception {
        if(transaction){
            try {
                return this.executeInternal();
            } catch (Exception e) {
                this.getConnection().rollback();
                this.getConnection().close();
                throw e;
            }
        } else {
            try {
                return this.executeInternal();
            } finally {
                this.getConnection().close();
            }
        }
    }
}
