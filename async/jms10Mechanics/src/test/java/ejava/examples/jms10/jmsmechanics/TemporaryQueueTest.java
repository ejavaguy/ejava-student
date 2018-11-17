package ejava.examples.jms10.jmsmechanics;

import static org.junit.Assert.*;


import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jms10.jmsmechanics.MessageCatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case tests the ability to create and send/receive messages 
 * to/from a temporary queue. 
 */
public class TemporaryQueueTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(TemporaryQueueTest.class);
    protected Session session = null;
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;
    protected int msgCount;
    
    @Before
    public void setUp() throws Exception {
        catcher1 = createCatcher("receiver1", null);
        catcher2 = createCatcher("receiver2", null);
    }
    
    @After
    public void tearDown() throws Exception {
    	shutdownCatcher(catcher1);
    	shutdownCatcher(catcher2);
        if (session != null)  { session.close(); }
    }


    @Test
    public void testTemporaryQueueSend() throws Exception {
        logger.info("*** testTemporaryQueueSend ***");
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            catcher1.setSession(session);
            catcher2.setSession(session);
            Destination destination = session.createTemporaryQueue();
            catcher1.setDestination(destination);
            catcher2.setDestination(destination);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
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
            if (connection != null) { connection.stop(); }
            if (producer != null) { producer.close(); }
        }
    }

    @Test
    public void testTemporaryQueueMultiSend() throws Exception {
        logger.info("*** testTemporaryQueueMultiSend ***");
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            connection.start();
            catcher1.setSession(session);
            catcher2.setSession(session);
            Destination destination = session.createTemporaryQueue();
            catcher1.setDestination(destination);
            catcher2.setDestination(destination);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
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
            if (connection != null) { connection.stop(); }
            if (producer != null) { producer.close(); }
        }
    }
}
