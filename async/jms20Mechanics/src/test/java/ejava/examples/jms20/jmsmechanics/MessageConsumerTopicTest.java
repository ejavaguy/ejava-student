package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs a demonstration of the two mechanisms that a 
 * MessageConsumer has for receiving messages using a Topic.
 */
public class MessageConsumerTopicTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageConsumerTopicTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("null destination:" + topicJNDI, destination);
    }

    @Test
    public void testMessageConsumer() throws Exception {
        logger.info("*** testMessageConsumer ***");
        try (JMSContext context=createContext();
             JMSContext context2=context.createContext(Session.AUTO_ACKNOWLEDGE)) {
            context.stop();
            
            try (JMSConsumer syncConsumer = context.createConsumer(destination);
                 JMSConsumer asyncConsumer = context2.createConsumer(destination)) {
                
                //create a client to synchronously poll for messages
                SyncClient syncClient = new SyncClient(syncConsumer);
                
                //create a client to asynchronous receive messages through onMessage() callbacks
                AsyncClient asyncClient = new AsyncClient();
                asyncConsumer.setMessageListener(asyncClient);

                //send a message
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());

                //gather the messages from the clients
                context.start();
                int receivedCount=0;
                for(int i=0; i<10; i++) {
                    for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                        Message m = client.getMessage();
                        receivedCount += (m != null ? 1 : 0);
                    }
                    if (receivedCount == 2) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                assertEquals(1, asyncClient.getCount());
                assertEquals(1, syncClient.getCount());
            }
        }
    }
    
    @Test
    public void testMessageConsumerMulti() throws Exception {
        logger.info("*** testMessageConsumerMulti ***");
        try (JMSContext context=createContext(Session.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(Session.CLIENT_ACKNOWLEDGE)) {
            context.stop();
            
            try (JMSConsumer syncConsumer = context.createConsumer(destination);
                 JMSConsumer asyncConsumer = context2.createConsumer(destination)) {
                
                //create a client to asynchronous receive messages through onMessage() callbacks
                AsyncClient asyncClient = new AsyncClient();
                asyncConsumer.setMessageListener(asyncClient);

                //create a client to synchronously poll for messages
                SyncClient syncClient = new SyncClient(syncConsumer);
                
                //send some messages to the waiting subscribers
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                for (int i=0; i<msgCount; i++) {
                    producer.send(destination, message);
                    logger.info("sent msgId={}", message.getJMSMessageID());
                }

                //collection messages from subscribers
                connection.start();
                int receivedCount=0;
                for(int i=0; i<10 || i<msgCount; i++) {
                    for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                        Message m=null;
                        do {
                           m = client.getMessage();
                           receivedCount += (m != null ? 1 : 0);
                        } while (m != null);
                    }
                    if (receivedCount == 2*msgCount) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(10);
                }
                assertEquals(msgCount, asyncClient.getCount());
                assertEquals(msgCount, syncClient.getCount());
            }
        }
    }    
    
}
