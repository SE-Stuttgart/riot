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
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.RoleSqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.Role;

public class RoleSqlQueryDaoTest extends DaoTestBase{

	@Test
	public void insertAndFindTest() {
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.ds);
		try {
			Role testrole = new Role(new Long(42), "TestRole");
			dao.insert(testrole);
			Role findRole = dao.findBy(new Long(42));
			System.out.println(testrole);
			assertEquals(testrole, findRole);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.ds);
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
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.ds);
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
		RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.ds);
		try {
			dao.update(new Role(new Long(32),""));
		} catch (DatasourceUpdateException e) {
			return;
		}
		fail("Should not ne reached because there is no item with id 32");
	}
	
	@Test
    public void findAllTest(){
	    RoleSqlQueryDAO dao = new RoleSqlQueryDAO(this.ds);
        try {
            Collection<Role> Role = dao.findAll();
            assertEquals(4, Role.size());
        } catch (DatasourceFindException e) {
            fail(e.getMessage());
        }
    }

}
