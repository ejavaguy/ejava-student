package ejava.projects.esales.jdbc;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.esales.bo.Account;
import ejava.projects.esales.bo.Address;
import ejava.projects.esales.dao.AccountDAO;

/**
 * This test case provides an example of one might test the JDBC DAO. It 
 * provides most of the basics, knowing the DAO only implemented one method.
 * As more tests are added, the implementation show here would benefit from
 * re-usable techniques to check the values within the database.
 *
 */
public class JDBCAccountDAOTest extends JDBCDAOTestBase {
	private static Logger log = LoggerFactory.getLogger(JDBCAccountDAO.class);

	private AccountDAO dao;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();

		dao = new JDBCAccountDAO();
	    ((JDBCAccountDAO)dao).setConnection(connection);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	    ((JDBCAccountDAO)dao).setConnection(null);
	}
	
	/**
	 * This method tests a single create into the database using the DAO. 
	 * This tests some core functionality, but clearly more types of inserts
	 * should also be tested. For example, what happens when the same userId 
	 * is added a second time.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testJDBCCreate() throws Exception {
		log.info("*** testJDBCCreate ***");
		
    	String userId = "foo";
    	String firstName = "bar";
    	
    	Account account = new Account(userId);
    	account.setFirstName(firstName);
    	account.getAddresses().add(new Address(0, "Shipping", "Laurel"));
    	account.getAddresses().add(new Address(0, "Billing", "Columbia"));
    	
    	log.debug("instantiated Account:" + account);
    	
    	dao.createAccount(account);
    	log.debug("dao created Account:" + account);
    	
    	//up to here the client was ignorant of the technology used, the 
    	//rest of this is used to test what should have happened above 
    	//and must leverage the JDBC resources.
    	
    	connection.commit();
    	Statement statement = null;
    	ResultSet rs = null;
    	try {
    	    statement = connection.createStatement();
    	    
        	log.debug("getting core account info...");
    	    rs = statement.executeQuery(
    	    		"SELECT FIRST_NAME FROM ESALES_ACCT WHERE USER_ID='" 
    	    		+ userId + "'");
    	    if (rs.next()) {
    	    	String fName = rs.getString("FIRST_NAME");
    	    	assertEquals("unexpected first name", firstName, fName);
    	    }
    	    else {
    	    	fail("no account found");
    	    }
    	    
        	log.debug("core account good, checking addresses...");
    	    rs = statement.executeQuery(
    	    	"SELECT NAME, CITY " +
    	    	"FROM ESALES_ACCT, ESALES_ADDRESS, ESALES_ACCT_ADDRESS_LINK " +
    	    	"WHERE ESALES_ACCT_ADDRESS_LINK.USER_ID='" + userId + "' " +
    	    		"AND ESALES_ACCT_ADDRESS_LINK.ADDRESS_ID = " +
    	    		"ESALES_ADDRESS.ID");
    	    int count=0;
    	    while (rs.next()) {
    	    	count += 1;
    	    	String name = rs.getString("NAME");
    	    	String city = rs.getString("CITY");
        		if ("Shipping".equals(name)) {
        			assertEquals("unexpected city", "Laurel", city);
        		}
        		else if ("Billing".equals(name)) {
        			assertEquals("unexpected city", "Columbia", city);
        		}
        		else {
        			fail("unexpected address:" + name);
        		}
    	    }
    	    assertEquals("unexpected number of addresses", 
    	    		account.getAddresses().size(), count);
    	    
        	log.debug("address info good too");
    	}
    	finally {
    		if (rs != null) { rs.close(); }
    		if (statement != null) { statement.close(); }
    	}
	}
}
