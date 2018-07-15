package ejava.projects.edmv.test;


import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.projects.edmv.ejb.ParserTestRemote;
import ejava.util.ejb.EJBClient;

public class ParserServerIT {
    private static final Logger log = LoggerFactory.getLogger(ParserServerIT.class);
    public static final String jndiName = System.getProperty("jndi.name",
    		EJBClient.getRemoteLookupName("eDmvTestEAR", "eDmvTestEJB", 
    				"ParserTestEJB", ParserTestRemote.class.getName()));
    
    private static ParserTestRemote parser;
    private InitialContext jndi;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.debug("looking up name:" + jndiName);
    }

    @Before
    public void setUp() throws Exception {
        jndi = new InitialContext();
        parser = (ParserTestRemote)jndi.lookup(jndiName);
    }
    
    @After
    public void tearDown() throws Exception {
    	if (jndi != null) {
    		jndi.close();
    	}
    }

    @Test
    public void testIngest() throws Exception {
        log.info("*** testIngest ***");
        parser.ingest();
    }
}
