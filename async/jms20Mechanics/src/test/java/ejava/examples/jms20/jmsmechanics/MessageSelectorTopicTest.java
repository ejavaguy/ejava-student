package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Topic;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs a demonstration of using a message selector with
 * a MessageConsumer and a Topic. In the specific case tested, the number
 * of messages received will be 2x the number sent for 'warn' and 'fatal' plus
 * 1x the number sent for 'info' plus zero for number sent for 'debug'.
 */
public class MessageSelectorTopicTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageSelectorTopicTest.class);
    protected Destination destination;        

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("null destination:" + topicJNDI, destination);
    }

    @Test
    public void testMessageSelector() throws Exception {
        logger.info("*** testMessageSelector ***");
        try (JMSContext context=createContext(JMSContext.CLIENT_ACKNOWLEDGE);
             JMSContext context2=context.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
            context.stop();
            
            String selector1 = "level in ('warn', 'fatal')";
            String selector2 = "level in ('debug', 'info','warn', 'fatal')";
            
            //need to use CLIENT_ACK to avoid race condition within this app
            try (JMSConsumer syncConsumer = context.createConsumer(destination, selector1);
                 JMSConsumer asyncConsumer = context2.createConsumer(destination, selector2))  {
                
                //create a client to asynchronous receive messages through onMessage() callbacks
                AsyncClient asyncClient = new AsyncClient();
                asyncConsumer.setMessageListener(asyncClient);
                
                //create a client to synchronously poll for messages            
                SyncClient syncClient = new SyncClient(syncConsumer);
                
                //send messages at varying levels
                String levels[] = {"info", "warn", "fatal"}; //no "debug"
                JMSProducer producer = context.createProducer();
                Message message = context.createMessage();
                for (String level : levels) {
                    message.setStringProperty("level", level);
                    producer.send(destination, message);
                    logger.info("sent msgId={}, level={}", 
                            message.getJMSMessageID(), message.getStringProperty("level"));
                }
                
                //gather sent messages
                context.start();
                int receivedCount=0;
                for(int i=0; i<10; i++) {
                    for(MyClient client: Arrays.asList(asyncClient, syncClient)) {
                        Message m = client.getMessage();
                        receivedCount += (m != null ? 1 : 0);
                    }
                    if (receivedCount == 5) { break; }
                    logger.debug("waiting for messages...");
                    Thread.sleep(1000);
                }
                assertEquals(2, syncClient.getCount());
                assertEquals(3, asyncClient.getCount());
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
                 JMSConsumer asyncConsumer = context2.createConsumer(destination, selector2))  {
               
               //create a client to asynchronous receive messages through onMessage() callbacks
               AsyncClient asyncClient = new AsyncClient();
               asyncConsumer.setMessageListener(asyncClient);
               
               //create a client to synchronously poll for messages            
               SyncClient syncClient = new SyncClient(syncConsumer);

               //send many messages with different properties
               String levels[] = {"info", "warn", "fatal"}; // no "debug",             
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
                       for (Message m=client.getMessage(); m!=null;) {
                           receivedCount += (m != null ? 1 : 0);
                           m = client.getMessage();
                       }
                   }
                   if (receivedCount == (3*msgCount + 2*msgCount)) { break; }
                   logger.debug("waiting for messages...");
                   Thread.sleep(10);
               }
               assertEquals(msgCount*3, asyncClient.getCount());
               assertEquals(msgCount*2, syncClient.getCount());
            }
        }
    }        
}
