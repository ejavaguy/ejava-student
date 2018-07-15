package ejava.projects.eleague.bo;

import static org.junit.Assert.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.projects.eleague.bo.Address;

/**
 * This test case provides an example framework for how business objects 
 * could be tested. The functionality of the business objects should be
 * tested at this level prior to adding more complex scenarios, like 
 * persistence and server-side logic.
 * 
 * @author jcstaff
 *
 */
public class ELeagueBOTest {
	Logger log = LoggerFactory.getLogger(ELeagueBOTest.class);
	
	@Test
    public void testVenue() {
    	log.info("*** testVenue ***");
    	
    	String name = "myVenue";
    	String city = "Laurel";
    	
    	Venue venue = new Venue();
    	venue.setName(name);
    	
    	venue.setAddress(new Address(0, city));
    	
    	log.debug("created Venue:" + venue);
    	
    	assertEquals("unexpected name", name, venue.getName());
    	assertEquals("unexpected city", city, venue.getAddress().getCity());    	
    }
}
