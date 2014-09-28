package info.ejava.examples.ejb.basicejb.ejbclient;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import info.ejava.examples.ejb.basic.ejb.BadNameException;
import info.ejava.examples.ejb.basic.ejb.GreeterRemote;
import info.ejava.examples.ejb.basic.ejb.Greeting;
import info.ejava.examples.ejb.basic.ejb.Name;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example has been broken up into a base IT test and two sets of two sub-classes.
 * One set is based on JBoss Remoting and the othere set is based on JBoss EJBClient.
 * Each set sets up a test for both the WAR and EAR deployment. The variants cause 
 * a difference in jndiProperties and jndiName used but the basic interaction with
 * JNDI and the EJBs are the same once all is setup.
 */
public abstract class GreeterBase {
	private static final Logger logger = LoggerFactory.getLogger(GreeterBase.class);

	protected Properties jndiProperties; //varies whether using Remoting or EJBClient
    protected String jndiName;           //varies whether accessing WAR or EAR deployment
    protected Context jndi;
	protected GreeterRemote greeter;
	
    @Before
    public void setUp() throws Exception {
    	logger.info("using jndiProperties={}", jndiProperties);
    	logger.info("using jndiName={}", jndiName);
    	jndi = new InitialContext(jndiProperties);
    	greeter = (GreeterRemote) jndi.lookup(jndiName);
    }
    
    @After
    public void tearDown() throws Exception {
    	if (jndi!=null) {
    		jndi.close(); //produces errors with JBoss Remoting
    	}
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
