package ejava.projects.esales.jpa;

import static org.junit.Assert.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.esales.bo.Account;
import ejava.projects.esales.bo.Address;
import ejava.projects.esales.dao.AccountDAO;

/**
 * This test case provides an example of one might test the JPA DAO. It 
 * tests only a minor set of functionality. However, this can be used
 * as a starting point for more detailed tests. 
 *
 */
public class JPAAccountDAOTest extends JPADAOTestBase {
	private static Logger log = LoggerFactory.getLogger(JPAAccountDAO.class);
	private AccountDAO dao;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
	    dao = new JPAAccountDAO();
	    ((JPAAccountDAO)dao).setEntityManager(em);
		
		em.getTransaction().begin();
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
	public void testJPACreate() throws Exception {
		log.info("*** testJPACreate ***");
		
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
    	//and must leverage the JPA resources.
    	
    	em.flush();
    	assertTrue(em.contains(account));
    	em.clear();
    	em.getTransaction().commit();
    	assertFalse(em.contains(account));
    	
    	Account account2 = em.find(Account.class, userId);
      	log.debug("checking account...");

    	assertNotNull(account2);
    	assertEquals("unexpected first name", 
    			firstName, account2.getFirstName());
    	assertEquals("unexpected number of addresses", 
    			account.getAddresses().size(), account2.getAddresses().size());
    	
      	log.debug("account looked good, checking address info...");
    	for (Address address : account2.getAddresses()) {
    		if ("Shipping".equals(address.getName())) {
    			assertEquals("unexpected city", "Laurel", address.getCity());
    		}
    		else if ("Billing".equals(address.getName())) {
    			assertEquals("unexpected city", "Columbia", address.getCity());
    		}
    		else {
    			fail("unexpected address:" + address.getName());
    		}
    	}
      	log.debug("address info good too");
	}
}
