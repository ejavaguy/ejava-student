package ejava.examples.jmsmechanics;

import static org.junit.Assert.*;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a test of a transacted session using a queue. 
 * Receivers should not receive messages until they are committed by the 
 * sender.
 */
public class TransactedQueueSessionTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(TransactedQueueSessionTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
        
        catcher1 = createCatcher("receiver1", destination);
        catcher2 = createCatcher("receiver2", destination);
    }

    @After
    public void tearDown() throws Exception {
    	shutdownCatcher(catcher1);
    	shutdownCatcher(catcher2);
    }

    @Test
    public void testTransactedQueueSessionSend() throws Exception {
        logger.info("*** testTransactedQueueSessionSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE); //<!-- TRUE=transacted
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
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.commit();         //COMMIT OUSTANDING MESSAGES FOR SESSION
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
    public void testRollbackTransactedQueueSessionSend() throws Exception {
        logger.info("*** testRollbackTransactedQueueSessionSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE); //<!-- TRUE=transacted
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
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.rollback();    //ROLLBACK OUSTANDING MESSAGES FOR SESSION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() + 
                 catcher2.getMessages().size()< 1); i++) {
                logger.debug("waiting for rolled back messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, catcher2.getMessages().size());
            assertEquals(0, catcher1.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testTransactedQueueSessionMultiSend() throws Exception {
        logger.info("*** testTransactedQueueSessionMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE);  //<!-- TRUE=transacted
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
            assertEquals(0, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
            session.commit();   //COMMIT OUSTANDING MESSAGES FOR SESSION
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

    @Test
    public void testRollbackTransactedQueueSessionMultiSend() throws Exception {
        logger.info("*** testRollbackTransactedQueueSessionMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE);  //<!-- TRUE=transacted
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
            assertEquals(0, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
            session.rollback();   //ROLLBACK OUSTANDING MESSAGES FOR SESSION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() +
                 catcher2.getMessages().size()< msgCount); i++) {
                logger.debug("waiting for rolled back messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
}
