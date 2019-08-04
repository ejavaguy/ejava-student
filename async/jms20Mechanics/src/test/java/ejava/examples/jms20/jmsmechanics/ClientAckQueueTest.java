package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs the basic steps to send/receive messages to/from
 * a JMS queue. In this test, the receiver performs a manual acknowledgement
 * of the last message received.
 */
public class ClientAckQueueTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(ClientAckQueueTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("destination null:" + queueJNDI, destination);
        
        catcher1 = createCatcher("receiver1", destination).setAckMode(JMSContext.CLIENT_ACKNOWLEDGE);
        catcher2 = createCatcher("receiver2", destination).setAckMode(JMSContext.CLIENT_ACKNOWLEDGE);
        
        //make sure the queue is empty
        try (JMSContext context=createContext();
             JMSConsumer consumer=context.createConsumer(destination)) {
            for (SyncClient cleanup = new SyncClient(consumer); cleanup.getMessage()!=null; ) {}            
        }
    }
    
    @After
    public void tearDown() throws Exception {
        	shutdownCatcher(catcher1);
        	shutdownCatcher(catcher2);
    }

    @Test
    public void testQueueSend() throws Exception {
        logger.info("*** testQueueSend ***");
        try (JMSContext context=createContext()){
            catcher1.setContext(context);
            catcher2.setContext(context);
            catcher1.clearMessages();
            
            Message message = context.createMessage();
            JMSProducer producer = context.createProducer();
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());

            //queues will hold messages waiting for delivery. We don't have
            //to have catcher started prior to sending the message to the 
            //queue.
            new Thread(catcher1).start();
            new Thread(catcher2).start();
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() + 
                 catcher2.getMessages().size()< 1); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            if (catcher1.getMessages().size() == 0) {
                assertEquals(1, catcher2.getMessages().size());
            }
            else {
                assertEquals(1, catcher1.getMessages().size());
            }
        }
    }

    @Test
    public void testQueueMultiSend() throws Exception {
        logger.info("*** testQueueMultiSend ***");
        try (JMSContext context=createContext()) {
            catcher1.setContext(context);
            catcher2.setContext(context);
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            //queues will hold messages waiting for delivery
            new Thread(catcher1).start();
            new Thread(catcher2).start();
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() +
                 catcher2.getMessages().size()< msgCount); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(msgCount, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
        }
    }
}
