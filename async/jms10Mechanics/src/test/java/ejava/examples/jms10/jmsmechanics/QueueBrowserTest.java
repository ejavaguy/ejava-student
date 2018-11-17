package ejava.examples.jms10.jmsmechanics;

import static org.junit.Assert.*;


import java.util.Enumeration;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.examples.jms10.jmsmechanics.MessageCatcher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case provides a demonstration of the JMS QueueBrowser 
 * functionality. This object can walk through a JMS queue and perform 
 * an inspection of pending messages.
 *
 */
public class QueueBrowserTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(QueueBrowserTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
        
        catcher1 = createCatcher("receiver1", destination);
        catcher2 = createCatcher("receiver2", destination);
    }
    
    @After
    public void tearDown() throws Exception {
    	shutdownCatcher(catcher1);
    	shutdownCatcher(catcher2);
    }

    @Test
    public void testQueueBrowser() throws Exception {
        logger.info("*** testQueueBrowser ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                logger.info("sent msgId={}", message.getJMSMessageID());
            }
            
            QueueBrowser qbrowser = session.createBrowser((Queue)destination);
            int msgs=0;
            //pause here in case server is running a bit slow
            for(int tries=0;tries<3;tries++) {
	            for (Enumeration<?> e = qbrowser.getEnumeration(); e.hasMoreElements(); ) {
	                Message m = (Message) e.nextElement();
	                msgs += 1;
	                logger.debug("browsing message ({})={}", msgs, m.getJMSMessageID());
	            }
	            if (msgs==msgCount) { break; }
	            else { 
	                logger.debug("retrying queueBrowser, got {} out of {}", msgs, msgCount);
	            	msgs=0;
	            	qbrowser.close();
	            	qbrowser = session.createBrowser((Queue)destination); 
	            }
            }
            assertEquals("unexpected number nf queue browser messages", msgCount, msgs);
            
            //queues will hold messages waiting for delivery
            new Thread(catcher1).start();
            new Thread(catcher2).start();
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() +
                 catcher2.getMessages().size()< msgCount); i++) {
                logger.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(msgCount, 
                    catcher1.getMessages().size() +
                    catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
}
