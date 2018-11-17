package ejava.examples.jms10.jmsmechanics;

import static org.junit.Assert.*;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jms10.jmsmechanics.MessageCatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        
        catcher1 = createCatcher("receiver1", destination).setAckMode(Session.CLIENT_ACKNOWLEDGE);
        catcher2 = createCatcher("receiver2", destination).setAckMode(Session.CLIENT_ACKNOWLEDGE);
    }
    
    @After
    public void tearDown() throws Exception {
        	shutdownCatcher(catcher1);
        	shutdownCatcher(catcher2);
    }

    @Test
    public void testQueueSend() throws Exception {
        logger.info("*** testQueueSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            producer.send(message);
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
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testQueueMultiSend() throws Exception {
        logger.info("*** testQueueMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
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
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
}
