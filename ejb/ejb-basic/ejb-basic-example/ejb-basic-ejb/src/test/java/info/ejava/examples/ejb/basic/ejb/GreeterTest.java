package info.ejava.examples.ejb.basic.ejb;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreeterTest {
	private static final Logger logger = LoggerFactory.getLogger(GreeterTest.class);
	private Greeter greeter;

	@Before
	public void setUp() {
		greeter = new GreeterEJB();
	}
	
	@Test
	public void pojoGreeter() throws BadNameException {
		logger.info("*** pojoGreeter ***");
		String name = "cat inhat";
		String greeting = greeter.sayHello(name);
		assertTrue("greeter did not say my name", greeting.contains(name));
	}
	
	@Test(expected=BadNameException.class) 
	public void badName() throws BadNameException {
		logger.info("*** badName ***");
		greeter.sayHello("");
	}
	
	@Test
	public void dto() throws BadNameException {
		logger.info("*** dto ***");
		Name name = new Name("thing", "one");
		Greeting greeting = greeter.sayHello(name);
		assertTrue("greeter did not say my name", 
				greeting.getGreeting().contains(name.getFirstName()));
	}
}
