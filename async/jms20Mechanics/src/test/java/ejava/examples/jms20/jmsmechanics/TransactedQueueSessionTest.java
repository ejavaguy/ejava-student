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
        try (JMSContext context=createContext(Session.SESSION_TRANSACTED)) {
            
            //send a message to the queue
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());

            //queues will hold messages waiting for delivery. We don't have
            //to have catcher started prior to sending the message to the queue 
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
                //check that no messages have arrived yet -- we have not yet committed tx
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            context.commit();         //COMMIT OUSTANDING MESSAGES FOR SESSION
            
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
    public void testRollbackTransactedQueueSessionSend() throws Exception {
        logger.info("*** testRollbackTransactedQueueSessionSend ***");
        try (JMSContext context = createContext(Session.SESSION_TRANSACTED)) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());

            //queues will hold messages waiting for delivery. We don't have
            //to have catcher started prior to sending the message to the queue 
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
                //we have not yet commited the Tx 
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            context.rollback();    //ROLLBACK OUSTANDING MESSAGES FOR SESSION
            for(int i=0; i<5 && 
                (catcher1.getMessages().size() + 
                 catcher2.getMessages().size()< 1); i++) {
                logger.debug("waiting for rolled back messages...");
                Thread.sleep(1000);
            }
                //no messages will be delivered for rollback
            assertEquals(0, catcher2.getMessages().size());
            assertEquals(0, catcher1.getMessages().size());
        }
    }

    @Test
    public void testTransactedQueueSessionMultiSend() throws Exception {
        logger.info("*** testTransactedQueueSessionMultiSend ***");
        try (JMSContext context=createContext(Session.SESSION_TRANSACTED)) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            //queues will hold messages waiting for delivery
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
                //Tx not yet committed
            assertEquals(0, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
            context.commit();   //COMMIT OUSTANDING MESSAGES FOR SESSION
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

    @Test
    public void testRollbackTransactedQueueSessionMultiSend() throws Exception {
        logger.info("*** testRollbackTransactedQueueSessionMultiSend ***");
        try (JMSContext context=createContext(Session.SESSION_TRANSACTED)) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            //queues will hold messages waiting for delivery
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
                //TX not yet committed
            assertEquals(0, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
            context.rollback();   //ROLLBACK OUSTANDING MESSAGES FOR SESSION
            for(int i=0; i<5 && 
                (catcher1.getMessages().size() +
                 catcher2.getMessages().size()< msgCount); i++) {
                logger.debug("waiting for rolled back messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
        }
    }
}
