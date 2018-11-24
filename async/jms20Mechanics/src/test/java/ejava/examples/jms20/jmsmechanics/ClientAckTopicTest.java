package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Topic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        
        catcher1 = createCatcher("subscriber1", destination).setAckMode(JMSContext.CLIENT_ACKNOWLEDGE
);
        catcher2 = createCatcher("subscriber2", destination).setAckMode(JMSContext.CLIENT_ACKNOWLEDGE
);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
    }

    @After
    public void tearDown() throws Exception {
        	shutdownCatcher(catcher1);
        	shutdownCatcher(catcher2);
    }

    @Test
    public void testTopicSend() throws Exception {
        logger.info("*** testTopicSend ***");
        try (JMSContext context=createContext()) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            //we need to make sure the catchers are subscribed before we begin sending messages
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
            producer.send(destination, message);
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
    }

    @Test
    public void testTopicMultiSend() throws Exception {
        logger.info("*** testTopicMultiSend ***");
        try (JMSContext context=createContext()) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            //we need to make sure the catchers are subscribed before we begin sending messages
            startCatcher(catcher1, context);
            startCatcher(catcher2, context);
            for(int i=0; i<msgCount; i++) {
                producer.send(destination, message);
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
    }
}
