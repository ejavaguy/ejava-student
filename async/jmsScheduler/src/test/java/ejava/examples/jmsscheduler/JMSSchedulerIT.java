package ejava.examples.jmsscheduler;

import static org.junit.Assert.*;


import java.io.InputStream;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.jmsscheduler.Requestor;
import ejava.examples.jmsscheduler.Worker;
import ejava.util.jndi.JNDIUtil;

/**
 * This integration test verifies the jmsScheduler functionality that gets
 * wrapped by Ant scripts. It runs as an integration test with the JMS server
 * launched separately from mavn during the pre-integration phase.
 */
public class JMSSchedulerIT {
    static final Logger log = LoggerFactory.getLogger(JMSSchedulerIT.class);
    private static String adminUser = System.getProperty("admin.user", "admin1");
    private static String adminPassword = System.getProperty("admin.password", "password1!");
	private static String requestorUsername;
	private static String requestorPassword;
	private static String workerUsername;
	private static String workerPassword;
    private static Context jndi; 
    private static ConnectionFactory connFactory; 

	
	public static String requestQueueName;
	public static String requestQueueJNDI;
    public static String dlqName;
	public static String dlqJNDI;
	public static String connFactoryJNDI;
	
	protected static Properties props;

	@BeforeClass
	public static void setUpClass() throws Exception {
		log.info("*** setUpClass() ***");
		
		//read property file used by thr Ant script to use same properties
		InputStream in = Thread.currentThread()
        			           .getContextClassLoader()
                               .getResourceAsStream("jmsScheduler.properties");
		assertNotNull("could not locate properties file",in);
        props = new Properties();
        props.load(in);
        in.close();
        //mangle the names to not conflict with static definition names
		connFactoryJNDI = props.getProperty("jndi.name.connFactory");
        requestQueueName = props.getProperty("jmx.name.requestQueue");
		requestQueueJNDI = props.getProperty("jndi.name.requestQueue");
		dlqName = props.getProperty("jmx.name.DLQ");
		dlqJNDI = props.getProperty("jndi.name.DLQ");
		requestorUsername = props.getProperty("requestor.username");
		requestorPassword = props.getProperty("requestor.password");
		workerUsername = props.getProperty("worker.username");
		workerPassword = props.getProperty("worker.password");

        //perform some setup on the topics used for the test
		log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());

