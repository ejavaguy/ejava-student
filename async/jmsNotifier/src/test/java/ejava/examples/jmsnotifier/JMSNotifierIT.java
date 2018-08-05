package ejava.examples.jmsnotifier;


import java.io.InputStream;



import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import ejava.util.jms.JMSUtil;
import ejava.util.jndi.JNDIUtil;

/**
 * This integration test verifies the jmsNotifier functionality the gets
 *  wrapped by the Ant scripts. It runs as an integration test with the 
 * JMS server launched separately from mavn during the pre-integration phase.
 */
public class JMSNotifierIT {
	static final Logger log = LoggerFactory.getLogger(JMSNotifierIT.class);
    protected static String adminUser = System.getProperty("admin.user", "admin1");
    protected static String adminPassword = System.getProperty("admin.password", "password1!");
    protected static boolean jmsEmbedded = Boolean.parseBoolean(System.getProperty("jms.embedded", "true"));
    private static Context jndi; 

    protected static Properties props;
	public static String topicName;
	public static String topicJNDI;
	public static String connFactoryJNDI;

	/** Creates the topic for use in tests */
	@BeforeClass
	public static void setUpClass() throws Exception {
		log.info("*** setUpClass() ***");

		//read property file used by the Ant script to use same properties
		InputStream in = Thread.currentThread()
        			           .getContextClassLoader()
                               .getResourceAsStream("jmsNotifier.properties");
		assertNotNull("could not locate properties file",in);
        props = new Properties();
        props.load(in);
        in.close();
        connFactoryJNDI= props.getProperty("jndi.name.connFactory");
        topicName = props.getProperty("jmx.name.testTopic");
        topicJNDI = props.getProperty("jndi.name.testTopic");

        //perform some setup on the topics used for the test
		log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        //wait for JMS server to start
        ConnectionFactory connFactory = JNDIUtil.lookup(jndi, ConnectionFactory.class, connFactoryJNDI, 10);
        log.debug(new JNDIUtil().dump(jndi,""));
        
        //wait extra for connection to be sure server is running
        JMSUtil.createConnection(connFactory, adminUser, adminPassword, 10).close();
	}
		
	@AfterClass
	public static void tearDownClass() throws Exception {
		if (jndi != null) {
			jndi.close();
			jndi=null;
		}
	}
	
	protected Object lookup(String name) throws NamingException {
		log.debug("lookup:" + name);
		return jndi.lookup(name);
	}
	
	/**
	 * This test just verifies that the necessary resources can be found
	 * in the server.
	 * @throws Exception
	 */
	@Test
	public void verifyResources() throws Exception {
		log.info("*** verifyResources ***");
        JNDIUtil.lookup(jndi, ConnectionFactory.class, connFactoryJNDI, 10);
        log.debug(new JNDIUtil().dump(jndi,""));
		
        assertNotNull("jndi.name.testTopic not found in props", topicJNDI);
        assertNotNull("jndi.name.connFactory not found in props", connFactoryJNDI);

        log.info("looking up connectionFactory:" + connFactoryJNDI);
		ConnectionFactory cf = (ConnectionFactory) lookup(connFactoryJNDI);
		log.info(String.format("%s=%s", connFactoryJNDI, cf));

        log.info("looking up topic:" + topicJNDI);
		Topic topic = (Topic) lookup(topicJNDI);
		log.info(String.format("%s=%s", topicJNDI, topic));

		assertNotNull("connectionFactory not found", cf);
		assertNotNull("topic not found", topic);
	}

	/**
	 * This test executes the demo publisher and subscriber classes in
	 * a test scenario. It uses the same main() method used by the Ant 
	 * scripts.
	 */
	@Test
	public void publishToSubscribers() {
		log.info("*** publishToSubscribers ***");
				
		String name = props.getProperty("publisher.name");
		String sleep = props.getProperty("publisher.sleep");
		String publisherUsername = props.getProperty("publisher.username");
		String publisherPassword = props.getProperty("publisher.password");
		String pubArgs[] = {
				"-jndi.name.connFactory", connFactoryJNDI,
				"-jndi.name.destination", topicJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", "10",
                "-username", publisherUsername,
                "-password", publisherPassword};

		String name0 = props.getProperty("subscriber.name");
		String sleep0 = props.getProperty("subscriber.sleep");
		String durable0 = props.getProperty("subscriber.durable");
		String selector0 = props.getProperty("subscriber.selector");
		String subscriberUsername = props.getProperty("subscriber.username");
		String subscriberPassword = props.getProperty("subscriber.password");
		final String sub0Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name0,
            "-sleep", sleep0,
            "-max", "10",
            "-durable", durable0,
            "-selector", selector0,
	        "-username", subscriberUsername,
	        "-password", subscriberPassword};
		
		new Thread() {
			public void run() {
				Subscriber.main(sub0Args);
			}			
		}.start();
		
		String name1 = props.getProperty("subscriber1.name");
		String sleep1 = props.getProperty("subscriber1.sleep");
		String durable1 = props.getProperty("subscriber1.durable");
		String selector1 = props.getProperty("subscriber1.selector");
		final String sub1Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name1,
            "-sleep", sleep1,
            "-max", "2",
            "-durable", durable1,
            "-selector", selector1,
	        "-username", subscriberUsername,
	        "-password", subscriberPassword};
		new Thread() {
			public void run() {
				Subscriber.main(sub1Args);
			}			
		}.start();

		String name2 = props.getProperty("subscriber2.name");
		String sleep2 = props.getProperty("subscriber2.sleep");
		String durable2 = props.getProperty("subscriber2.durable");
		String selector2 = props.getProperty("subscriber2.selector");
		final String sub2Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name2,
            "-sleep", sleep2,
            "-max", "2",
            "-durable", durable2,
            "-selector", selector2,
	        "-username", subscriberUsername,
	        "-password", subscriberPassword};
		new Thread() {
			public void run() {
				Subscriber.main(sub2Args);
			}			
		}.start();

		String name3 = props.getProperty("subscriber3.name");
		String sleep3 = props.getProperty("subscriber3.sleep");
		String durable3 = props.getProperty("subscriber3.durable");
		String selector3 = props.getProperty("subscriber3.selector");
		final String sub3Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name3,
            "-sleep", sleep3,
            "-max", "2",
            "-durable", durable3,
            "-selector", selector3,
	        "-username", subscriberUsername,
	        "-password", subscriberPassword};
		new Thread() {
			public void run() {
				Subscriber.main(sub3Args);
			}			
		}.start();

		String name4 = props.getProperty("subscriber4.name");
		String sleep4 = props.getProperty("subscriber4.sleep");
		String durable4 = props.getProperty("subscriber4.durable");
		String selector4 = props.getProperty("subscriber4.selector");
		final String sub4Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.destination", topicJNDI,
            "-name", name4,
            "-sleep", sleep4,
            "-max", "2",
            "-durable", durable4,
            "-selector", selector4,
	        "-username", subscriberUsername,
	        "-password", subscriberPassword};
		new Thread() {
			public void run() {
				Subscriber.main(sub4Args);
			}			
		}.start();

		Publisher.main(pubArgs);
	}
}
