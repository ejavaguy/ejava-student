package ejava.examples.jmsmechanics;

import static org.junit.Assert.*;


import java.util.LinkedList;

import javax.jms.Destination;
import javax.jms.JMSException;
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
    
    private interface MyClient {
        int getCount();
        Message getMessage() throws Exception;
    }
    private class AsyncClient implements MessageListener, MyClient {
        private int count=0;
        LinkedList<Message> messages = new LinkedList<Message>();
        public void onMessage(Message message) {
            try {
                long now = System.currentTimeMillis();
                long expiration = message.getJMSExpiration();
                logger.debug("onMessage received ({}):{}, expiration={}, {} msecs", 
                        ++count, message.getJMSMessageID(), expiration , 
                        (expiration == 0 ? 0 : expiration-now));
                messages.add(message);
            } catch (JMSException ex) {
                logger.error("error handling message", ex);
            }
        }        
        public int getCount() { return count; }
        public Message getMessage() {
            return (messages.isEmpty() ? null : messages.remove());
        }
    }

    @Test
    public void testProducerTimeToLive() throws Exception {
        logger.info("*** testProducerPriority ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            consumer = session.createConsumer(destination);
            AsyncClient client = new AsyncClient();
            consumer.setMessageListener(client);
            
            long ttlMsecs[] = {100, 0, 10000, 100, 10000};             
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            int count=0;
            for (int i=0; i<msgCount; i++) {
                for (long ttl : ttlMsecs) {
                    producer.setTimeToLive(ttl);
                    producer.send(message);
                    long now = System.currentTimeMillis();
                    long expiration = message.getJMSExpiration();
                    logger.info("sent ({}) msgId={}, expiration={} msecs", 
                            ++count, message.getJMSMessageID(), expiration, 
                            (expiration == 0 ? 0 : expiration-now));
                }
            }
            
            long sleepTime = 1000; 
            logger.info("waiting {} msecs for some messages to expire", sleepTime);
            Thread.sleep(sleepTime);  //wait for some to expire
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10 || i<msgCount; i++) {
                Message m=null;
                do {
                   m = client.getMessage();
                   receivedCount += (m != null ? 1 : 0);
                } while (m != null);
                if (receivedCount == (3*msgCount)) { break; }
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            logger.info("client received {} msgs", client.getCount());
            assertEquals(3*msgCount, 
                    client.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }    

    @Test
    public void testSendTimeToLive() throws Exception {
        logger.info("*** testSendTimeToLive ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            consumer = session.createConsumer(destination);
            AsyncClient client = new AsyncClient();
            consumer.setMessageListener(client);
            
            long ttlMsecs[] = {100, 0, 10000, 100, 10000};             
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            int count=0;
            for (int i=0; i<msgCount; i++) {
                for (long ttl : ttlMsecs) {
                    producer.send(message,
                            Message.DEFAULT_DELIVERY_MODE,
                            Message.DEFAULT_PRIORITY,
                            ttl);
                    long now = System.currentTimeMillis();
                    long expiration = message.getJMSExpiration();
                    logger.info("sent ({}) msgId={}, expiration={}, {} msecs",
                            ++count, message.getJMSMessageID(), expiration,
                            (expiration == 0 ? 0 : expiration-now));
                }
            }
            
            long sleepTime = 1000; 
            logger.info("waiting {} msecs for some messages to expire", sleepTime);
            Thread.sleep(sleepTime);  //wait for some to expire
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10 || i<msgCount; i++) {
                Message m=null;
                do {
                   m = client.getMessage();
                   receivedCount += (m != null ? 1 : 0);
                } while (m != null);
                if (receivedCount == (3*msgCount)) { break; }
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            logger.info("client received {} msgs", client.getCount());
            assertEquals(3*msgCount, 
                    client.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }    
}
