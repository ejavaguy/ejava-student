package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.jms.Destination;
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
 * a JMS queue. Notice that only one of the catchers in this test should a
 * copy of any single message. 
 */
public class JMSQueueBasicsTest extends JMSTestBase {
    private static final Logger logger = LoggerFactory.getLogger(JMSQueueBasicsTest.class);
    private int msgCount = Integer.parseInt(System.getProperty("multi.message.count", "20"));
    
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;
    
    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("destination null:" + queueJNDI, destination);
        
        catcher1 = createCatcher("receiver1", destination);
        catcher2 = createCatcher("receiver2", destination);
    }
    
    @After
    public void tearDown() throws Exception {
        	shutdownCatcher(catcher1);
        	shutdownCatcher(catcher2);
    }

    @Test
    public void testQueueSend() throws Exception {
        logger.info("*** testQueueSend ***");
            //send a message to queue prior to having consumers
        try (JMSContext context=createContext()) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            producer.send(destination, message);
            logger.info("sent msgId=" + message.getJMSMessageID());
        }
            //queues will hold messages waiting for delivery
        
            //start some consumers to process queue message
        try (JMSContext context=createContext()) {
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() + 
                 catcher2.getMessages().size()< 1); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals("unexpected number of messages consumed", 1, 
                    catcher1.getMessages().size() + catcher2.getMessages().size());
        }
    }
    
    @Test
    public void testQueueMultiSend() throws Exception {
        logger.info("*** testQueueMultiSend ***");
        try (JMSContext context=createContext()){
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
        }
        
            //queues will hold messages waiting for delivery
        try (JMSContext context=createContext()){
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
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
