package ejava.examples.jmsmechanics;

import javax.jms.Connection;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hornetq.jms.server.embedded.EmbeddedJMS;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;


public class JMSTestBase {
	private static final Logger log = LoggerFactory.getLogger(JMSTestBase.class);
    protected static boolean jmsEmbedded = Boolean.parseBoolean( 
		System.getProperty("jms.embedded", "true"));
    protected int msgCount = Integer.parseInt(System.getProperty("multi.message.count", "20"));
    private static String connFactoryJNDI = 
		System.getProperty("jndi.name.connFactory", "/jms/RemoteConnectionFactory");
    protected static String queueJNDI = System.getProperty("jndi.name.testQueue",
            "queue/ejava/examples/jmsMechanics/queue1");
    protected static String topicJNDI = System.getProperty("jndi.name.testTopic",
            "topic/ejava/examples/jmsMechanics/topic1");

    protected static String adminUser = System.getProperty("admin.user", "admin1");
    protected static String adminPassword = System.getProperty("admin.password", "password1!");
    protected static String user = System.getProperty("user", "user1");
    protected static String password = System.getProperty("password", "password1!");

    private static EmbeddedJMS server; //used when JMS server embedded in JVM
    private static Context jndi;     //used when JMS server remote in JBoss
    private static ConnectionFactory connFactory;
    protected static Connection connection;
    protected static JMSAdmin jmsAdmin;
    

	@BeforeClass
	public static final void setUpClass() throws Exception {
		if (jmsEmbedded) {
			log.info("using embedded JMS server");
			server = new EmbeddedJMS();
			server.start();
			
			connFactory=(ConnectionFactory) server.lookup(connFactoryJNDI);
	        jmsAdmin=new JMSAdminHornetQ(connFactory, adminUser, adminPassword);
		}
		else {
	        log.debug("getting jndi initial context");
	        jndi = new InitialContext();    
	        log.debug("jndi=" + jndi.getEnvironment());
			
	        log.debug("connection factory name:" + connFactoryJNDI);
	        connFactory = (ConnectionFactory)jndi.lookup(connFactoryJNDI);
	        jmsAdmin=new JMSAdminHornetQ(connFactory, adminUser, adminPassword)
	        	.setJNDIPrefix("/jboss/exported");
		}		
		connection = createConnection();
		connection.start();
	}
	
	@Before
	public void commonSetup() throws Exception {
    	//dynamically create necessary JMS resources
        jmsAdmin.destroyTopic("topic1")
            	.destroyQueue("queue1")
            	.deployTopic("topic1", topicJNDI)
            	.deployQueue("queue1", queueJNDI);
	}
	
	@AfterClass
	public static final void tearDownClass() throws Exception {
		jmsAdmin.close();
		if (connection != null) {
			connection.stop();
			connection.close();
			connection = null;
		}
		if (server != null) {
			server.stop();
		}
		if (jndi != null) {
			jndi.close();
		}
	}

	protected static Connection createConnection() throws JMSException {
		return connFactory.createConnection(user, password);
	}
	
	protected Object lookup(String name) throws NamingException {
		log.debug("lookup:" + name);
		return (server != null) ?
			server.lookup(name) :
			jndi.lookup(name);	
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
            log.debug(String.format("waiting for %s to start", catcher.getName()));
            Thread.sleep(2000);
        }
	}
	
	protected void shutdownCatcher(MessageCatcher catcher) throws Exception {
    	if (catcher != null) {
	        for (int i=0; catcher.isStarted() != true && i< 10; i++) {
	            log.debug(String.format("waiting for %s to start", catcher.getName()));
	            Thread.sleep(2000);
	        }
	        catcher.stop();
	        for (int i=0; catcher.isStopped() != true && i<10; i++) {
	            log.debug(String.format("waiting for %s to stop", catcher.getName()));
	            Thread.sleep(2000);
	        }
    	}		
	}
	
}
