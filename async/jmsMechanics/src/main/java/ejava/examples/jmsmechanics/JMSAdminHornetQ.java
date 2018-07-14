package ejava.examples.jmsmechanics;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.management.JMSManagementHelper;

/**
 * This class implements a client to dynamically create JMS resources on the
 * server.
 */
public class JMSAdminHornetQ implements JMSAdmin {
	private static final Log log = LogFactory.getLog(JMSAdminHornetQ.class);
	private Connection connection;
	private Queue managementQueue;
	private String jndiPrefix;
	
	public JMSAdminHornetQ(ConnectionFactory connFactory, String adminUser, String adminPassword) throws JMSException {
		connection = connFactory.createConnection(adminUser, adminPassword);
		connection.start();
		managementQueue = HornetQJMSClient.createQueue("hornetq.management");   
	}
	
	@Override
	public JMSAdmin setJNDIPrefix(String prefix) {
		this.jndiPrefix = prefix;
		return this;
	}
	
	@Override
	public void close() throws JMSException {
		if (connection != null) {
			connection.close();
		}
	}
	
	/**
	 * Concatenates two JNDI names making sure there is a single "/" character
	 * separating them
	 * @param name1
	 * @param name2
	 * @return
	 */
	private static String concat(String name1, String name2) {
		if (name1==null) { return name2; }
		else if (name2==null) { return name1; }
		
		String name= name1.endsWith("/") || name2.startsWith("/") ?
				String.format("%s%s", name1, name2) : 
				String.format("%s/%s", name1, name2); 
		name=name.replace("//", "/");
		return name;
	}

	public JMSAdmin deployDestination(String method, String name, String jndiName) throws Exception {
		Session session=null;
		QueueRequestor requestor = null;
	   try {
		   session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		   requestor=new QueueRequestor((QueueSession) session, managementQueue);
		   Message message = session.createMessage();
		   jndiName = concat(jndiPrefix, jndiName);
		   JMSManagementHelper.putOperationInvocation(message, "jms.server", method, name, jndiName);
		   log.debug(String.format("%s: %s, jndi=%s", method, name, jndiName));
		   Message reply = requestor.request(message);
		   if (!JMSManagementHelper.hasOperationSucceeded(reply)) {
			   throw new RuntimeException("failed to create desintation:" + name);
			   }
	   } finally {
		   if (requestor != null) { requestor.close(); }
		   if (session != null) { session.close(); }
	   }
   
   return this;
   }

	public JMSAdmin destroyDestination(String method, String name) throws Exception {
		Session session=null;
		QueueRequestor requestor = null;
        try {
 		   session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		   requestor=new QueueRequestor((QueueSession) session, managementQueue);
		   Message message = session.createMessage();
		   JMSManagementHelper.putOperationInvocation(message, "jms.server", method, name);
		   Message reply = requestor.request(message);
		   if (!JMSManagementHelper.hasOperationSucceeded(reply)) {
			   log.info("failed to destroy desintation:" + name);
		   }
	    } finally {
		   if (requestor != null) { requestor.close(); }
		   if (session != null) { session.close(); }
	    }
	   
	   return this;
	}

	@Override
	public JMSAdmin deployTopic(String name, String jndiName) throws Exception {
		return deployDestination("createTopic", name, jndiName);
	}

	@Override
	public JMSAdmin deployQueue(String name, String jndiName) throws Exception {
		return deployDestination("createQueue", name, jndiName);
	}

	@Override
	public JMSAdmin destroyTopic(String name) throws Exception {
		return destroyDestination("destroyTopic", name);
	}

	@Override
	public JMSAdmin destroyQueue(String name) throws Exception {
		return destroyDestination("destroyQueue", name);
	}
}
