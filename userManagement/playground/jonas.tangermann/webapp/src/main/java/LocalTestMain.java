
import de.uni_stuttgart.riot.data.exc.DatasourceFindException;
import de.uni_stuttgart.riot.data.exc.DatasourceInsertException;
import de.uni_stuttgart.riot.data.memoryDao.MemoryDAO;
import de.uni_stuttgart.riot.data.storable.User;


public class LocalTestMain {

	public static void main(String[] args) {
		MemoryDAO<User> userMemDAO = new MemoryDAO<User>();
		try {
			userMemDAO.insert(new User(new Long(1), "Jonas"));
			User user = userMemDAO.findBy(1);
			System.out.println(user);
		} catch (DatasourceInsertException e) {
			e.printStackTrace();
		} catch (DatasourceFindException e) {
			e.printStackTrace();
		}
		
	}

}
