package ejava.examples.secureping.ejbclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;

import ejava.util.ejb.EJBClient;

/**
 * This class contains common setUp and tearDown functionality for testing
 * the SecurePingClient remote interface.
 */
public class SecurePingClientTestBase {
	private static final Log log = LogFactory.getLog(SecurePingClientTestBase.class);

    //jndi name for SecurePingEJB
    protected String jndiName = System.getProperty("jndi.name.secureping",
    	EJBClient.getRemoteLookupName("securePingClientEAR", "securePingClientEJB", 
			"SecurePingClientEJB", 
			ejava.examples.secureping.ejb.SecurePingClientRemote.class.getName()));
    
    //username to use for admin login
    protected String adminUser = System.getProperty("admin.username", "admin1");
    
    //username to use for user login
    protected String userUser = System.getProperty("user.username", "user1");

    //username for known user with no roles
    protected String knownUser = System.getProperty("known.username", "known");


	@BeforeClass
	public static void setUpClass() throws Exception {
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

}
