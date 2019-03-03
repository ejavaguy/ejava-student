package ejava.examples.jms20.jmsmechanics;

import java.util.LinkedList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This client provides a JMSConsumer callback to process incoming messages.
 * It runs within a thread of the JMS client provider. 
 */
public class AsyncClient implements MessageListener, MyClient {
    private static final Logger logger = LoggerFactory.getLogger(AsyncClient.class);
    private int count=0;
    private LinkedList<Message> messages = new LinkedList<>();
    
    public void onMessage(Message message) {
        try {
            String level = message.getStringProperty("level");            
            logger.debug("onMessage received ({}, mode={}, pri={}{}):{}", ++count,
                    message.getJMSDeliveryMode(),
                    message.getJMSPriority(),
                    (level==null?"":", level="+level),
                    message.getJMSMessageID());
            message.acknowledge();
            synchronized(messages) {
                messages.add(message);
            }
        } catch (JMSException ex) {
            logger.error("error handling message", ex);
        }
    }
    
    public int getCount() { return count; }
    
    public Message getMessage() {
        synchronized(messages) {
            return (messages.isEmpty() ? null : messages.remove());
        }
    }
}
