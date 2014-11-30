package datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.enterprise.inject.spi.PassivationCapable;

import org.junit.Test;

import datasource.common.DaoTestBase;
import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.RolePermissionSqlQueryDAO;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.UserSqlQueryDao;
import de.uni_stuttgart.riot.data.storable.RolePermission;
import de.uni_stuttgart.riot.data.storable.User;

public class RolePermissionSqlQueryDaoTest extends DaoTestBase{

	@Test
	public void insertAndFindTest() {
		RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(this.ds);
		try {
			RolePermission testrolePermission = new RolePermission(new Long(1), new Long(1), new Long(42));
			dao.insert(testrolePermission);
			RolePermission findRolePermission = dao.findBy(new Long(42));
			System.out.println(findRolePermission);
			assertEquals(findRolePermission, findRolePermission);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(this.ds);
		try {
			RolePermission findRolePermission = dao.findBy(new Long(1));
			// RolePermissions are not mutable at the time, only to test the update funktion.
			dao.update(findRolePermission);
			RolePermission findAfterUpdate = dao.findBy(new Long(1));
			assertEquals(findAfterUpdate, findRolePermission);
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceUpdateException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void deleteTest(){
		RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(this.ds);
		try {
			RolePermission rolePermission = dao.findBy(new Long(1));
			dao.delete(rolePermission);
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
		RolePermissionSqlQueryDAO dao = new RolePermissionSqlQueryDAO(this.ds);
		try {
			dao.update(new RolePermission(new Long(32), new Long(32), new Long(32)));
		} catch (DatasourceUpdateException e) {
			return;
		}
		fail("Should not ne reached because there is no item with id 32");
	}
	
}
