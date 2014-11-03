package ejava.projects.edmv.jdbc;

import static org.junit.Assert.*;


import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.projects.edmv.bo.Person;
import ejava.projects.edmv.dao.PersonDAO;

/**
 * This test case provides an example of one might test the JDBC DAO. It 
 * provides most of the basics, knowing the DAO only implemented one method.
 * As more tests are added, the implementation show here would benefit from
 * re-usable techniques to check the values within the database.
 * 
 * @author jcstaff
 *
 */
public class JDBCPersonDAOTest extends JDBCDAOTestBase {
	static Log log = LogFactory.getLog(JDBCPersonDAOTest.class);
	PersonDAO dao;
	
	/**
	 * Delegate common setUp tasks to parent and handle specifics of 
	 * PersonDAO here.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
	    dao = new JDBCPersonDAO();
	    ((JDBCPersonDAO)dao).setConnection(connection);
	}
	
	/**
	 * Delegate common tearDown tasks to parent and handle specifics of 
	 * PersonDAO here.
	 */
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	    ((JDBCPersonDAO)dao).setConnection(null);
	}
	
	
	/**
	 * This method tests a single create into the database using the DAO. 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJDBCCreate() throws Exception {
		log.info("*** testJDBCCreate ***");
		
    	String lastName = "foo";
    	
    	Person person = new Person();
    	person.setLastName(lastName);
    	
    	log.debug("instantiated Person:" + person);
    	assertEquals("unexpected initial ID", 0, person.getId());
    	
    	dao.createPerson(person);
    	log.debug("dao created Person:" + person);
        assertFalse("unexpected generated PK", 0 == person.getId());
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JDBC resources.
    	
    	connection.commit();
    	Statement statement = null;
    	ResultSet rs = null;
    	try {
    	    statement = connection.createStatement();
    	    
        	log.debug("getting core person info...");
    	    rs = statement.executeQuery(
    	    		"SELECT LAST_NAME FROM EDMV_PERSON WHERE ID='" 
    	    		+ person.getId() + "'");
    	    if (rs.next()) {
    	    	String name = rs.getString("LAST_NAME");
    	    	assertEquals("unexpected last name", person.getLastName(), name);
    	    }
    	    else {
    	    	fail("no person found");
    	    }
    	    
        	log.debug("JDBC person looks good");
    	}
    	finally {
    		if (rs != null) { rs.close(); }
    		if (statement != null) { statement.close(); }
    	}
	}
}
