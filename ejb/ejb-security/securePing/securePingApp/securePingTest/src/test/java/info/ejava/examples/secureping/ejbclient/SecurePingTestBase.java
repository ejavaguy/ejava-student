package info.ejava.examples.secureping.ejbclient;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains common setUp and tearDown functionality for testing
 * the SecurePingEJB remote interface.
 */
public class SecurePingTestBase {
	private static final Logger logger = LoggerFactory.getLogger(SecurePingTestBase.class);
    
	protected String knownUser = System.getProperty("known.username", "known");
	protected String knownPassword = System.getProperty("known.password", "password1!");
	protected String userUser = System.getProperty("user.username","user1");
	protected String userPassword = System.getProperty("user.password","password1!");
	protected String adminUser = System.getProperty("admin.username","admin1");
	protected String adminPassword = System.getProperty("admin.password","password1!");
    protected String jmxUser = System.getProperty("jmx.username","admin");
    protected String jmxPassword = System.getProperty("jmx.password","password1!");

	@BeforeClass
	public static void setUpClass() throws Exception {
		//give application time to fully deploy
		if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
			long waitTime=15000;
	    	logger.info(String.format("pausing %d secs for server deployment to complete", waitTime/1000));
	    	Thread.sleep(waitTime);
		}
		else {
	    	logger.info(String.format("startstop not set"));
		}
	}
}
