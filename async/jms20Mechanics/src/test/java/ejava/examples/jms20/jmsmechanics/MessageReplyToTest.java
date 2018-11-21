package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs a demonstration of using a message time to live.
 * Messages will be sent and left in the server beyond a specified time to 
 * live.
 */
public class MessageReplyToTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageReplyToTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
        emptyQueue(destination);
    }
    
    private class Replier implements MessageListener {
        private int count=0;
        private JMSProducer producer;
        private Message reply;
        public void setContext(JMSContext context) throws JMSException {
            producer = context.createProducer();
            reply = context.createMessage();
        }
        public void onMessage(Message message) {
            try {
                logger.debug("onMessage received ({}): {}, replyTo={}", 
                        ++count , message.getJMSMessageID() , message.getJMSReplyTo());
                Destination replyDestination = message.getJMSReplyTo();
                reply.setIntProperty("count", count);
                reply.setJMSCorrelationID(message.getJMSMessageID());
                producer.send(replyDestination, reply);                
            } catch (JMSException ex) {
                logger.error("error handling message", ex);
            }
        }        
    }

    @Test
    public void testReplyTo() throws Exception {
        logger.info("*** testReplyTo ***");
        try (JMSContext context=createContext(Session.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(Session.CLIENT_ACKNOWLEDGE)) {
            context.stop();
            
            List<JMSConsumer> replyConsumers = new ArrayList<>();
            try (JMSConsumer consumer = context2.createConsumer(destination)) {
                //setup a client to consume and reply to published messages
                Replier client = new Replier();
                client.setContext(context2); //client needs a context to form replies
                consumer.setMessageListener(client);
                
                //create several temporary reply queues to receive responses
                Destination replyDestinations[] = {
                        context.createTemporaryQueue(),
                        context.createTemporaryQueue(),
                        context.createTemporaryQueue(),
                        context.createTemporaryQueue()
                };
                //create consumers for each of the temporary queues using senders context
                for(Destination replyTo : replyDestinations) {
                    replyConsumers.add(context.createConsumer(replyTo));
                }

                //send out requests and identify expected responses
                JMSProducer producer = context.createProducer();            
                Message message = context.createMessage();
                Map<String, Message> responses = new HashMap<String, Message>();
                int sendCount=0;
                for(Destination replyTo : replyDestinations) {
                    message.setJMSReplyTo(replyTo);
                    producer.send(destination, message);
                    responses.put(message.getJMSMessageID(), null);
                    logger.info("sent ({}) msgId={}, replyTo={}", 
                            ++sendCount , message.getJMSMessageID(), message.getJMSReplyTo());
                }
                
                context2.start();
                
                //verify that response table is empty
                assertEquals(sendCount, responses.size());
                for(String id : responses.keySet()) {
                    assertNull(responses.get(id));
                }

                //gather replies
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
                
                logger.info("sent={} messages, received={} messages", sendCount, totalCount);
                assertEquals(sendCount, totalCount);
                
                for(int d=0; d<receivedCount.length; d++) {
                    logger.info("replyTo {} received {} messages", replyDestinations[d], receivedCount[d]);
                    assertEquals(totalCount/receivedCount.length,receivedCount[d]); 
                }
                for(String id : responses.keySet()) {
                    assertNotNull(responses.get(id));
                }
            } finally {
                for (JMSConsumer rc: replyConsumers) {
                    rc.close();
                }
            }
        }
    }

    @Test
    public void testReplyToMulti() throws Exception {
        logger.info("*** testReplyToMulti ***");
        try (JMSContext context=createContext(Session.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(Session.CLIENT_ACKNOWLEDGE)) {
            context.stop();

            List<JMSConsumer> replyConsumers = new ArrayList<>();
            try (JMSConsumer consumer = context.createConsumer(destination)) {
                //put a client in place that will reply to our messages
                Replier client = new Replier();                
                client.setContext(context2); //set the JMS context so they can reply
                consumer.setMessageListener(client);
                
                    //create several temporary queues to receive replies
                Destination replyDestinations[] = {
                        context.createTemporaryQueue(),
                        context.createTemporaryQueue(),
                        context.createTemporaryQueue(),
                        context.createTemporaryQueue()
                };
                    //create a consumer for each of the queues
                for(Destination replyTo : replyDestinations) {
                    replyConsumers.add(context.createConsumer(replyTo));
                }

                    //send some requests with a distinct reply destination
                JMSProducer producer = context.createProducer();            
                Message message = context.createMessage();
                int sendCount=0;
                Map<String, Message> responses = new HashMap<String, Message>();
                for (int i=0; i<msgCount; i++) {
                    for(Destination replyTo : replyDestinations) {
                        message.setJMSReplyTo(replyTo);
                        producer.send(destination, message);
                        responses.put(message.getJMSMessageID(), null);
                        logger.info("sent ({}) msgId={}, replyTo={}", 
                                ++sendCount, message.getJMSMessageID(), message.getJMSReplyTo());
                    }
                }
                
                //verify that response table is empty
                assertEquals(sendCount, responses.size());
                for(String id : responses.keySet()) {
                    assertNull(responses.get(id));
                }
                
                    //gather responses
                context.start();
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
                    logger.debug("waiting for messages...{} of expected {}", totalCount, sendCount);
                }
                logger.info("sent={} messages, received={} messages",  sendCount, totalCount);
                assertEquals(sendCount, totalCount);
                
                for(int d=0; d<receivedCount.length; d++) {
                    logger.info("replyTo {} received {} messages", replyDestinations[d], receivedCount[d]);
                    assertEquals(totalCount/receivedCount.length,receivedCount[d]); 
                }
                for(String id : responses.keySet()) {
                    assertNotNull(responses.get(id));
                }
            }
        }
    }    

}
