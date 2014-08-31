package ejava.examples.txhotel.ejbclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;

/**
 * This class implements comnmon setUp and tearDown logic when testing the 
 * remote interface for the Hotel EJBs.
 */
public class HotelRemoteTestBase {
	private static final Log log = LogFactory.getLog(HotelRemoteTestBase.class);

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
