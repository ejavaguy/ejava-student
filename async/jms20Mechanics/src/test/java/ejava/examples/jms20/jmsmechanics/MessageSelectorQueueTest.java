package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @Test
    public void testMessageSelector() throws Exception {
        logger.info("*** testMessageSelector ***");
            //need to use CLIENT_ACK to avoid race condition within this app
        try (JMSContext context=createContext(JMSContext.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
            context.stop();
            
            String selector1 = "level in ('warn', 'fatal')";
            String selector2 = "level in ('debug', 'info','warn', 'fatal')";
            
            try (JMSConsumer syncConsumer = context.createConsumer(destination, selector1);
                 JMSConsumer asyncConsumer = context2.createConsumer(destination, selector2)) {
                
                //create a client to asynchronous receive messages through onMessage() callbacks
                AsyncClient asyncClient = new AsyncClient();
                asyncConsumer.setMessageListener(asyncClient);
                
                //create a client to synchronously poll for messages
                SyncClient syncClient = new SyncClient(syncConsumer);

                //send messages at varying levels
                String levels[] = {"info", "warn", "fatal"}; //no "debug", 
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                for (String level : levels) {
                    message.setStringProperty("level", level);
                    producer.send(destination, message);
                    logger.info("sent msgId={}, level={}", 
                            message.getJMSMessageID(), message.getStringProperty("level"));
                }
                
                //gather messages sent
                context.start();
                int receivedCount=0;
                for(int i=0; i<10; i++) {
                    for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
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
        }
    }
    
    @Test
    public void testMessageSelectorMulti() throws Exception {
        logger.info("*** testMessageSelectorMulti ***");
        try (JMSContext context=createContext(JMSContext.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
            context.stop();
            
            String selector1 = "level in ('warn', 'fatal')";
            String selector2 = "level in ('debug', 'info','warn', 'fatal')";
            
            //need to use CLIENT_ACK to avoid race condition within this app
            try (JMSConsumer syncConsumer = context.createConsumer(destination, selector1);
                 JMSConsumer asyncConsumer = context2.createConsumer(destination, selector2)) {
                //create a client to asynchronous receive messages through onMessage() callbacks
                AsyncClient asyncClient = new AsyncClient();
                asyncConsumer.setMessageListener(asyncClient);
                
                //create a client to synchronously poll for messages
                SyncClient syncClient = new SyncClient(syncConsumer);

                //send some messages at varing level property values
                String levels[] = {"info", "warn", "fatal"}; //no "debug",             
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                for (int i=0; i<msgCount; i++) {
                    for (String level : levels) {
                        message.setStringProperty("level", level);
                        producer.send(destination, message);
                        logger.info("sent msgId={}, level={}", 
                                message.getJMSMessageID(), message.getStringProperty("level"));
                    }
                }
                
                //gather sent messages
                context.start();
                int receivedCount=0;
                for(int i=0; i<10 || i<msgCount; i++) {
                    for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                        for (Message m=client.getMessage();m!=null;) {
                           receivedCount += (m != null ? 1 : 0);
                           m = client.getMessage();
                        }
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
        }
    }    
}
