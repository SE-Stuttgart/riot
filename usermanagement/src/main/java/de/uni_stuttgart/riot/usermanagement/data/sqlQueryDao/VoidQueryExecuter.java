package de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao;

import org.sql2o.Connection;


public abstract class VoidQueryExecuter extends QueryExecuter{

    public VoidQueryExecuter(Connection connection) {
        super(connection);
    }

    public abstract void executeInternal() throws Exception;

    public void execute(boolean transaction) throws Exception {
        if(transaction){
            try {
                this.executeInternal();
            } catch (Exception e) {
                this.getConnection().rollback();
                this.getConnection().close();
                throw e;
            }
        } else {
            try {
                this.executeInternal();
            } finally {
                this.getConnection().close();
            }
        }
    }
}
