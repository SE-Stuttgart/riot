package datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import datasource.common.DaoTestBase;
import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.UserRoleSqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.UserRole;

public class UserRoleSqlQueryDaoTest extends DaoTestBase {

    @Test
    public void insertAndFindTest() {
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.ds);
        try {
            UserRole testUserRole = new UserRole(new Long(1), new Long(1), new Long(42));
            dao.insert(testUserRole);
            UserRole findUserRole = dao.findBy(new Long(42));
            System.out.println(findUserRole);
            assertEquals(findUserRole, findUserRole);
        } catch (DatasourceInsertException e) {
            fail(e.getMessage());
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void FindUpdateFindTest() {
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.ds);
        try {
            UserRole findUserRole = dao.findBy(new Long(1));
            // UserRoles are not mutable at the time, only to test the update funktion.
            dao.update(findUserRole);
            UserRole findAfterUpdate = dao.findBy(new Long(1));
            assertEquals(findAfterUpdate, findUserRole);
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        } catch (DatasourceUpdateException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void deleteTest(){
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.ds);
        try {
            UserRole UserRole = dao.findBy(new Long(1));
            dao.delete(UserRole);
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        } catch (DatasourceDeleteException e) {
            fail(e.getMessage());
        }
        try {
            dao.findBy(new Long(1));
        } catch (DatasourceFindException e) {
            return;
        }
        fail("Should not be reached because id 1 does not longer exist");
    }

    @Test
    public void errorUpdateTest(){
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.ds);
        try {
            dao.update(new UserRole(new Long(32), new Long(32), new Long(32)));
        } catch (DatasourceUpdateException e) {
            return;
        }
        fail("Should not ne reached because there is no item with id 32");
    }

    @Test
    public void findAllTest(){
        UserRoleSqlQueryDAO dao = new UserRoleSqlQueryDAO(this.ds);
        try {
            Collection<UserRole> UserRole = dao.findAll();
            assertEquals(3, UserRole.size());
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        }
    }

}
