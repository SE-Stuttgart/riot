package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.commons.rest.usermanagement.data.Role;
import de.uni_stuttgart.riot.commons.test.TestData;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class RoleSqlQueryDaoTest extends DaoTestBase {

    private RoleSqlQueryDAO dao = new RoleSqlQueryDAO();

    @Test
    public void insertAndFindTest() throws DatasourceFindException, DatasourceInsertException, SQLException {
        Role testrole = new Role(new Long(42), "TestRole");
        dao.insert(testrole);
        Role findRole = dao.findBy(testrole.getId());
        assertEquals(testrole, findRole);
    }

    @Test
    public void findUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException, SQLException {
        Role findRole = dao.findBy(new Long(1));
        findRole.setRoleName("testRoleName");
        dao.update(findRole);
        Role findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findRole);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceFindException, DatasourceDeleteException, SQLException {
        Role role = dao.findBy(new Long(1));
        dao.delete(role);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException, SQLException {
        dao.update(new Role(new Long(32), ""));
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException {
        Collection<Role> role = dao.findAll();
        assertEquals(4, role.size());
    }

}
