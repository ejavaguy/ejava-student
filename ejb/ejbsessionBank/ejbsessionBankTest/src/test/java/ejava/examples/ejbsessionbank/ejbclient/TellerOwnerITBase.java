package ejava.examples.ejbsessionbank.ejbclient;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hibernate.LazyInitializationException;
import org.junit.Test;
import static org.junit.Assert.*;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dto.OwnerDTO;

/**
 * This class tests the teller with additional Owner objects that 
 * provide some issues for the remote interface. A derived class will
 * determine which type of RMI techique is used to locate and comminicate with
 * the remote teller object. The base class provides common setup and teardown
 * mechanisms.
 */
public class TellerOwnerITBase extends TellerRemoteITBase {
    private static Logger log = LoggerFactory.getLogger(TellerOwnerITBase.class);

    /**
     * Demonstrates how a Lazy Load exception can occur when processing
     * objects from server that have not been hydrated.
     * @throws Exception
     */
    @Test
    public void testLazy() throws Exception {
        log.info("*** testLazy ***");
        
        Owner owner = teller.createOwner("joe", "jones", "123");
        owner = teller.openAccount(owner.getId(), "111");
        Owner owner2 = teller.createOwner("jane", "jones", "333");
        owner2 = teller.addOwner(owner2.getId(), "111");
        
        //get a set of owners that have been lazily loaded
        List<Owner> owners = teller.getOwners(0, 100);
        assertEquals("unexpected number of owners", 2, owners.size());
        
        try {
            for(Owner o : owners) {
                for (Account a: o.getAccounts()) {
                    log.info("account=" + a);
                }
            }
            fail("did not encounter expected exception");
        }
        catch (LazyInitializationException expected) {
            log.info("encountered expected exception:" + expected);    
        }
        
        //now get them fully loaded
        owners = teller.getOwnersLoaded(0, 100);
        for(Owner o : owners) {
            for (Account a: o.getAccounts()) {
                log.info("account=" + a);
            }
        }
    }
    
    /**
     * Demonstrates a fix for the LAZY Load issue but then shows how 
     * persistence classes from the server are being leaked into the client.
     * @throws Exception
     */
    @Test
    public void testPOJO() throws Exception {
        log.info("*** testPOJO ***");
        
        Owner owner = teller.createOwner("joe", "jones", "123");
        owner = teller.openAccount(owner.getId(), "111");
        Owner owner2 = teller.createOwner("jane", "jones", "333");
        owner2 = teller.addOwner(owner2.getId(), "111");
        
        
        //now get the objects fully loaded
        List<Owner> owners = teller.getOwnersLoaded(0, 100);
        for(Owner o : owners) {
            for (Account a: o.getAccounts()) {
                log.info("account=" + a);
            }
            log.debug("addresses=" + o.getAccounts().getClass().getName());
            assertTrue("unexpected collection class", 
                    o.getAccounts().getClass().getName().contains("hibernate"));
        }
        
        //now get the objects cleaned of persistence classes
        owners = teller.getOwnersPOJO(0, 100);
        for(Owner o : owners) {
            for (Account a: o.getAccounts()) {
                log.info("account=" + a);
            }
            log.debug("addresses=" + o.getAccounts().getClass().getName());
            assertFalse("unexpected collection class", 
                    o.getAccounts().getClass().getName().contains("hibernate"));
        }
    }
    
    /**
     * Demonstrates a fix of the LAZY Load and class leakage problem by 
     * using DTOs.
     * @throws Exception
     */
    @Test
    public void testDTO() throws Exception {
        log.info("*** testDTO ***");
        
        int startCount = teller.getOwnersDTO(0, 100).size();
        
        Owner owner = teller.createOwner("joe", "jones", "123");
        owner = teller.openAccount(owner.getId(), "111");
        Owner owner2 = teller.createOwner("jane", "jones", "333");
        owner2 = teller.addOwner(owner2.getId(), "111");
        
        
        //get DTOs instead
        List<OwnerDTO> owners = teller.getOwnersDTO(0, 100);
        for(OwnerDTO o : owners) {
            log.debug("ownerDTO=" + o);
        }
        assertEquals("unexpected number of owners", startCount + 2,
                owners.size());
    }
}
