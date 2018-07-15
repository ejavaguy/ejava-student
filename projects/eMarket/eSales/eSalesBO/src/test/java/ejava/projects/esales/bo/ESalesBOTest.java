package ejava.projects.esales.bo;

import static org.junit.Assert.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

/**
 * This test case provides an example framework for how business objects 
 * could be tested. The functionality of the business objects should be
 * tested at this level prior to adding more complex scenarios, like 
 * persistence and server-side logic.
 * 
 * @author jcstaff
 *
 */
public class ESalesBOTest {
    private static final Logger log = LoggerFactory.getLogger(ESalesBOTest.class);
	
	@Test
    public void testAccount() {
    	log.info("*** testAccount ***");
    	
    	String userId = "foo";
    	String firstName = "bar";
    	
    	Account account = new Account(userId);
    	account.setFirstName(firstName);
    	account.getAddresses().add(new Address(0, "Shipping", "Laurel"));
    	account.getAddresses().add(new Address(0, "Billing", "Columbia"));
    	
    	log.debug("created Account:" + account);
    	
    	assertEquals("unexpected userId", userId, account.getUserId());
    	assertEquals("unexpected firstName", firstName, account.getFirstName());
    	assertEquals("unexpected # of addresses", 
    			2, account.getAddresses().size());
    	for(Address address : account.getAddresses()) {
    		if ("Shipping".equals(address.getName())) {
    			assertEquals("unexpected city", "Laurel", address.getCity());
    		}
    		else if ("Billing".equals(address.getName())) {
    			assertEquals("unexpected city", "Columbia", address.getCity());
    		}
    		else {
    			fail("unexpected address:" + address);
    		}
    	}    	
    }
}
