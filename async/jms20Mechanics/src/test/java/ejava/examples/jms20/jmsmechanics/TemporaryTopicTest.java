package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;

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
 * This test case tests the ability to create and send/receive messages 
 * to/from a temporary topic. 
 */
public class TemporaryTopicTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(TemporaryTopicTest.class);
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;
    protected int msgCount;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        	shutdownCatcher(catcher1);
        	shutdownCatcher(catcher2);
    }
    
    protected void startCatchers(JMSContext parentContext, Destination destination) 
    		throws Exception {
        catcher1 = createCatcher("subscriber1", destination);
        catcher2 = createCatcher("subscriber2", destination);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
        
        //catchers will create child JMSContexts from this parentContext
        startCatcher(catcher1, parentContext);
        startCatcher(catcher2, parentContext);
    }

    @Test
    public void testTemporaryTopicSend() throws Exception {
        logger.info("*** testTemporaryTopicSend ***");
        try (JMSContext context=createContext()) {
            context.stop();
            
            Topic destination = context.createTemporaryTopic();
            logger.debug("created temporary topic={}", destination);
            startCatchers(context, destination);
            context.start();
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
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
    public void testTemporaryTopicMultiSend() throws Exception {
        logger.info("*** testTemporaryTopicMultiSend ***");
        try (JMSContext context=createContext()) {
            context.stop();
            
            Topic destination = context.createTemporaryTopic();
            logger.debug("created temporary topic={}", destination);
            startCatchers(context, destination);
            context.start();
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            
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
