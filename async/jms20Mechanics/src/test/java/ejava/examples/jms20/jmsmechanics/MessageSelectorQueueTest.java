package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.*;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
 * This test case performs a demonstration of using a message selector with
 * a MessageConsumer and a Queue. In the specific case tested, the same number
 * of messages sent will be received. However, one of the clients will only
 * receive 'warn' and 'fatal' messages and the other client will receive 
 * 'info', 'warn', and 'fatal'. No one will receive 'debug'.
 */
public class MessageSelectorQueueTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageSelectorQueueTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
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
                logger.debug("onMessage received ({}):{}, level={}", 
                        ++count , message.getJMSMessageID(),  message.getStringProperty("level"));
                messages.add(message);
                message.acknowledge();
            } catch (JMSException ex) {
                logger.error("error handling message", ex);
            }
        }        
        public int getCount() { return count; }
        public Message getMessage() {
            return (messages.isEmpty() ? null : messages.remove());
        }
    }
    
    private class SyncClient implements MyClient {
        private MessageConsumer consumer;
        private int count=0;
        public SyncClient(MessageConsumer consumer) {
            this.consumer = consumer;
        }
        public int getCount() { return count; }
        public Message getMessage() throws JMSException {
            Message message=consumer.receiveNoWait();
            if (message != null) {
                logger.debug("receive ({}):{}, level={}", 
                        ++count , message.getJMSMessageID(),  message.getStringProperty("level"));
                message.acknowledge();
            }
            return message;
        }
    }

    @Test
    public void testMessageSelector() throws Exception {
        logger.info("*** testMessageSelector ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection.stop();
            //need to use CLIENT_ACK to avoid race condition within this app
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            String selector1 = "level in ('warn', 'fatal')";
            asyncConsumer = session.createConsumer(destination, selector1);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            String selector2 = "level in ('debug', 'info','warn', 'fatal')";
            syncConsumer = session.createConsumer(destination, selector2);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            String levels[] = {"info", "warn", "fatal"}; //no "debug", 
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (String level : levels) {
                message.setStringProperty("level", level);
                producer.send(message);
                logger.info("sent msgId={}, level={}", 
                        message.getJMSMessageID(), message.getStringProperty("level"));
            }
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10; i++) {
                for(MyClient client: clients) {
                    Message m = client.getMessage();
                    receivedCount += (m != null ? 1 : 0);
                }
                if (receivedCount == 3) { break; }
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            logger.info("asyncClient received {} msgs", asyncClient.getCount());
            logger.info("syncClient received {} msgs", syncClient.getCount());
            assertEquals(3, asyncClient.getCount()+ syncClient.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (asyncConsumer != null) { asyncConsumer.close(); }
            if (syncConsumer != null) { syncConsumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
    
    @Test
    public void testMessageSelectorMulti() throws Exception {
        logger.info("*** testMessageSelectorMulti ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection.stop();
            //need to use CLIENT_ACK to avoid race condition within this app
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            String selector1 = "level in ('warn', 'fatal')";
            asyncConsumer = session.createConsumer(destination, selector1);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            String selector2 = "level in ('debug', 'info','warn', 'fatal')";
            syncConsumer = session.createConsumer(destination, selector2);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            String levels[] = {"info", "warn", "fatal"}; //no "debug",             
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (int i=0; i<msgCount; i++) {
                for (String level : levels) {
                    message.setStringProperty("level", level);
                    producer.send(message);
                    logger.info("sent msgId={}, level={}", 
                            message.getJMSMessageID(), message.getStringProperty("level"));
                }
            }
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10 || i<msgCount; i++) {
                for(MyClient client: clients) {
                    Message m=null;
                    do {
                       m = client.getMessage();
                       receivedCount += (m != null ? 1 : 0);
                    } while (m != null);
                }
                if (receivedCount == (3*msgCount)) { break; }
                logger.debug("waiting for messages...");
                Thread.sleep(10);
            }
            logger.info("asyncClient received {} msgs", asyncClient.getCount());
            logger.info("syncClient received {} msgs", syncClient.getCount());
            assertEquals(msgCount*3, 
                    asyncClient.getCount()+ syncClient.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (asyncConsumer != null) { asyncConsumer.close(); }
            if (syncConsumer != null) { syncConsumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }    
    
}
