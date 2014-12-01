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
import de.uni_stuttgart.riot.data.sqlQueryDao.impl.TokenRoleSqlQueryDAO;
import de.uni_stuttgart.riot.data.storable.TokenRole;

public class TokenRoleSqlQueryDaoTest extends DaoTestBase{

	@Test
	public void insertAndFindTest() {
		TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.ds);
		try {
			TokenRole testtokenRole = new TokenRole(new Long(42), new Long(1), new Long(1));
			dao.insert(testtokenRole);
			TokenRole find = dao.findBy(new Long(42));
			System.out.println(find);
			assertEquals(find, testtokenRole);
		} catch (DatasourceInsertException e) {
			fail(e.getMessage());
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void FindUpdateFindTest() {
		TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.ds);
		try {
			TokenRole find = dao.findBy(new Long(1));
			// TokenRoles are not mutable at the time, only to test the update funktion.
			dao.update(find);
			TokenRole findAfterUpdate = dao.findBy(new Long(1));
			assertEquals(findAfterUpdate, find);
		} catch (DatasourceFindException e) {
			fail(e.getMessage());
		} catch (DatasourceUpdateException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void deleteTest(){
		TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.ds);
		try {
			TokenRole TokenRole = dao.findBy(new Long(1));
			dao.delete(TokenRole);
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
		TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.ds);
		try {
			dao.update(new TokenRole(new Long(32), new Long(32), new Long(32)));
		} catch (DatasourceUpdateException e) {
			return;
		}
		fail("Should not ne reached because there is no item with id 32");
	}
	
	  @Test
      public void findAllTest(){
	      TokenRoleSqlQueryDAO dao = new TokenRoleSqlQueryDAO(this.ds);
          try {
              Collection<TokenRole> TokenRole = dao.findAll();
              assertEquals(4, TokenRole.size());
          } catch (DatasourceFindException e) {
              fail(e.getMessage());
          }
      }
	
}
