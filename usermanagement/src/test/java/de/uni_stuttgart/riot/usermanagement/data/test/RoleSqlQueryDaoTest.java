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
	public void insertAndFindTest() {
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
		try {
			Role testrole = new Role(new Long(42), "TestRole");
			dao.insert(testrole);
			Role findRole = dao.findBy(testrole.getId());
			assertEquals(testrole, findRole);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
		try {
			Role findRole = dao.findBy(new Long(1));
			findRole.setRoleName("testRoleName");
			dao.update(findRole);
			Role findAfterUpdate = dao.findBy(new Long(1));
			assertEquals(findAfterUpdate, findRole);
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceUpdateException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void deleteTest(){
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
		try {
			Role Role = dao.findBy(new Long(1));
			dao.delete(Role);
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
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
		try {
			dao.update(new Role(new Long(32),""));
		} catch (DatasourceUpdateException e) {
			return;
		}
		fail("Should not ne reached because there is no item with id 32");
	}
	
	@Test
    public void findAllTest(){
	    RoleSqlQueryDAO dao = new RoleSqlQueryDAO(getDataSource());
        try {
            Collection<Role> Role = dao.findAll();
            assertEquals(4, Role.size());
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        }
    }

}
