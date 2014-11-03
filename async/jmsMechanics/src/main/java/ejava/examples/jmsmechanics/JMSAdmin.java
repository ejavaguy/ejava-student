package ejava.examples.jmsmechanics;

/**
 * This interface defines a runtime capability to manage topics and queues
 * on the JMS server.
 */
public interface JMSAdmin {
	JMSAdmin deployTopic(String name, String jndiName)
			throws Exception;
	JMSAdmin deployQueue(String name, String jndiName)
			throws Exception;
	JMSAdmin destroyTopic(String name) throws Exception;
	JMSAdmin destroyQueue(String name) throws Exception;

	/**
	 * Sets a prefix to add to any JNDI name registered. This is required
	 * when the server is running within the application server and only 
	 * specially prefixed JNDI names are made available for global use.
	 * @param string
	 * @return
	 */
	JMSAdmin setJNDIPrefix(String string);

	void close() throws Exception;
}