package org.myorg.basicejb.warejb;

import static org.junit.Assert.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.myorg.basicejb.webejb.ShopperRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShopperIT {
    private static final Logger logger = LoggerFactory.getLogger(ShopperIT.class);
    private static final String shopperJNDI = System.getProperty("jndi.name.shopper",
            "ejb:/basicejb-war/ShopperEJB!"+ShopperRemote.class.getName()+"?stateful"); 
    private InitialContext jndi;
    
    @Before
    public void setUp() throws NamingException {
        assertNotNull("jndi.name.reservation not supplied", shopperJNDI);

        logger.debug("getting jndi initial context");
        jndi=new InitialContext();
        logger.debug("jndi={}", jndi.getEnvironment());
    }
    
    @Test
    public void testPing() throws NamingException {
        logger.info("*** testPing ***");
        ShopperRemote shopper1=null;
        ShopperRemote shopper2=null;
        try {
            shopper1= (ShopperRemote) jndi.lookup(shopperJNDI);
            shopper2= (ShopperRemote) jndi.lookup(shopperJNDI);
            for (int i=0; i<10; i++) {
                int counter1=shopper1.ping();
                int counter2=shopper2.ping();
                assertEquals("unexpected count from shopper1",  i, counter1);
                assertEquals("unexpected count from shopper2",  i, counter2);
            }
        } finally {
            if (shopper1!=null) { shopper1.close(); }
            if (shopper2!=null) { shopper2.close(); }           
        }
    }
}
