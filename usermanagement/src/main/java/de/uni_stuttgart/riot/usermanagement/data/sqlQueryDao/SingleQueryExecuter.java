package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import org.sql2o.Connection;


public abstract class SingleQueryExecuter<T> extends QueryExecuter{

    public SingleQueryExecuter(Connection connection) {
        super(connection);
    }

    public abstract T executeInternal() throws Exception;

    public T execute(boolean transaction) throws Exception {
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
