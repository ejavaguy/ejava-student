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
public class JMSAdminArtemis implements JMSAdmin {
	private static final Logger logger = LoggerFactory.getLogger(JMSAdminArtemis.class);
	private Connection connection;
	private Queue managementQueue;
	private String jndiPrefix;
	
	public JMSAdminArtemis(ConnectionFactory connFactory, String adminUser, String adminPassword) throws JMSException {
//		connection = connFactory.createConnection(adminUser, adminPassword);
//		connection.start();
	}
	
	@Override
	public JMSAdmin setJNDIPrefix(String prefix) {
		this.jndiPrefix = prefix;
		return this;
	}
	
	@Override
	public void close() throws JMSException {
//		if (connection != null) {
//			connection.close();
//		}
	}

	@Override
	public JMSAdmin deployTopic(String name, String jndiName) throws Exception {
		return this;
	}

	@Override
	public JMSAdmin deployQueue(String name, String jndiName) throws Exception {
		return this;
	}

	@Override
	public JMSAdmin destroyTopic(String name) throws Exception {
		return this;
	}

	@Override
	public JMSAdmin destroyQueue(String name) throws Exception {
		return this;
	}
}
