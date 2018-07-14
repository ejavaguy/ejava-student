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
 *
 * @author jcstaff
 */
public class MessageSelectorQueueTest extends JMSTestBase {
    static Log log = LogFactory.getLog(MessageSelectorQueueTest.class);
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
                log.debug("onMessage received (" + ++count + 
                        "):" + message.getJMSMessageID() +
                        ", level=" + message.getStringProperty("level"));
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
                        "):" + message.getJMSMessageID() +
                        ", level=" + message.getStringProperty("level"));
                message.acknowledge();
            }
            return message;
        }
    }

    @Test
    public void testMessageSelector() throws Exception {
        log.info("*** testMessageSelector ***");
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
                log.info("sent msgId=" + message.getJMSMessageID() +
                        ", level=" + message.getStringProperty("level"));
            }
            
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10; i++) {
                for(MyClient client: clients) {
                    Message m = client.getMessage();
                    receivedCount += (m != null ? 1 : 0);
                }
                if (receivedCount == 3) { break; }
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            log.info("asyncClient received " +asyncClient.getCount()+ " msgs");
            log.info("syncClient received " +syncClient.getCount()+ " msgs");
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
        log.info("*** testMessageSelectorMulti ***");
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
                    log.info("sent msgId=" + message.getJMSMessageID() +
                            ", level=" + message.getStringProperty("level"));
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
                log.debug("waiting for messages...");
                Thread.sleep(10);
            }
            log.info("asyncClient received " +asyncClient.getCount()+ " msgs");
            log.info("syncClient received " +syncClient.getCount()+ " msgs");
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
