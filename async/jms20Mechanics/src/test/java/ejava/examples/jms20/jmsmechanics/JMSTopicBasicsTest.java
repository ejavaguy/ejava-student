package ejava.examples.jms20.jmsmechanics;

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

import static org.junit.Assert.*;

/**
 * This test case performs the basic steps to send/receive messages to/from
 * a JMS topic. Notice that each of the catchers in this test should each
 * receive the same messages.
 */
public class JMSTopicBasicsTest extends JMSTestBase {
    private static final Logger logger = LoggerFactory.getLogger(JMSTopicBasicsTest.class);
    private Destination destination;        
    private MessageCatcher catcher1;
    private MessageCatcher catcher2;
    
    @Before
    public void setUp() throws Exception {
                
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("destination null:" + topicJNDI, destination);
        
        catcher1 = createCatcher("subscriber1", destination);
        catcher2 = createCatcher("subscriber2", destination);
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

            //topics will only deliver messages to subscribers that are 
            //successfully registered prior to the message being published. We
            //need to wait for the catcher to start so it doesn't miss any 
            //messages.
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
        try (JMSContext context = createContext()) {
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
            //topics will only deliver messages to subscribers that are 
            //successfully registered prior to the message being published. We
            //need to wait for the catcher to start so it doesn't miss any 
            //messages.
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
