package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs a demonstration of using a message delivery mode.
 * Timings will be taken using both PERSISTENT and NON_PERSISTENT modes.
 */
public class MessageDeliveryModeTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageDeliveryModeTest.class);
    protected Destination destination;        
    
    @Before
    public void setUp() throws Exception {
        logger.debug("getting jndi initial parentContext");
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
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
        logger.info("total messages per test={}", msgCount);
        for(Mode mode: hacks.keySet()) {
            long total = hacks.get(mode);
            StringBuilder text = new StringBuilder();
            text.append("mode:" + mode.name() + " total=" + total);
            if (msgCount > 0) {
                text.append("msecs , ave=" + 
                        (double)total/(double)msgCount + "msecs");
            }
            logger.info(text.toString());
        }
    }
    
    private long doTestProducerDeliveryMode(Mode mode) throws Exception {
        logger.info("*** testProducerDeliverMode:{} ***", mode.name());
        try (JMSContext context=createContext();
             JMSContext context2=context.createContext(Session.AUTO_ACKNOWLEDGE)) {
            context.stop();
            
            try (JMSConsumer consumer = context2.createConsumer(destination);) {
                //create a client to asynchronous receive messages through onMessage() callbacks
                AsyncClient client = new AsyncClient();
                consumer.setMessageListener(client);
                
                //send some messages with assigned delivery mode
                MapMessage message = context.createMapMessage();
                for(int i=0; i<26; i++) {
                    message.setChar("val" + i, (char)('a' + i));
                }
                logger.info("sending {} messages", msgCount);
                JMSProducer producer = context.createProducer()
                        .setPriority(Message.DEFAULT_PRIORITY)
                        .setTimeToLive(Message.DEFAULT_TIME_TO_LIVE)
                        .setDeliveryMode(mode.mode);
                long start=System.currentTimeMillis();
                for (int i=0; i<msgCount; i++) {
                    producer.send(destination, message);
                }
                long sendComplete=System.currentTimeMillis();
                

                    //gather the consumed messages
                context.start();
                int receivedCount=0;
                for(int i=0; i<10 || i<msgCount; i++) {
                    for (Message m=client.getMessage(); m!=null; ) {
                       receivedCount += (m != null ? 1 : 0);
                       m = client.getMessage();
                    }
                    if (receivedCount == (msgCount)) { break; }
                    logger.debug("waiting for messages...{}", client.getCount());
                    Thread.sleep(1000);
                }
                logger.info("client received {} msgs", client.getCount());
                assertEquals(msgCount, client.getCount());
                logger.info("total time to transmit={} msecs", (sendComplete - start));
                if (msgCount > 0) {
                    logger.info("ave time to transmit={} msecs", (sendComplete - start)/msgCount);
                }
                return (sendComplete - start);
            }
        }
    }    
}
