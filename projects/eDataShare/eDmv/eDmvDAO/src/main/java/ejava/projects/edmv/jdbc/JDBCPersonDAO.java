package ejava.projects.edmv.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.dao.DAOException;
import ejava.projects.edmv.dao.PersonDAO;

/**
 * This class provides a _sparse_ example of a JDBC DAO providing a mapping
 * of objects related to people in the DB.
 * 
 * @author jcstaff
 *
 */
public class JDBCPersonDAO implements PersonDAO {
	private static Logger log = LoggerFactory.getLogger(JDBCPersonDAO.class);
	private Connection connection;
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void createPerson(Person person) 
	    throws DAOException {
		PreparedStatement statement1 = null;
        Statement statement2 = null;
		ResultSet rs = null;
				
		try {
            statement1 = connection.prepareStatement(
                    "INSERT INTO EDMV_PERSON " +
                    "(ID, LAST_NAME) " +
                    "VALUES (null, ?)");
            statement1.setString(1, person.getLastName());
		    statement1.execute();
		    
            Method setId = Person.class.getDeclaredMethod(
	                    "setId", new Class[]{long.class});
            setId.setAccessible(true);
                //this is HSQL-specific
                //gets its db-generated primary key
            statement2 = connection.createStatement();
            rs = statement2.executeQuery("call identity()");
            if (rs.next()) {
                long id = rs.getLong(1);
                setId.invoke(person, id);
            }
		}
		catch (SQLException ex) {
		    log.error("SQL error creating person", ex);
		    throw new DAOException("error creating person:"+ex, ex);
		}
		catch (Exception ex) {
		    log.error("error creating person", ex);
		    throw new DAOException("error creating person:"+ex, ex);
		}
		finally {
			try { rs.close(); } catch (Exception ignored) {}
			try { statement1.close(); } catch (Exception ignored) {}
			try { statement2.close(); } catch (Exception ignored) {}
		}
	}

    public Person getPerson(long id)
        throws DAOException {
        throw new DAOException("not implemented");
    }
    
	public List<Person> getPeople(int index, int count)
			throws DAOException {
		throw new DAOException("not implemented");
	}
}
