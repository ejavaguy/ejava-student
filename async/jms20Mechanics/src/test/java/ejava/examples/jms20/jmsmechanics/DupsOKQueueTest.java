package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs the basic steps to send/receive messages to/from
 * a JMS queue. In this test, the receiver allows session to lazily acknowledge
 * messages.
 */
public class DupsOKQueueTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(DupsOKQueueTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("destination null:" + queueJNDI, destination);
        
        catcher1 = createCatcher("receiver1", destination).setAckMode(Session.DUPS_OK_ACKNOWLEDGE);
        catcher2 = createCatcher("receiver2", destination).setAckMode(Session.DUPS_OK_ACKNOWLEDGE);
    }
    
    @After
    public void tearDown() throws Exception {
        	shutdownCatcher(catcher1);
        	shutdownCatcher(catcher2);
    }

    @Test
    public void testQueueSend() throws Exception {
        logger.info("*** testQueueSend ***");
        try (JMSContext context=createContext()) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());

            //queues will hold messages waiting for delivery. We don't have
            //to have catcher started prior to sending the message to the 
            //queue.
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
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
        try (JMSContext context=createContext()){
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            //queues will hold messages waiting for delivery
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
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
