package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs a demonstration of using a message priorities.
 * Messages will be sent in an ad-hoc priority order and then later received.
 * Although the provider is not actually specified in the actual behavior,
 * you would expect some type of priority ordering in this case.
 */
public class MessagePriorityTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessagePriorityTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
    }
    
    @Test
    public void testProducerPriority() throws Exception {
        logger.info("*** testProducerPriority ***");
        try (JMSContext context=createContext()) {
            context.stop();

            try (JMSConsumer consumer = context.createConsumer(destination)) {
                //create a synchronous client to poll for messages -- better control            
                SyncClient client = new SyncClient(consumer);
                
                //send the messages
                int priorities[] = {9,0,8,1,7,2,6,3,6,4,5};             
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                for (int i=0; i<msgCount; i++) {
                    for (int priority : priorities) {
                        producer.setPriority(priority);
                        producer.send(destination, message);
                        logger.info("sent ({}) msgId={}, priority={}", 
                                i,message.getJMSMessageID(), message.getJMSPriority());
                    }
                }
                
                //gather received messages
                context.start();
                int receivedCount=0;
                int prevPriority = 9;
                for(int i=0; i<10 || i<msgCount; i++) {
                    for (Message m=client.getMessage(); m!=null; ) {
                       receivedCount += 1;
                       int priority = m.getJMSPriority();
                       if (priority > prevPriority) {
                           logger.warn("previous priority={} received {}", prevPriority, priority);
                       }
                       prevPriority = priority;
                       m = client.getMessage();
                    }
                    if (receivedCount == (priorities.length*msgCount)) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                logger.info("client received {} msgs", client.getCount());
                assertEquals(msgCount*priorities.length, 
                        client.getCount());
            }
        }
    }    

    @Test
    public void testSendPriority() throws Exception {
        logger.info("*** testSendPriority ***");
        try (JMSContext context=createContext()) {
            context.stop();
            
            try (JMSConsumer consumer=context.createConsumer(destination)) {
                //create a synchronous client to poll for messages -- better control            
                SyncClient client = new SyncClient(consumer);
                
                //send messages and different priorities
                Integer priorities[] = {9,0,8,1,7,2,6,3,6,4,5};             
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                for (int i=0; i<msgCount; i++) {
                    for (int priority : priorities) {
                        producer.setPriority(priority);
                        producer.send(destination, message);
                        logger.info("sent ({}) msgId={}, priority={}", 
                                i, message.getJMSMessageID(), message.getJMSPriority());
                    }
                }
                
                context.start();
                int receivedCount=0;
                int prevPriority = 9;
                Set<Integer> remainingPriorities = new HashSet<>(Arrays.asList(priorities));
                for(int i=0; i<10 || i<msgCount; i++) {
                    for (Message m=client.getMessage(); m!=null; ) {
                       receivedCount += 1;
                       int priority = m.getJMSPriority();
                       if (priority > prevPriority) {
                           logger.warn("previous priority={} received {}", prevPriority, priority);
                       }
                       remainingPriorities.remove(priority);
                       prevPriority = priority;
                       m = client.getMessage();
                    }
                    if (receivedCount == (priorities.length*msgCount)) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                logger.info("client received {} msgs", client.getCount());
                assertEquals(msgCount*priorities.length, 
                        client.getCount());
                assertTrue("not all priorities seen: {}" + remainingPriorities, remainingPriorities.isEmpty());
            }

            
            
        }
    }    

    /**
     * This test demonstrates that Message.setPriority() is not the method to 
     * use to send a message with a specific priority.
     */
    @Test
    public void testMessagePriority() throws Exception {
        logger.info("*** testMessagePriority ***");
        try (JMSContext context=createContext()) {
            context.stop();

            try (JMSConsumer consumer = context.createConsumer(destination)) {
                //create a client to synchronously poll for messages
                SyncClient client = new SyncClient(consumer);

                    //send some messages at various priorities
                Integer priorities[] = {9,0,8,1,7,2,6,3,6,4,5};             
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                for (int i=0; i<msgCount; i++) {
                    for (int priority : priorities) {
                        message.setJMSPriority(priority);  //<== this is *NOT* how we do this!!!
                        producer.send(destination, message);
                        logger.info("sent ({}) msgId={}, priority={}", 
                                i, message.getJMSMessageID(), message.getJMSPriority());
                    }
                }
                
                    //gather the messages from the client
                context.start();
                int receivedCount=0;
                int prevPriority = 9;
                Set<Integer> remainingPriorities = new HashSet<>(Arrays.asList(priorities));
                for(int i=0; i<10 || i<msgCount; i++) {
                    for (Message m=client.getMessage(); m!=null; ) {
                       receivedCount += 1;
                       int priority = m.getJMSPriority();
                       if (priority > prevPriority) {
                           logger.warn("previous priority={} received={}", prevPriority, priority);
                       }
                       remainingPriorities.remove(priority);
                       prevPriority = priority;
                       m = client.getMessage();
                    }
                    if (receivedCount == (priorities.length*msgCount)) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                logger.info("client received {} msgs", client.getCount());
                assertEquals(msgCount*priorities.length, 
                        client.getCount());
                assertFalse("all priorities seen", remainingPriorities.isEmpty()); //<== we are looking for failure
                logger.info("priorties not seen: {}", remainingPriorities);
            }
        }
    }    

}
