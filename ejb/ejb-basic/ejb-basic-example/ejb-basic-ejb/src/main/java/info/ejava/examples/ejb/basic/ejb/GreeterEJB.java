package info.ejava.examples.ejb.basic.ejb;

import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@Remote(GreeterRemote.class)
@Local(GreeterLocal.class)
public class GreeterEJB implements Greeter {
	private static final Logger logger = LoggerFactory.getLogger(GreeterEJB.class);
	
	/**
	 * This method is an example of a business method that can be invoked by
	 * users of this object (if a POJO) or bean (if deployed as an EJB).
	 */
	@Override
	public String sayHello(String name) throws BadNameException {
		logger.debug("sayHello({})", name);
		
		if (name==null || name.isEmpty()) {
			throw new BadNameException("you must have a name for me to say hello");
		}
		return "hello " + name;
	}
	
	/**
	 * This method is an example of a business method that 
	 */
	@Override
	public Greeting sayHello(Name name) throws BadNameException {
		logger.debug("sayHello({})", name);
		
		if (name==null) {
			throw new BadNameException("you must have a name for me to say hello");
		}
		return new Greeting(new Date(), 
				"hello " + name.getFirstName() + " " + name.getLastName());
	}
}
