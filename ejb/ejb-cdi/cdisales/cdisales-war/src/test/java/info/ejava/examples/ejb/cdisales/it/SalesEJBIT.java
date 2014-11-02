package info.ejava.examples.ejb.cdisales.it;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesEJBIT  {
	private static final Logger logger = LoggerFactory.getLogger(SalesEJBIT.class);
    private static final String hotelmgmtJNDI = System.getProperty("hotelmgmt.jndi.name",
            "ejb:/ejb-cdi-example-war/EJB!" + Object.class.getName());
	private Context jndi;
	private Object hotelMgmt;
	
	@BeforeClass
	public static void setUpClass() throws NamingException {
	}
	
	@Before
	public void setUp() throws NamingException {
        jndi=new InitialContext();
        logger.debug("looking up jndi.name={}", hotelmgmtJNDI);
	    hotelMgmt = (Object)jndi.lookup(hotelmgmtJNDI);
        
        cleanup();
        populate();
	}
	
	private void cleanup() {
	}
	
	private void populate() {
	}

    @Test
    public void test()  {
    }
}
