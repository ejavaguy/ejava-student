package ejava.examples.secureping.ejbclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;

import ejava.examples.secureping.ejb.SecurePingRemote;
import ejava.util.ejb.EJBClient;

/**
 * This class contains common setUp and tearDown functionality for testing
 * the SecurePingEJB remote interface.
 */
public class SecurePingTestBase {
	private static final Log log = LogFactory.getLog(SecurePingTestBase.class);
    String jndiName = System.getProperty("jndi.name.secureping",
    	EJBClient.getRemoteLookupName("securePingEAR", "securePingEJB", 
   			"SecurePingEJB", SecurePingRemote.class.getName()));
    String knownUser = System.getProperty("known.username", "known");
    String knownPassword = System.getProperty("known.password", "password1!");
    String userUser = System.getProperty("user.username","user1");
    String userPassword = System.getProperty("user.password","password1!");
    String adminUser = System.getProperty("admin.username","admin1");
    String adminPassword = System.getProperty("admin.password","password1!");

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
