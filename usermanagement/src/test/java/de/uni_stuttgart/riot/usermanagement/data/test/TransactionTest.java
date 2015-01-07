package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.junit.Test;

import de.uni_stuttgart.riot.server.commons.db.DAO;
import de.uni_stuttgart.riot.server.commons.db.TransactionInterface;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.SqlTransaction;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.usermanagement.data.storable.UMUser;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class TransactionTest extends DaoTestBase {

    @Test
    public void transactionErrorTest() throws DatasourceFindException, SQLException {
        try {
         // Open Transaction
            SqlTransaction transaction = new SqlTransaction();
            // Getting the DAO's
            DAO<UMUser> userDAO = transaction.getUserDao();
            UMUser yoda = userDAO.findBy(1L);
            // Is OK
            yoda.setUsername("Yoda2");
            userDAO.update(yoda);
            // FAILS because Vader assigned as username
            yoda.setUsername("Vader");
            userDAO.update(yoda);
            // Close Transaction
            transaction.commit();
        } catch (Exception e) {
            DAO<UMUser> userDAO1 = new UserSqlQueryDao(this.getConn(),false);
            UMUser yoda1 = userDAO1.findBy(1L);
            assertEquals("Yoda", yoda1.getUsername());
        }
    }

    @Test
    public void transactionRollbackTest() throws DatasourceUpdateException, SQLException, DatasourceFindException, NamingException {
    	SqlTransaction transaction = new SqlTransaction();
        DAO<UMUser> userDAO = transaction.getUserDao();
        UMUser yoda = userDAO.findBy(1L);
        yoda.setUsername("Yoda2");
        userDAO.update(yoda);
        UMUser vader = userDAO.findBy(3L);
        vader.setUsername("Vader2");
        userDAO.update(vader);
        transaction.rollback();
        userDAO = new UserSqlQueryDao(this.getConn(),false);
        UMUser yodaF = userDAO.findBy(1L);
        UMUser vaderF = userDAO.findBy(3L);
        assertEquals("Yoda", yodaF.getUsername());
        assertEquals("Vader", vaderF.getUsername());
    }

    @Test
    public void transactionCommitTest() throws DatasourceUpdateException, SQLException, DatasourceFindException, NamingException {
    	SqlTransaction transaction = new SqlTransaction();
        DAO<UMUser> userDAO = transaction.getUserDao();
        UMUser yoda = userDAO.findBy(1L);
        yoda.setUsername("Yoda2");
        userDAO.update(yoda);
        UMUser vader = userDAO.findBy(3L);
        vader.setUsername("Vader2");
        userDAO.update(vader);
        transaction.commit();
        userDAO = new UserSqlQueryDao(this.getConn(),false);
        UMUser yodaF = userDAO.findBy(1L);
        UMUser vaderF = userDAO.findBy(3L);
        assertEquals("Yoda2", yodaF.getUsername());
        assertEquals("Vader2", vaderF.getUsername());
    }
}