        //wait for JMS server to start
        connFactory = JNDIUtil.lookup(jndi, ConnectionFactory.class, connFactoryJNDI, 10);
        log.debug(new JNDIUtil().dump(jndi,""));
	}
	
	@Before
	public void setUp() throws Exception {
		int count = emptyQueue(requestQueueJNDI, 0);
		log.info("cleared " + count + " messages from " + requestQueueJNDI);
		count = emptyQueue(dlqJNDI, 0);
		log.info("cleared " + count + " messages from " + dlqJNDI);
	}
	
	@AfterClass
	public static void tearDownClass() throws NamingException {
		if (jndi!=null) {
			jndi.close();
		}
	}

	/**
	 * This helper method removes count messages from the specified queue.
	 * @param queueName
	 * @param count
	 * @return
	 * @throws Exception
	 */
	static int emptyQueue(String queueName, int count) throws Exception {
		int total = 0;
		Queue queue = (Queue) jndi.lookup(queueName);
		Connection connection = null;
		Session session = null;
		MessageConsumer consumer = null;
		try {
			connection = dlqJNDI.equals(queueName) ?
					connFactory.createConnection(adminUser, adminPassword) :
					connFactory.createConnection(workerUsername, workerPassword);
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumer = session.createConsumer(queue);
			connection.start();
			for (int i=0; i<count || count == 0; i++) {
				log.debug("cleaning queue of message");
				Message m = consumer.receive(250);
				if (m == null) { break; }
				total += 1;
			}
		}
		finally {
			if (consumer != null) { consumer.close(); }
			if (session != null) { session.close(); }
			if (connection != null) { connection.close(); }
		}
		return total;
	}

	/**
	 * This test is a sanity check of the server resources that the 
	 * launched client will use.
	 * @throws Exception
	 */
	@Test
	public void verifyResources() throws Exception {
		InitialContext jndi = new InitialContext();
		log.info("looking up JNDI factory:" + connFactoryJNDI);
		
		log.info("looking up queue:" + requestQueueJNDI);
		Queue queue = (Queue) jndi.lookup(requestQueueJNDI);
		log.info(String.format("%s=%s", requestQueueJNDI, queue));
		
		log.info("looking up DLQ:" + dlqJNDI);
		Queue dlq = (Queue) jndi.lookup(dlqJNDI);
		log.info(String.format("%s=%s", dlqJNDI, dlq));
	}
	
	/**
	 * Run a basic scenario with one scheduler and one worker.
	 */
	@Test
	public void schedulerScenario() {
		log.info("*** schedulerScenario ***");
		
		String name = props.getProperty("requestor.name");
		String sleep = "0";//props.getProperty("requestor.sleep");
		String max = props.getProperty("requestor.max");		

		String sendArgs[] = {
				"-jndi.name.connFactory", connFactoryJNDI,
				"-jndi.name.requestQueue", requestQueueJNDI,
	            "-jndi.name.DLQ", dlqJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", max,
                "-username", requestorUsername,
                "-password", requestorPassword,
                "-noExit"};

		String name0 = props.getProperty("worker.name");
		String sleep0 = props.getProperty("worker.sleep");
		final String worker0Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.requestQueue", requestQueueJNDI,
            "-jndi.name.DLQ", dlqJNDI,
            "-name", name0,
            "-sleep", sleep0,
            "-max", max,
            "-noFail", "true",//don't fail getting last message
            "-username", workerUsername,
            "-password", workerPassword,
            "-noExit"}; 
		
		new Thread() {
			public void run() {
				Worker.main(worker0Args);
			}			
		}.start();

		Requestor.main(sendArgs);
	}
	
	/**
	 * This scenario will use a single scheduler and multiple workers.
	 * One or the workers will fail prior to completing all work and
	 * the message is handled by an alternate worker.
	 */
	@Test
	public void failScenario() {
		log.info("*** failScenario ***");

		String name = props.getProperty("requestor.name");
		String sleep = "0";//props.getProperty("requestor.sleep");
		String max = props.getProperty("requestor.max");		
		String requestorUsername = props.getProperty("requestor.username");
		String requestorPassword = props.getProperty("requestor.password");
		final String sendArgs[] = {
				"-jndi.name.connFactory", connFactoryJNDI,
				"-jndi.name.requestQueue", requestQueueJNDI,
	            "-jndi.name.DLQ", dlqJNDI,
				"-name", name, 
                "-sleep", sleep,
                "-max", max,
                "-username", requestorUsername,
                "-password", requestorPassword,
                "-noExit"};

		String name1 = props.getProperty("worker1.name");
		String sleep1 = props.getProperty("worker1.sleep");
		String max1 = "3"; //props.getProperty("worker1.max");		
		String workerUsername = props.getProperty("worker.username");
		String workerPassword = props.getProperty("worker.password");
		final String worker1Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.requestQueue", requestQueueJNDI,
            "-jndi.name.DLQ", dlqJNDI,
            "-name", name1,
            "-sleep", sleep1,
            "-max", max1,
            "-noFail", "false",//fail on last message
            "-username", workerUsername,
            "-password", workerPassword,
            "-noExit"}; 
		
		String name2 = props.getProperty("worker2.name");
		String sleep2 = props.getProperty("worker2.sleep");
		String max2 = "3"; //props.getProperty("worker2.max");		
		final String worker2Args[] = {
            "-jndi.name.connFactory", connFactoryJNDI,
            "-jndi.name.requestQueue", requestQueueJNDI,
            "-jndi.name.DLQ", dlqJNDI,
            "-name", name2,
            "-sleep", sleep2,
            "-max", max2,
            "-noFail", "false", //fail on last message
            "-username", workerUsername,
            "-password", workerPassword,
            "-noExit"};
		
		new Thread() { //each of these will try 3, but only complete 2
			public void run() { 
				Worker.main(worker1Args);
				Worker.main(worker2Args);
				Worker.main(worker1Args);
				Worker.main(worker2Args);
				Worker.main(worker1Args);
				Worker.main(worker2Args);
				Worker.main(worker1Args);
				Worker.main(worker2Args);
			}			
		}.start();
		
		Requestor.main(sendArgs);
	}
}
