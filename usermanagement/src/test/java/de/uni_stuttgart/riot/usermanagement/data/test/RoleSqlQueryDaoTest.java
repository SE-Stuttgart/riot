package de.uni_stuttgart.riot.usermanagement.data.test;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.Collection;

import org.junit.Test;

import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceDeleteException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceFindException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceInsertException;
import de.uni_stuttgart.riot.server.commons.db.exception.DatasourceUpdateException;
import de.uni_stuttgart.riot.usermanagement.data.dao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.usermanagement.data.storable.Role;
import de.uni_stuttgart.riot.usermanagement.data.test.common.DaoTestBase;

public class RoleSqlQueryDaoTest extends DaoTestBase{

    @Test
    public void insertAndFindTest() throws DatasourceFindException, DatasourceInsertException, SQLException {
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.getConn(),false);
        Role testrole = new Role(new Long(42), "TestRole");
        dao.insert(testrole);
        Role findRole = dao.findBy(testrole.getId());
        assertEquals(testrole, findRole);
    }

    @Test
    public void FindUpdateFindTest() throws DatasourceUpdateException, DatasourceFindException, SQLException {
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.getConn(),false);
        Role findRole = dao.findBy(new Long(1));
        findRole.setRoleName("testRoleName");
        dao.update(findRole);
        Role findAfterUpdate = dao.findBy(new Long(1));
        assertEquals(findAfterUpdate, findRole);
    }

    @Test(expected = DatasourceFindException.class)
    public void deleteTest() throws DatasourceFindException, DatasourceDeleteException, SQLException{
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.getConn(),false);
        Role Role = dao.findBy(new Long(1));
        dao.delete(Role);
        dao.findBy(new Long(1));
    }

    @Test(expected = DatasourceUpdateException.class)
    public void errorUpdateTest() throws DatasourceUpdateException, SQLException{
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.getConn(),false);
        dao.update(new Role(new Long(32),""));
    }

    @Test
    public void findAllTest() throws DatasourceFindException, SQLException{
        RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.getConn(),false);
        Collection<Role> Role = dao.findAll();
        assertEquals(4, Role.size());
    }

}
