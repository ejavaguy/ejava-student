package ejava.examples.txhotel.ejb;

import javax.ejb.Remote;

/**
 * This interface is used in support of testing from remote clients.
 */
@Remote
public interface TestUtilRemote {
	/**
	 * clear all records for the Hotel from the DB.
	 */
	void reset();
}
