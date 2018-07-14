package ejava.examples.ejbsessionbank.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an example of a Singleton EJB. The container will
 * instantiate only one instance of this class and manage concurrent
 * access to the instance.
 */
@Singleton
//used to cause the singleton to be initialized during server startup --
//otherwise it would be on demand
@Startup
//tells the container to manage concurrent access. Alternately we could use 
//BEAN and standard Java concurrency managaement mechanisms
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
//establish an access timeout either at class or method level
@AccessTimeout(value=3000)
public class StatsEJB implements StatsLocal, StatsRemote {
	static final Logger log = LoggerFactory.getLogger(StatsEJB.class);
	private int delta;
	private int total;
	
	@PostConstruct
	public void init() {
		log.info("*** StatsEJB ***");
	}

	/**
	 * An example of a write method that the container will protect against
	 * other write and readers.
	 */
	@Override
	@Lock(LockType.WRITE)
	public void open() {
		this.delta += 1;
		this.total += 1;
		log.debug(String.format("open: stats=%d, total=%d", delta, total));
	}
	
	@Override
	@Lock(LockType.WRITE)
	public void close() {
		this.delta -= 1;
		this.total += 1;
		log.debug(String.format("close: stats=%d, total=%d", delta, total));
	}	
	
	/**
	 * An example read method that the container will protect from other
	 * write methods, but allow concurrent read access.
	 */
	@Override
	@Lock(LockType.READ)
	public int getTotal() {
		log.debug(String.format("getTotal: stats=%d, total=%d", delta, total));
		return total;
	}

	@Override
	@Lock(LockType.READ)
	public int getDelta() {
		log.debug(String.format("getDelta: stats=%d, total=%d", delta, total));
		return delta;
	}
	
	@Override
	@Lock(LockType.WRITE)
	public void reset() {
		delta=0;
		total=0;
		log.debug(String.format("reset: stats=%d, total=%d", delta, total));
	}
}
