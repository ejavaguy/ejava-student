package ejava.examples.asyncmarket.ejb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains some demo methods to perform example actions on 
 * behalf of the AuctionMgmtEJB.
 */
@Stateless
public class AuctionMgmtActionEJB {
	private static Logger logger = LoggerFactory.getLogger(AuctionMgmtActionEJB.class);
	
    /**
     * Perform action synchronously while caller waits.
     */
	public Date doWorkSync(long delay) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		logger.debug("sync method {} starting {} delay at {}", Thread.currentThread().getId(), delay, df.format(new Date()));
		try { Thread.sleep(delay); }
		catch (Exception ex) {
			logger.error("unexpected error during sleep", ex);
		}
		Date now = new Date();
		logger.debug("sync method {} completed {} delay at {}", Thread.currentThread().getId(), delay, df.format(now));
		
		return now;
	}    

	/**
	 * Perform action async from caller
	 */
	@Asynchronous
	public Future<Date> doWorkAsync(long delay) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		logger.debug("async method {} starting {} delay at {}", Thread.currentThread().getId(), delay, df.format(new Date()));
		try { Thread.sleep(delay); }
		catch (Exception ex) {
			logger.error("unexpected error during sleep", ex);
		}
		Date now = new Date();
		logger.debug("async method {} completed {} delay at {}", Thread.currentThread().getId(), delay, df.format(now));
		
		return new AsyncResult<Date>(now);
	}    
}
