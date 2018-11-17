package ejava.examples.jms10.jmsmechanics;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jms10.jmsmechanics.MessageCatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs the basic steps to send/receive messages to/from
 * a JMS topic. In this test, the receiver performs a manual acknowledgement
 * of the last message received.
 */
public class ClientAckTopicTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(ClientAckTopicTest.class);
    Destination destination;        
    MessageCatcher catcher1;
    MessageCatcher catcher2;
    
    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("destination null:" + topicJNDI, destination);
        
        catcher1 = createCatcher("subscriber1", destination).setAckMode(Session.CLIENT_ACKNOWLEDGE);
        catcher2 = createCatcher("subscriber2", destination).setAckMode(Session.CLIENT_ACKNOWLEDGE);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
        startCatcher(catcher1);
        startCatcher(catcher2);
    }

    @After
    public void tearDown() throws Exception {
    	shutdownCatcher(catcher1);
    	shutdownCatcher(catcher2);
    }

    @Test
    public void testTopicSend() throws Exception {
        logger.info("*** testTopicSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            producer.send(message);
            logger.info("sent msgId={}", message.getJMSMessageID());
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, catcher1.getMessages().size());
            assertEquals(1, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testTopicMultiSend() throws Exception {
        logger.info("*** testTopicMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(msgCount, catcher1.getMessages().size());
            assertEquals(msgCount, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
}
