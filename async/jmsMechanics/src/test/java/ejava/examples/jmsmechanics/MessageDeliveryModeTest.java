package ejava.examples.jmsmechanics;

import java.util.HashMap;

import java.util.LinkedList;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MapMessage;
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
import static org.junit.Assert.*;

/**
 * This test case performs a demonstration of using a message delivery mode.
 * Timings will be taken using both PERSISTENT and NON_PERSISTENT modes.
 *
 * @author jcstaff
 */
public class MessageDeliveryModeTest extends JMSTestBase {
    static Logger log = LoggerFactory.getLogger(MessageDeliveryModeTest.class);
    protected Destination destination;        
    
    @Before
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
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
                count += 1;
                //if (count == 1 || count % 100 ==0) {
                //    log.debug("onMessage received (" + count + 
                //            "):" + message.getJMSMessageID() +
                //            ", mode=" + message.getJMSDeliveryMode());
                //}
                synchronized(messages) {
                    messages.add(message);
                }
            } catch (Exception ex) {
                log.fatal("error handling message", ex);
            }
        }        
        public int getCount() { return count; }
        public Message getMessage() {
            synchronized(messages) {
                return (messages.isEmpty() ? null : messages.remove());
            }
        }
    }
    
    private enum Mode { PERSISTENT(DeliveryMode.PERSISTENT),
                         NON_PERSISTENT(DeliveryMode.NON_PERSISTENT);
        private int mode;
        private Mode(int mode) { this.mode = mode; }
    }
    
    @Test
    public void testProducerDeliveryMode() throws Exception {
        Map<Mode,Long> hacks = new HashMap<Mode, Long>();
        for(int i=0; i<2; i++) {
            for(Mode mode : Mode.values()) {
               hacks.put(mode, doTestProducerDeliveryMode(mode)); 
            }
        }
        log.info("total messages per test=" + msgCount);
        for(Mode mode: hacks.keySet()) {
            long total = hacks.get(mode);
            StringBuilder text = new StringBuilder();
            text.append("mode:" + mode.name() + " total=" + total);
            if (msgCount > 0) {
                text.append("msecs , ave=" + 
                        (double)total/(double)msgCount + "msecs");
            }
            log.info(text.toString());
        }
    }
    public long doTestProducerDeliveryMode(Mode mode) throws Exception {
        log.info("*** testProducerDeliverMode:"+ mode.name() + " ***");
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
            
            producer = session.createProducer(destination);
            MapMessage message = session.createMapMessage();
            for(int i=0; i<26; i++) {
                message.setChar("val" + i, (char)('a' + i));
            }
            long start=System.currentTimeMillis();
            log.info("sending " + msgCount + " messages");
            for (int i=0; i<msgCount; i++) {
                producer.send(message,
                        mode.mode,
                        Message.DEFAULT_PRIORITY,
                        Message.DEFAULT_TIME_TO_LIVE);
            }
            long sendComplete=System.currentTimeMillis();
                        
            connection.start();
            int receivedCount=0;
            for(int i=0; i<10 || i<msgCount; i++) {
                Message m=null;
                do {
                   m = client.getMessage();
                   receivedCount += (m != null ? 1 : 0);
                } while (m != null);
                if (receivedCount == (msgCount)) { break; }
                log.debug("waiting for messages..." + client.getCount());
                Thread.sleep(1000);
            }
            log.info("client received " +client.getCount()+ " msgs");
            assertEquals(msgCount, 
                    client.getCount());
            log.info("total time to transmit=" + 
                    (sendComplete - start) + "msecs");
            if (msgCount > 0) {
                log.info("ave time to transmit=" + 
                        (sendComplete - start)/msgCount + "msecs");
            }
            return (sendComplete - start);
        }
        finally {
            if (connection != null) { connection.stop(); }
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }    
}
