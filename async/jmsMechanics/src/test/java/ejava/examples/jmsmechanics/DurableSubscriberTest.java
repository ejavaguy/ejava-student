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
 * This test case performs a demonstration of durable topic subscriptions. 
 *
 * @author jcstaff
 */
public class DurableSubscriberTest extends JMSTestBase {
    static Log log = LogFactory.getLog(DurableSubscriberTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("destination null:" + topicJNDI, destination);
        
        //close connection started by base
        if (connection !=null) { connection.close(); connection=null; }
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
            }
            return message;
        }
    }

    @Test
    public void testNonDurableSubscription() throws Exception {
        log.info("*** testNonDurableSubscription ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection = createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
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
            log.info("clients=" + clients);
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

            //now lets go away for a while
            asyncConsumer.close();
            syncConsumer.close();
            clients.clear();
            producer.close();
            session.close();
            connection.close(); connection=null;;
            
            //come back and send some messages
            connection = createConnection();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            message = session.createMessage();
            producer.send(message);
            log.info("clients=" + clients);
            log.info("sent msgId=" + message.getJMSMessageID());

            //now get in late for the messages
            asyncConsumer = session.createConsumer(destination);
            asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);
            syncConsumer = session.createConsumer(destination);
            syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);            
            producer = session.createProducer(destination);
            
            connection.start();
            receivedCount=0;
            for(int i=0; i<10; i++) {
                for(MyClient client: clients) {
                    Message m = client.getMessage();
                    receivedCount += (m != null ? 1 : 0);
                }
                if (receivedCount == clients.size()) { break; }
                log.debug("waiting for non-durable messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, asyncClient.getCount());
            assertEquals(0, syncClient.getCount());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (asyncConsumer != null) { asyncConsumer.close(); }
            if (syncConsumer != null) { syncConsumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
            if (connection != null) { connection.close(); connection=null;; }
        }
    }
    
    @Test
    public void testDurableSubscription() throws Exception {
        log.info("*** testNonDurableSubscription ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer asyncConsumer = null;
        MessageConsumer syncConsumer = null;
        try {
            connection = createConnection();
            //the Connection.clientID is needed for Durable Subscriptions 
            connection.setClientID("testDurableSubscription"); 
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            List<MyClient> clients = new ArrayList<MyClient>();
            //make sure we don't have pre-existing subscriptions
            try { session.unsubscribe("async1"); } catch(JMSException ignored){}
            try { session.unsubscribe("sync1"); } catch(JMSException ignored){}

            //create a client to asynchronous receive messages through 
            //onMessage() callbacks - USE A DURABLE TOPIC SUBSCRIPTION
            asyncConsumer = 
                session.createDurableSubscriber((Topic)destination,"async1");
            AsyncClient asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);

            //create a client to synchronously poll for messages with 
            //receive calls - USE A DURABLE TOPIC SUBSCRIPTION
            syncConsumer = 
                session.createDurableSubscriber((Topic)destination, "sync1");
            SyncClient syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);
            
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            producer.send(message);
            log.info("clients=" + clients);
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

            //now lets go away for a while
            asyncConsumer.close();
            syncConsumer.close();
            clients.clear();
            producer.close();
            session.close();
            connection.close(); connection=null;;
            
            //come back and send some messages
            connection = createConnection();
            //the Connection.clientID is needed for Durable Subscriptions 
            connection.setClientID("testDurableSubscription"); 
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            message = session.createMessage();
            producer.send(message);
            log.info("clients=" + clients);
            log.info("sent msgId=" + message.getJMSMessageID());

            //now get in late for the messages - RESUME DURABLE SUBSCRIPTION
            asyncConsumer = 
                session.createDurableSubscriber((Topic)destination,"async1");
            asyncClient = new AsyncClient();
            asyncConsumer.setMessageListener(asyncClient);
            clients.add(asyncClient);
            syncConsumer = 
                session.createDurableSubscriber((Topic)destination, "sync1");
            syncClient = new SyncClient(syncConsumer);
            clients.add(syncClient);            
            producer = session.createProducer(destination);
            
            connection.start();
            receivedCount=0;
            for(int i=0; i<10; i++) {
                for(MyClient client: clients) {
                    Message m = client.getMessage();
                    receivedCount += (m != null ? 1 : 0);
                }
                if (receivedCount == clients.size()) { break; }
                log.debug("waiting for durable messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, asyncClient.getCount());
            assertEquals(1, syncClient.getCount());
        }
        catch (Exception ex) {
        	log.error("error testing durable subscription", ex);
        	fail(ex.toString());
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (asyncConsumer != null) { asyncConsumer.close(); }
            if (syncConsumer != null) { syncConsumer.close(); }
            if (session != null) { session.unsubscribe("async1"); }
            if (session != null) { session.unsubscribe("sync1"); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
            if (connection != null) { connection.close(); connection=null;; }
        }
    }
    
}
