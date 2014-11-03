package ejava.examples.asyncmarket.ejb;

import java.util.Date;
import java.util.concurrent.Future;

import javax.ejb.Remote;

import ejava.examples.asyncmarket.AuctionMgmt;

@Remote
public interface AuctionMgmtRemote extends AuctionMgmt {
	
	/**
	 * An example of performing a synchronous task while client has to
	 * wait for the result.
	 * @param count number of tasks to perform
	 * @param delay time each task should take
	 * @return timestamp when method completed
	 */
	void workSync(int count, long msecs);
	
	/**
	 * An example of performing an async task while client doesn't
	 * wait for result.
	 * @param count number of tasks to perform
	 * @param delay time each task should take
	 */
	void workAsync(int count, long msecs);
}
