package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.*;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jms20.jmsmechanics.MessageCatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a test of a transacted session using a topic. 
 * Receivers should not receive messages until they are committed by the 
 * sender.
 */
public class TransactedTopicSessionTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(TransactedTopicSessionTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("null destination:" + topicJNDI, destination);
        
        catcher1 = createCatcher("subscriber1", destination);
        catcher2 = createCatcher("subscriber2", destination);
    }
    
    @After
    public void tearDown() throws Exception {
        shutdownCatcher(catcher1);
    	    shutdownCatcher(catcher2);
    }

    @Test
    public void testTransactedTopicSessionSend() throws Exception {
        logger.info("*** testTransactedTopicSessionSend ***");
        try (JMSContext context=createContext(Session.SESSION_TRANSACTED)) {            
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            //topics will only deliver messages to subscribers that are 
            //successfully registered prior to the message being published. We
            //need to wait for the catcher to start so it doesn't miss any 
            //messages.
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            context.commit(); //<!-- COMMITTING SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, catcher1.getMessages().size());
            assertEquals(1, catcher2.getMessages().size());
        }
    }

    @Test
    public void testRollbackTransactedTopicSessionSend() throws Exception {
        logger.info("*** testRollbackTransactedTopicSessionSend ***");
        try (JMSContext context = createContext(Session.SESSION_TRANSACTED)) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            context.rollback(); //<!-- ROLLING BACK SESSION TRANSACTION
            
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                logger.debug("waiting for rolled back messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
        }
    }
    
    @Test
    public void testTransactedTopicSessionMultiSend() throws Exception {
        logger.info("*** testTransactedTopicSessionMultiSend ***");
        try (JMSContext context=createContext(Session.SESSION_TRANSACTED)) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            context.commit(); //<!-- COMMITTING SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(msgCount, catcher1.getMessages().size());
            assertEquals(msgCount, catcher2.getMessages().size());
        }
    }

    @Test
    public void testRolledbackTransactedTopicSessionMultiSend() throws Exception {
        logger.info("*** testRolledbackTransactedTopicSessionMultiSend ***");
        try (JMSContext context=createContext(Session.SESSION_TRANSACTED)) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            context.rollback(); //<!-- ROLLBACK SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                logger.debug("waiting for rolledback messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
        }
    }
}
