package ejava.examples.jmsmechanics;

import static org.junit.Assert.*;


import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case demonstrates some basic use of message properties.
 */
public class MessagePropertiesTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessagePropertiesTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;
    
    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("null destination:" + topicJNDI, destination);
    }
    

    @Test
    public void testMessageProperties() throws Exception {
        logger.info("*** testMessageProperties ***");
        Session session = null;
        MessageProducer producer = null;
        MessageConsumer consumer = null;
        try {
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            consumer = session.createConsumer(destination);
            Message message = session.createMessage();
            
            message.setBooleanProperty("booleanProperty", true);
            message.setByteProperty("byteProperty", (byte)0x01);
            message.setDoubleProperty("doubleProperty", 1.01);
            message.setFloatProperty("floatProperty", (float)1.02);
            message.setIntProperty("intProperty", 3);
            message.setLongProperty("longProperty", 5L);
            message.setObjectProperty("intPropertyAsObject", 3);
            message.setShortProperty("shortProperty", (short)4);
            message.setStringProperty("stringProperty", "hello JMS world");            
            
            producer.send(message);            
            Message message2 = consumer.receive(1000);
            assertNotNull("no message received", message2);
            
            logger.debug("message2.JMSMessageID={}", message2.getJMSMessageID());
            logger.debug("message2.JMSTimestamp={}", message2.getJMSTimestamp());
            try { 
                logger.debug("message2.JMSCorrelationIDAsBytes={}", 
                        message2.getJMSCorrelationIDAsBytes());
            }
            catch (JMSException ex) {
                logger.debug("message2.JMSCorrelationIDAsBytes={}", ex.toString());
            }
            logger.debug("message2.JMSCorrelationID={}", message2.getJMSCorrelationID());
            logger.debug("message2.JMSReplyTo={}", message2.getJMSReplyTo());
            logger.debug("message2.JMSDestination={}", message2.getJMSDestination());
            logger.debug("message2.JMSDeliveryMode={}", message2.getJMSDeliveryMode());
            logger.debug("message2.JMSRedelivered={}", message2.getJMSRedelivered());
            logger.debug("message2.JMSType={}", message2.getJMSType());
            logger.debug("message2.JMSExpiration={}", message2.getJMSExpiration());
            logger.debug("message2.JMSPriority={}", message2.getJMSPriority());

            
            for(@SuppressWarnings("rawtypes")
			Enumeration e=message2.getPropertyNames();e.hasMoreElements();){
                String name = (String)e.nextElement();
                Object property = message2.getObjectProperty(name);
                logger.debug("message2.{} (:{})={}", name, property.getClass().getName(), property);
            }
            
            assertEquals(message2.getBooleanProperty("booleanProperty"), true);
            assertEquals(message2.getByteProperty("byteProperty"), (byte)0x01);
            assertEquals(message2.getDoubleProperty("doubleProperty"), 1.01, 0.01);
            assertEquals(message2.getFloatProperty("floatProperty"),
                    (float)1.02, 0.01);
            assertEquals(message2.getIntProperty("intProperty"), 3);
            assertEquals(message2.getLongProperty("longProperty"), 5L);
            assertEquals(message2.getObjectProperty("intPropertyAsObject"), 3);
            assertEquals(message2.getShortProperty("shortProperty"), (short)4);
            assertEquals(message2.getStringProperty("stringProperty"), 
                    "hello JMS world");            
        }
        finally {
            if (consumer != null) { consumer.close(); }
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }    
}
