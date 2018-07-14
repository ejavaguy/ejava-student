package ejava.examples.jmsmechanics;

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
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a demonstration of the two mechanisms that a 
 * MessageConsumer has for receiving messages using a Topic.
 *
 * @author jcstaff
 */
public class MessageConsumerTopicTest extends JMSTestBase {
    static Log log = LogFactory.getLog(MessageConsumerTopicTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("null destination:" + topicJNDI, destination);
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
                log.debug("onMessage received (" + ++count + 
                        "):" + message.getJMSMessageID());
                messages.add(message);
                message.acknowledge();
            } catch (JMSException ex) {
                log.fatal("error handling message", ex);
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
                log.debug("receive (" + ++count + 
                        "):" + message.getJMSMessageID());
                message.acknowledge();
            }
            return message;
        }
    }

    @Test
    public void testMessageConsumer() throws Exception {
        log.info("*** testMessageConsumer ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            asyncConsumer = session.createConsumer(destination);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            syncConsumer = session.createConsumer(destination);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            producer.send(message);
            log.info("sent msgId=" + message.getJMSMessageID());
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10; i++) {
                for(MyClient client: clients) {
                    Message m = client.getMessage();
                    receivedCount += (m != null ? 1 : 0);
                }
                if (receivedCount == clients.size()) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, asyncClient.getCount());
            assertEquals(1, syncClient.getCount());
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
    public void testMessageConsumerMulti() throws Exception {
        log.info("*** testMessageConsumerMulti ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks
            asyncConsumer = session.createConsumer(destination);
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls
            syncConsumer = session.createConsumer(destination);
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            for (int i=0; i<msgCount; i++) {
                producer.send(message);
                log.info("sent msgId=" + message.getJMSMessageID());
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
                if (receivedCount == clients.size()*msgCount) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(10);
            }
            assertEquals(msgCount, asyncClient.getCount());
            assertEquals(msgCount, syncClient.getCount());
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
