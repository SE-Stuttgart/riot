package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.*;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.DAO;
import de.uni_stuttgart.riot.usermanagement.data.DatasourceUtil;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.SqlTransaction;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.User;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class TransactionTest extends DaoTestBase {

    @Test
    public void transactionErrorTest() {
        try {
            // Open Transaction
            SqlTransaction transaction = new SqlTransaction(getDataSource());
            // Getting the DAO's
            DAO<User> userDAO = transaction.getUserDao();
            User yoda = userDAO.findBy(1L);
            // Is OK
            yoda.setUsername("Yoda2");
            userDAO.update(yoda);
            // FAILS because Vader assigned as username
            yoda.setUsername("Vader");
            userDAO.update(yoda);
            // Close Transaction
            transaction.commit();
        } catch (SQLException e) {
            fail();
        } catch (DatasourceFindException e) {
            fail();
        } catch (DatasourceUpdateException e) {
            DAO<User> userDAO;
            try {
                userDAO = new UserSqlQueryDao(getDataSource());
                User yoda = userDAO.findBy(1L);
                assertEquals("Yoda", yoda.getUsername());
            } catch (DatasourceFindException e1) {
                e1.printStackTrace();
                fail(e1.getMessage());
            }

        }
    }
    
    @Test
    public void transactionRollbackTest() {
        try {
            SqlTransaction transaction = new SqlTransaction(getDataSource());
            DAO<User> userDAO = transaction.getUserDao();
            User yoda = userDAO.findBy(1L);
            yoda.setUsername("Yoda2");
            userDAO.update(yoda);
            User vader = userDAO.findBy(3L);
            vader.setUsername("Vader2");
            userDAO.update(vader);
            transaction.rollback();
            userDAO = new UserSqlQueryDao(getDataSource());
            User yodaF = userDAO.findBy(1L);
            User vaderF = userDAO.findBy(3L);
            assertEquals("Yoda", yodaF.getUsername());
            assertEquals("Vader", vaderF.getUsername());
        } catch (SQLException e) {
            fail();
        } catch (DatasourceFindException e) {
            fail();
        } catch (DatasourceUpdateException e) {
            fail();
        }
    }

    @Test
    public void transactionCommitTest() {
        try {
            SqlTransaction transaction = new SqlTransaction(getDataSource());
            DAO<User> userDAO = transaction.getUserDao();
            User yoda = userDAO.findBy(1L);
            yoda.setUsername("Yoda2");
            userDAO.update(yoda);
            User vader = userDAO.findBy(3L);
            vader.setUsername("Vader2");
            userDAO.update(vader);
            transaction.commit();
            userDAO = new UserSqlQueryDao(getDataSource());
            User yodaF = userDAO.findBy(1L);
            User vaderF = userDAO.findBy(3L);
            assertEquals("Yoda2", yodaF.getUsername());
            assertEquals("Vader2", vaderF.getUsername());
        } catch (SQLException e) {
            fail();
        } catch (DatasourceFindException e) {
            fail();
        } catch (DatasourceUpdateException e) {
            fail();
        }
    }
}
