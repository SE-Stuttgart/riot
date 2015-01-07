package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.UserRoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.UserRole;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class UserRoleSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() throws DatasourceInsertException, DatasourceFindException, SQLException {
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.getConn(),false);
        UserRole testUserRole = new UserRole(new Long(1), new Long(2), new Long(42));
        dao.insert(testUserRole);
        UserRole findUserRole = dao.findBy(testUserRole.getId());
        assertEquals(findUserRole, findUserRole);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException, SQLException {
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.getConn(),false);
        UserRole findUserRole = dao.findBy(new Long(1));
        // UserRoles are not mutable at the time, only to test the update funktion.
        dao.update(findUserRole);
        UserRole findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findUserRole);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceDeleteException, DatasourceFindException, SQLException{
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.getConn(),false);
        UserRole UserRole = dao.findBy(new Long(1));
        dao.delete(UserRole);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException, SQLException{
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.getConn(),false);
        dao.update(new UserRole(new Long(32), new Long(32), new Long(32)));
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException{
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.getConn(),false);
        Collection<UserRole> UserRole = dao.findAll();
        assertEquals(3, UserRole.size());
    }

}
