package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a demonstration of durable topic subscriptions. 
 */
public class DurableSubscriberTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(DurableSubscriberTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("destination null:" + topicJNDI, destination);
    }
    
    @Test
    public void testNonDurableSubscription() throws Exception {
        logger.info("*** testNonDurableSubscription ***");
        //publish messages while no subscribers are in place
        try (JMSContext context=createContext();) {
            Message message = context.createMessage();
            JMSProducer producer = context.createProducer();
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());
        }
        
        //start the consumers to process messages after they were published
        try (JMSContext context = createContext()) {
            context.start();
            try (JMSContext context1 = context.createContext(Session.AUTO_ACKNOWLEDGE);
                 JMSContext context2 = context.createContext(Session.AUTO_ACKNOWLEDGE)) {                    
                
                try (JMSConsumer syncConsumer = context1.createConsumer(destination);
                     JMSConsumer asyncConsumer = context2.createConsumer(destination)){
                        //register async client as a callback to the JMSConsumer
                    AsyncClient asyncClient = new AsyncClient();
                    asyncConsumer.setMessageListener(asyncClient);
                    
                        //store the JMSConsumer for the sync client to poll
                    SyncClient syncClient = new SyncClient(syncConsumer);
                    
                    int receivedCount=0;                        
                    for(int i=0; i<10; i++) {
                        for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                            Message m = client.getMessage();
                            receivedCount += (m != null ? 1 : 0);
                        }
                        if (receivedCount == 2) { break; }
                        logger.debug("waiting for non-durable subscription messages...");
                        Thread.sleep(1000);
                    }
                    //we should not have received any -- we subscribed after messages sent
                    assertEquals(0, asyncClient.getCount());
                    assertEquals(0, syncClient.getCount());                        
                }
            }
        }
    }
    
    @Test
    public void testDurableSubscription() throws Exception {
        logger.info("*** testDurableSubscription ***");
        //setup subscriptions
        try (JMSContext context=createContext()) {
                //set the clientID required by the durable subscriptions
            context.setClientID("testDurableSubscription");
                //setup the durable subscriptions with the JMS server
            try (JMSContext context1=context.createContext(Session.AUTO_ACKNOWLEDGE);
                 JMSContext context2=context.createContext(Session.AUTO_ACKNOWLEDGE)) {
                //make sure we don't have pre-existing subscriptions
                try { context1.unsubscribe("sync1"); } catch(Exception ignored){}
                try { context2.unsubscribe("async1"); } catch(Exception ignored){}
                
                //create a client to asynchronous receive messages through 
                //onMessage() callbacks - USE A DURABLE TOPIC SUBSCRIPTION
                try (JMSConsumer syncConsumer = context1.createDurableConsumer((Topic)destination,"sync1");                        
                     JMSConsumer asyncConsumer = context2.createDurableConsumer((Topic)destination,"async1")) {
                }
            }
        }

        //publish messages while subscribers are not running but subscriptions in place
        try (JMSContext context=createContext();) {
            Message message = context.createMessage();
            JMSProducer producer = context.createProducer();
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());
        }

        //now lets go away for a while
        connection.close(); connection=null;
        //come back and receive the messages
        connection = createConnection();
        
        //start the consumers to process messages waiting with their durable subscription
        try (JMSContext context = createContext()) {
            context.setClientID("testDurableSubscription");
            context.start();
            try (JMSContext context1 = context.createContext(Session.AUTO_ACKNOWLEDGE);
                 JMSContext context2 = context.createContext(Session.AUTO_ACKNOWLEDGE)) {
                
                //now get in late for the messages - RESUME DURABLE SUBSCRIPTION
                try (JMSConsumer syncConsumer = context1.createDurableConsumer((Topic)destination,"sync1");
                     JMSConsumer asyncConsumer = context2.createDurableConsumer((Topic)destination, "async1")){
                        //register async client as a callback to the JMSConsumer
                    AsyncClient asyncClient = new AsyncClient();
                    asyncConsumer.setMessageListener(asyncClient);
                    
                        //store the JMSConsumer for the sync client to poll
                    SyncClient syncClient = new SyncClient(syncConsumer);
                    
                    int receivedCount=0;                        
                    for(int i=0; i<10; i++) {
                        for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                            Message m = client.getMessage();
                            receivedCount += (m != null ? 1 : 0);
                        }
                        if (receivedCount == 2) { break; }
                        logger.debug("waiting for durable subscription messages...");
                        Thread.sleep(1000);
                    }
                    assertEquals(1, asyncClient.getCount());
                    assertEquals(1, syncClient.getCount());                        
                }
            }
        }
    }
    
}
