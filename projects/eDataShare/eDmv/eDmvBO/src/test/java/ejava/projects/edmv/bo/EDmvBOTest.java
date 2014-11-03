package ejava.projects.edmv.bo;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * This test case provides a _sparse_ example of testing of business
 * object functionality.
 * 
 * @author jcstaff
 *
 */
public class EDmvBOTest {
    private static Log log = LogFactory.getLog(EDmvBOTest.class);

    @Test
    public void testPerson() {
        log.info("*** testPerson() ***");
        long id = 23;
        String lastName = "Smith";
        Person p = new Person(23);
        p.setLastName(lastName);
        
        log.debug("created person:" + p);
        assertEquals("unexpected id", id, p.getId());
        assertEquals("unexpected lastname", lastName, p.getLastName());
    }
    
    @Test
    public void testVehicleRegistration() {
        log.info("*** testVehicleRegistration() ***");
        
        Person owner1 = new Person(1);
        owner1.setLastName("owner1");
        log.debug("created owner:" + owner1);
        
        Person owner2 = new Person(2);
        owner2.setLastName("owner2");
        log.debug("created owner:" + owner2);
        
        long id = 100;
        String vin = "1234";
        VehicleRegistration reg = new VehicleRegistration(id);
        reg.setVin(vin);
        reg.getOwners().add(owner1);
        reg.getOwners().add(owner2);
        
        log.debug("created vehicle registration:" + reg);
        assertEquals("unexpected id", id, reg.getId());
        assertEquals("unexpected vin", vin, reg.getVin());
        assertEquals("unexpected number of owners", 2, reg.getOwners().size());        
    }
}
