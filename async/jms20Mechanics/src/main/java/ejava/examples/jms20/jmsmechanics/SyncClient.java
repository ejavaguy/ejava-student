package ejava.examples.jms20.jmsmechanics;

import javax.jms.JMSConsumer;
import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This client will synchronously poll for messages from a JMSConsumer. It 
 * runs within the thread of the caller. 
 */
public class SyncClient implements MyClient {
    private static final Logger logger = LoggerFactory.getLogger(SyncClient.class);    
    private JMSConsumer consumer;
    private int count=0;
    
    public SyncClient(JMSConsumer syncConsumer) {
        this.consumer = syncConsumer;
    }
    
    public int getCount() { return count; }
    
    public Message getMessage() throws JMSException {
        Message message=consumer.receiveNoWait();
        if (message != null) {
            String level = message.getStringProperty("level");            
            logger.debug("receive ({}, mode={}, pri={}{}):{}",++count,
                    message.getJMSDeliveryMode(),
                    message.getJMSPriority(),
                    (level==null?"":", level="+level),
                    message.getJMSMessageID());
            message.acknowledge();
        }        
        return message;
    }
}