package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a demonstration of the two mechanisms that a 
 * MessageConsumer has for receiving messages using a Queue.
 */
public class MessageConsumerQueueTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageConsumerQueueTest.class);
    Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("destination null:" + queueJNDI, destination);
    }
    
    
    @Test
    public void testMessageConsumer() throws Exception {
        logger.info("*** testMessageConsumer ***");
        //need to use CLIENT_ACK to avoid race condition within this app
        try (JMSContext context=createContext(Session.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(Session.CLIENT_ACKNOWLEDGE)) {
            context.stop();

                //send a message
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            producer.send(destination, message);
            logger.info("sent msgId={}", message.getJMSMessageID());
            
            try (JMSConsumer syncConsumer = context.createConsumer(destination);
                 JMSConsumer asyncConsumer = context2.createConsumer(destination)) {
                
                //create a client to synchronously poll for messages            
                SyncClient syncClient = new SyncClient(syncConsumer);
                
                //create a client to asynchronous receive messages through onMessage() callbacks            
                AsyncClient asyncClient = new AsyncClient();
                asyncConsumer.setMessageListener(asyncClient);
                
                //make sure someone received the message
                context.start();
                int receivedCount=0;
                for(int i=0; i<10; i++) {
                    for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                        Message m = client.getMessage();
                        receivedCount += (m != null ? 1 : 0);
                    }
                    if (receivedCount == 1) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                assertEquals(1, asyncClient.getCount() + syncClient.getCount());
            }
        }
    }

    @Test
    public void testMessageConsumerMulti() throws Exception {
        //need to use CLIENT_ACK to avoid race condition within this app
        try (JMSContext context=createContext(Session.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(Session.CLIENT_ACKNOWLEDGE)) {
            context.stop();
            
            //send some messages
            JMSProducer producer = context.createProducer();
            Message message = context.createMessage();
            for (int i=0; i<msgCount; i++) {
                producer.send(destination, message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            
            try (JMSConsumer syncConsumer = context.createConsumer(destination);
                 JMSConsumer asyncConsumer = context2.createConsumer(destination)) {
                
                //create a client to synchronously poll for messages
                SyncClient syncClient = new SyncClient(syncConsumer);
                
                //create a client to asynchronous receive messages through onMessage() callbacks
                AsyncClient asyncClient = new AsyncClient();
                asyncConsumer.setMessageListener(asyncClient);
                
                //start receiving messages
                context.start();
                int receivedCount=0;
                for(int i=0; i<10 || i<msgCount; i++) {
                    for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                        Message m=null;
                        do {
                           m = client.getMessage();
                           receivedCount += (m != null ? 1 : 0);
                        } while (m != null);
                    }
                    if (receivedCount == msgCount) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(5);
                }
                if (msgCount > 10) {
                    if (asyncClient.getCount()==0) {
                        logger.info("asyncClient did not get messages; not really an error");
                    }
                    if (syncClient.getCount()==0) {
                        logger.info("syncClient did not get messages; not really an error");
                    }
                }
                assertEquals(msgCount, 
                        asyncClient.getCount() + syncClient.getCount());
                
            }
        }
    }    
    
}
