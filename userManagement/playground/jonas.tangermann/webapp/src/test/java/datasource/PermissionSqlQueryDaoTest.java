package datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import datasource.common.DaoTestBase;
import de.uni_stuttgart.riot.data.exc.DatasourceDeleteException;
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.exc.DatasourceUpdateException;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.PermissionSqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.Permission;

public class PermissionSqlQueryDaoTest extends DaoTestBase{


	@Test
	public void insertAndFindTest() {
		PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.ds);
		try {
			Permission testPermission = new Permission(new Long(42), "TestPermission");
			dao.insert(testPermission);
			Permission findPermission = dao.findBy(new Long(42));
			System.out.println(testPermission);
			System.out.println(findPermission);
			assertEquals(testPermission, findPermission);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.ds);
		try {
			Permission findPermission = dao.findBy(new Long(1));
			findPermission.setPermissionValue("testPermissionName");
			dao.update(findPermission);
			Permission findAfterUpdate = dao.findBy(new Long(1));
			assertEquals(findAfterUpdate, findPermission);
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceUpdateException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void deleteTest(){
		PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.ds);
		try {
			Permission Permission = dao.findBy(new Long(1));
			dao.delete(Permission);
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
		PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.ds);
		try {
			dao.update(new Permission(new Long(32),""));
		} catch (DatasourceUpdateException e) {
			return;
		}
		fail("Should not ne reached because there is no item with id 32");
	}
	
	@Test
	public void findByParamTest(){
		PermissionSqlQueryDAO dao = new PermissionSqlQueryDAO(this.ds);
		LinkedList<String> param = new LinkedList<>();
		param.add("x");
		param.add("y");
		try {
			Collection<Permission> ps = dao.findBy(param);
			assertEquals(2, ps.size());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
}
