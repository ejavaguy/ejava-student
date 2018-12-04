package ejava.examples.asyncmarket.ejbclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

/**
 * This testcase provides a demonstration of asynchronous methods on session 
 * beans.
 */
public class AsyncMethodIT extends MarketITBase {
    Logger logger = LoggerFactory.getLogger(AsyncMethodIT.class);
    
    @Test
    public void testAsync() throws Exception {
        logger.info("*** demoAsync ***");
        int count=3;
        long delay=3000;
        
        long startTime = System.currentTimeMillis();
        auctionmgmt.workSync(count, delay);
    	    long syncTime = System.currentTimeMillis() - startTime;

        
        startTime = System.currentTimeMillis();
        auctionmgmt.workAsync(count, delay);
    	    long asyncTime = System.currentTimeMillis() - startTime;

    	    logger.info("count={}, delay={}, syncTime={} msecs", count, delay, syncTime);
    	    logger.info("count={}, delay={}, asyncTime={} msecs", count, delay, asyncTime);
    }
}
