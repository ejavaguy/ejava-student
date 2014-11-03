package ejava.examples.jmsmechanics;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a demonstration of using a message time to live.
 * Messages will be sent and left in the server beyond a specified time to 
 * live.
 *
 * @author jcstaff
 */
public class MessageReplyToTest extends JMSTestBase {
    static Log log = LogFactory.getLog(MessageReplyToTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
        emptyQueue();
    }
    
    protected void emptyQueue() throws JMSException {
        Session session = null;
        MessageConsumer consumer = null;
        try {
            session = connection.createSession(
                false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(destination);
            connection.start();
            Message message = null;
            do {
                message = consumer.receiveNoWait();
                log.debug("clearing old message" + message);
            } while (message != null);
            connection.stop();
        }
        finally {
            if (consumer != null) { consumer.close(); }
            if (session != null) { session.close(); }
        }
    }
    
    private class Replier implements MessageListener {
        private int count=0;
        private MessageProducer producer;
        private Message reply;
        public void setSession(Session session) throws JMSException {
            producer = session.createProducer(null);
            reply = session.createMessage();
        }
        public void onMessage(Message message) {
            try {
                log.debug("onMessage received (" + ++count + 
                        "):" + message.getJMSMessageID() +
                        ", replyTo=" + message.getJMSReplyTo());
                Destination replyDestination = message.getJMSReplyTo();
                reply.setIntProperty("count", count);
                reply.setJMSCorrelationID(message.getJMSMessageID());
                producer.send(replyDestination, reply);
                
            } catch (JMSException ex) {
                log.fatal("error handling message", ex);
            }
        }        
    }

    @Test
    public void testReplyTo() throws Exception {
        log.info("*** testReplyTo ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        List<MessageConsumer> replyConsumers = new ArrayList<MessageConsumer>();
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);

            consumer = session.createConsumer(destination);
            Replier client = new Replier();
            //set the JMS session so they can reply
            client.setSession(session);
            consumer.setMessageListener(client);
            
            
            producer = session.createProducer(destination);            
            Destination replyDestinations[] = {
                    session.createTemporaryQueue(),
                    session.createTemporaryQueue(),
                    session.createTemporaryQueue(),
                    session.createTemporaryQueue()
            };
            for(Destination replyTo : replyDestinations) {
                replyConsumers.add(session.createConsumer(replyTo));
            }

            Message message = session.createMessage();
            Map<String, Message> responses = new HashMap<String, Message>();
            int sendCount=0;
            for(Destination replyTo : replyDestinations) {
                message.setJMSReplyTo(replyTo);
                producer.send(message);
                responses.put(message.getJMSMessageID(), null);
                log.info("sent (" + ++sendCount + 
                    ")msgId=" + message.getJMSMessageID() +
                    ", replyTo=" + message.getJMSReplyTo());
            }
            
            connection.start();
            
            //verify that response table is rempty
            assertEquals(sendCount, responses.size());
            for(String id : responses.keySet()) {
                assertNull(responses.get(id));
            }

            int receivedCount[] = new int[replyDestinations.length];
            int totalCount=0;
            for(int d=0; d<replyDestinations.length; d++) {
                Message m = replyConsumers.get(d).receive(1000);
                if (m != null) {
                    responses.put(m.getJMSCorrelationID(), m);
                    receivedCount[d] += 1;
                    totalCount += 1;
                    m.acknowledge();
                }
            }
            log.info("sent=" + sendCount + " messages, received=" + 
                    totalCount + " messages");
            assertEquals(sendCount, totalCount);
            
            for(int d=0; d<receivedCount.length; d++) {
                log.info("replyTo " + replyDestinations[d] + " received " +
                        receivedCount[d] + " messages");
               assertEquals(totalCount/receivedCount.length,receivedCount[d]); 
            }
            for(String id : responses.keySet()) {
                assertNotNull(responses.get(id));
            }
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testReplyToMulti() throws Exception {
        log.info("*** testReplyToMulti ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        List<MessageConsumer> replyConsumers = new ArrayList<MessageConsumer>();
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.CLIENT_ACKNOWLEDGE);

            consumer = session.createConsumer(destination);
            Replier client = new Replier();
            //set the JMS session so they can reply
            client.setSession(session);
            consumer.setMessageListener(client);
            
            
            producer = session.createProducer(destination);            
            Destination replyDestinations[] = {
                    session.createTemporaryQueue(),
                    session.createTemporaryQueue(),
                    session.createTemporaryQueue(),
                    session.createTemporaryQueue()
            };
            for(Destination replyTo : replyDestinations) {
                replyConsumers.add(session.createConsumer(replyTo));
            }

            Message message = session.createMessage();
            int sendCount=0;
            Map<String, Message> responses = new HashMap<String, Message>();
            for (int i=0; i<msgCount; i++) {
                for(Destination replyTo : replyDestinations) {
                    message.setJMSReplyTo(replyTo);
                    producer.send(message);
                    responses.put(message.getJMSMessageID(), null);
                    log.info("sent (" + ++sendCount + 
                        ")msgId=" + message.getJMSMessageID() +
                        ", replyTo=" + message.getJMSReplyTo());
                }
            }
            
            //verify that response table is rempty
            assertEquals(sendCount, responses.size());
            for(String id : responses.keySet()) {
                assertNull(responses.get(id));
            }
            connection.start();

            int receivedCount[] = new int[replyDestinations.length];
            int totalCount=0;
            for(int i=0; i<10 || i<msgCount; i++) {
                for(int d=0; d<replyDestinations.length; d++) {
                    Message m = replyConsumers.get(d).receive(1000);
                    if (m != null) {
                        responses.put(m.getJMSCorrelationID(), m);
                        receivedCount[d] += 1;
                        totalCount += 1;
                        m.acknowledge();
                    }
                }
                if (totalCount == sendCount) { break; }
                log.debug("waiting for messages..." + totalCount +
                        " of expected " + sendCount);
            }
            log.info("sent=" + sendCount + " messages, received=" + 
                    totalCount + " messages");
            assertEquals(sendCount, totalCount);
            
            for(int d=0; d<receivedCount.length; d++) {
                log.info("replyTo " + replyDestinations[d] + " received " +
                        receivedCount[d] + " messages");
               assertEquals(totalCount/receivedCount.length,receivedCount[d]); 
            }
            for(String id : responses.keySet()) {
                assertNotNull(responses.get(id));
            }
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }    

}
