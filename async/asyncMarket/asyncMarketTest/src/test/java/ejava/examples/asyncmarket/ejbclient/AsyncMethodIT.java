package ejava.examples.asyncmarket.ejbclient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * This testcase provides a demonstration of asynchronous methods on session 
 * beans.
 */
public class AsyncMethodIT extends MarketITBase {
    Log log = LogFactory.getLog(AsyncMethodIT.class);
    
    @Test
    public void testAsync() throws Exception {
        log.info("*** demoAsync ***");
        int count=3;
        long delay=3000;
        
        long startTime = System.currentTimeMillis();
        auctionmgmt.workSync(count, delay);
    	long syncTime = System.currentTimeMillis() - startTime;

        
        startTime = System.currentTimeMillis();
        auctionmgmt.workAsync(count, delay);
    	long asyncTime = System.currentTimeMillis() - startTime;

    	log.info(String.format("count=%d, delay=%d, syncTime=%d msecs", count, delay, syncTime));
    	log.info(String.format("count=%d, delay=%d, asyncTime=%d msecs", count, delay, asyncTime));
    }
}
