package ejava.examples.jndidemo.ejbclient;

import java.io.InputStream;



import javax.naming.InitialContext;
import javax.naming.NamingException;


import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.jndidemo.Scheduler;
import ejava.examples.jndidemo.ejb.AidSchedulerRemote;
import ejava.examples.jndidemo.ejb.BakeSchedulerRemote;
import ejava.examples.jndidemo.ejb.TrainSchedulerRemote;
import ejava.util.ejb.EJBClient;

/**
 * Performs a basic set of calls in the EJBs deployed to demonstrate 
 * aspects of how the EJBs are configured.
 */
public class JndiIT  {
    private static final Logger log = LoggerFactory.getLogger(JndiIT.class);
    private InitialContext jndi;
    
    static final String EAR_NAME=System.getProperty("ear.name","jndiDemoEAR");
    static final String EJB_MODULE=System.getProperty("ejb.module", "jndiDemoEJB");
    static final String aidName = System.getProperty("jndi.name.aid",
    	EJBClient.getEJBClientLookupName(
			EAR_NAME, EJB_MODULE, "","AidScheduler",
    		AidSchedulerRemote.class.getName(), false));
    static final String bakeName = System.getProperty("jndi.name.bake",
    	EJBClient.getEJBClientLookupName(
			EAR_NAME, EJB_MODULE, "","BakeScheduler",
    		BakeSchedulerRemote.class.getName(), true));
    static final String trainName = System.getProperty("jndi.name.train",
       	EJBClient.getEJBClientLookupName(
			EAR_NAME, EJB_MODULE, "","TrainSchedulerEJB",
    		TrainSchedulerRemote.class.getName(), false));
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    	log.info("*** setUpClass() ***");
		//give application time to fully deploy
		if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
			long waitTime=15000;
	    	log.info(String.format("pausing %d secs for server deployment to complete", waitTime/1000));
	    	Thread.sleep(waitTime);
		}
		else {
	    	log.info(String.format("startstop not set"));
		}
    }
    
    
    @Before
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();
        log.debug("jndi=" + jndi.getEnvironment());
                
        log.debug("aidName=" + aidName);
        log.debug("bakeName=" + bakeName);
        log.debug("trainName=" + trainName);
    }
    
    @After
    public void tearDown() throws NamingException {
        jndi.close();
    }

    /**
     * Calls the EJBs that used POJOs with XML deployment descriptors.
     * @throws Exception
     */
    @Test
    public void testXMLPopulation() throws Exception {
        log.info("*** testXMLPopulation ***");
        
        Object object = jndi.lookup(aidName);
        log.debug(aidName + "=" + object);
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s);
        String name = s.getName();
        log.debug("scheduler.name:" + name);
        assertEquals("", "AidScheduler", name);
        
        String encname = "ejb/hospital";
        String jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndidemo";
        jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));
        
        log.debug("java:comp/env=" + s.getEnv());
    }
    
    /**
     * Calls the EJBs that used annotated classes and no deployment descriptors.
     * @throws Exception
     */
    @Test
    public void testAnnotationPopulation() throws Exception {
        log.info("*** testAnnotationPopulation ***");
        
        Object object = jndi.lookup(bakeName);
        log.debug(bakeName + "=" + object);
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s);
        String name = s.getName();
        log.debug("scheduler.name:" + name);
        assertEquals("", "BakeSchedulerEJB", name);
        
        String encname = "ejb/cook";
        String jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));

        encname = "persistence/jndidemo";
        jndiname = "java:comp/env/" + encname;
        log.debug("jndi: " + jndiname + "=" + s.getJndiProperty(jndiname));
        log.debug("ctx : " + encname + "=" + s.getCtxProperty(encname));
        
        log.debug("java:comp/env=" + s.getEnv());
    }
    
    @Test
    public void testCDIPopulation() throws Exception {
        log.info("*** testCDIPopulation ***");
        
        Object object = jndi.lookup(trainName);
        log.debug(trainName + "=" + object);
        
        Scheduler s = (Scheduler)object;        
        log.debug("got scheduler:" + s);
        String name = s.getName();
        log.debug("scheduler.name:" + name);
        assertEquals("", "TrainSchedulerEJB", name);
    }
}
