package ejava.examples.jms10.jmsmechanics;

import static org.junit.Assert.assertNotNull;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jms10.jmsmechanics.MessageCatcher;


public class JMSTestBase {
	private static final Logger logger = LoggerFactory.getLogger(JMSTestBase.class);
    protected static boolean jmsEmbedded = Boolean.parseBoolean( 
		System.getProperty("jms.embedded", "true"));
    protected int msgCount = Integer.parseInt(System.getProperty("multi.message.count", "20"));
    private static String connFactoryJNDI = 
		System.getProperty("jndi.name.connFactory", "jms/RemoteConnectionFactory");
    protected static String queueJNDI = System.getProperty("jndi.name.testQueue",
            "jms/queue/ejava/examples/jmsMechanics/queue1");
    protected static String topicJNDI = System.getProperty("jndi.name.testTopic",
            "jms/topic/ejava/examples/jmsMechanics/topic1");

    protected static String adminUser = System.getProperty("admin.user", "admin1");
    protected static String adminPassword = System.getProperty("admin.password", "password1!");
    protected static String user = System.getProperty("user", "user1");
    protected static String password = System.getProperty("password", "password1!");

    private static ArtemisServer server; //used when JMS server embedded in JVM
    private static Context jndi;     //used when JMS server remote in JBoss
    private static ConnectionFactory connFactory;
    protected static Connection connection;    

    @BeforeClass
	public static final void setUpClass() throws Exception {
        logger.info("connFactoryJNDI={}", connFactoryJNDI);
        logger.info("jndi.name.testQueue={}", queueJNDI);
        logger.info("jndi.name.testTopic={}", topicJNDI);

        logger.debug("getting jndi initial context");
        jndi = new InitialContext();    
        logger.debug("jndi=" + jndi.getEnvironment());

        if (jmsEmbedded) {
			logger.info("using embedded JMS server");
			server = new ArtemisServer();
			server.start();			
		}
        connFactory=(ConnectionFactory) jndi.lookup(connFactoryJNDI);
        assertNotNull("connFactory not found:" + connFactoryJNDI, connFactory);
 		connection = createConnection();
		connection.start();
	}
	
	@AfterClass
	public static final void tearDownClass() throws Exception {
		if (connection != null) {
			connection.stop();
			connection.close();
			connection = null;
		}
		if (server != null) {
			server.stop();
			server=null;
		}
		if (jndi != null) {
			jndi.close();
			jndi=null;
		}
	}

	protected static Connection createConnection() throws JMSException {
		return connFactory.createConnection(user, password);
	}
	
	protected Object lookup(String name) throws NamingException {
		logger.debug("lookup:" + name);
		return jndi.lookup(name);	
	}
	
	protected MessageCatcher createCatcher(String name, Destination destination) {
        MessageCatcher catcher = new MessageCatcher(name);
        catcher.setConnFactory(connFactory);
        catcher.setDestination(destination);
        catcher.setUser(user);
        catcher.setPassword(password);
        return catcher;
	}
	
	protected void startCatcher(MessageCatcher catcher) throws Exception {
        new Thread(catcher).start();
        while (catcher.isStarted() != true) {
            logger.debug(String.format("waiting for %s to start", catcher.getName()));
            Thread.sleep(2000);
        }
	}
	
	protected void shutdownCatcher(MessageCatcher catcher) throws Exception {
        	if (catcher != null) {
    	        for (int i=0; !catcher.isStarted() && i< 10; i++) {
    	            logger.debug(String.format("waiting for %s to start", catcher.getName()));
    	            Thread.sleep(2000);
    	        }
    	        catcher.stop();
    	        for (int i=0; !catcher.isStopped() && i<10; i++) {
    	            logger.debug(String.format("waiting for %s to stop", catcher.getName()));
    	            Thread.sleep(2000);
    	        }
        	}		
	}
}
