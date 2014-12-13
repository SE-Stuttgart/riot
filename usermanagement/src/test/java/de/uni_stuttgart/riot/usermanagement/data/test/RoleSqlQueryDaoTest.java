package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceFindException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.usermanagement.data.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class RoleSqlQueryDaoTest extends DaoTestBase{

    @Test
    public void insertAndFindTest() throws DatasourceFindException, DatasourceInsertException {
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
        Role testrole = new Role(new Long(42), "TestRole");
        dao.insert(testrole);
        Role findRole = dao.findBy(testrole.getId());
        assertEquals(testrole, findRole);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException {
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
        Role findRole = dao.findBy(new Long(1));
        findRole.setRoleName("testRoleName");
        dao.update(findRole);
        Role findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findRole);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceFindException, DatasourceDeleteException{
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
        Role Role = dao.findBy(new Long(1));
        dao.delete(Role);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException{
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
        dao.update(new Role(new Long(32),""));
    }

    @Test
    public void findAllTest() throws DatasourceFindException{
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
        Collection<Role> Role = dao.findAll();
        assertEquals(4, Role.size());
    }

}
