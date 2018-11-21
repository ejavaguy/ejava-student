package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.*;


import java.util.LinkedList;

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
import javax.jms.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a demonstration of using a message time to live.
 * Messages will be sent and left in the server beyond a specified time to 
 * live.
 */
public class MessageTimeToLiveTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageTimeToLiveTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:"+ queueJNDI, destination);
    }

    @Test
    public void testProducerTimeToLive() throws Exception {
        logger.info("*** testProducerTimeToLive ***");
        try (JMSContext context=createContext()) {
            context.stop();

            try (JMSConsumer consumer = context.createConsumer(destination)) {
                SyncClient client = new SyncClient(consumer);

                //send some messages with different TTL values
                long ttlMsecs[] = {100, 0, 10000, 100, 10000};             
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                int count=0;
                for (int i=0; i<msgCount; i++) {
                    for (long ttl : ttlMsecs) {
                        producer.setTimeToLive(ttl);
                        producer.send(destination, message);
                        long now = System.currentTimeMillis();
                        long expiration = message.getJMSExpiration();
                        logger.info("sent ({}) msgId={}, expiration={} msecs", 
                                ++count, message.getJMSMessageID(), expiration, 
                                (expiration == 0 ? 0 : expiration-now));
                    }
                }

                    //pause for a period of time to allow some TTLs to expire
                long sleepTime = 1000; 
                logger.info("waiting {} msecs for some messages to expire", sleepTime);
                Thread.sleep(sleepTime);  //wait for some to expire
                
                    //now go get the messages
                context.start();
                int receivedCount=0;
                for(int i=0; i<10 || i<msgCount; i++) {
                    for (Message m = client.getMessage(); m!=null; ) {
                       receivedCount += (m != null ? 1 : 0);
                       m = client.getMessage();
                    }
                    if (receivedCount == (3*msgCount)) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                logger.info("client received {} msgs", client.getCount());
                assertEquals(3*msgCount, 
                        client.getCount());
            }
        }
    }    

    @Test
    public void testSendTimeToLive() throws Exception {
        logger.info("*** testSendTimeToLive ***");
        try (JMSContext context=createContext()) {
            context.stop();

            try (JMSConsumer consumer = context.createConsumer(destination)) {
                SyncClient client = new SyncClient(consumer);

                    //send messages
                long ttlMsecs[] = {100, 0, 10000, 100, 10000};             
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                int count=0;
                for (int i=0; i<msgCount; i++) {
                    for (long ttl : ttlMsecs) {
                        producer.setTimeToLive(ttl);
                        producer.send(destination, message);
                        long now = System.currentTimeMillis();
                        long expiration = message.getJMSExpiration();
                        logger.info("sent ({}) msgId={}, expiration={}, {} msecs",
                                ++count, message.getJMSMessageID(), expiration,
                                (expiration == 0 ? 0 : expiration-now));
                    }
                }

                    //wait for some TTLs to expire
                long sleepTime = 1000; 
                logger.info("waiting {} msecs for some messages to expire", sleepTime);
                Thread.sleep(sleepTime);  //wait for some to expire
                
                    //collect delivered messages
                context.start();
                int receivedCount=0;
                for(int i=0; i<10 || i<msgCount; i++) {
                    for (Message m=client.getMessage(); m!=null; ) {
                       receivedCount += (m != null ? 1 : 0);
                       m = client.getMessage();
                    }
                    if (receivedCount == (3*msgCount)) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                logger.info("client received {} msgs", client.getCount());
                assertEquals(3*msgCount, client.getCount());
            }
        }
    }    
}
