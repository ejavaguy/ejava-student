package ejava.examples.jmsmechanics;

import static org.junit.Assert.*;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case performs a test of a transacted session using a topic. 
 * Receivers should not receive messages until they are committed by the 
 * sender.
 *
 * @author jcstaff
 */
public class TransactedTopicSessionTest extends JMSTestBase {
    static Logger log = LoggerFactory.getLogger(TransactedTopicSessionTest.class);
    protected Destination destination;        
    protected MessageCatcher catcher1;
    protected MessageCatcher catcher2;

    @Before
    public void setUp() throws Exception {
        destination = (Topic) lookup(topicJNDI);
        assertNotNull("null destination:" + topicJNDI, destination);
        
        catcher1 = createCatcher("subscriber1", destination);
        catcher2 = createCatcher("subscriber2", destination);
        
        //topics will only deliver messages to subscribers that are 
        //successfully registered prior to the message being published. We
        //need to wait for the catcher to start so it doesn't miss any 
        //messages.
        startCatcher(catcher1);
        startCatcher(catcher2);
    }
    
    protected void tearDown() throws Exception {
    	shutdownCatcher(catcher1);
    	shutdownCatcher(catcher2);
    }

    @Test
    public void testTransactedTopicSessionSend() throws Exception {
        log.info("*** testTransactedTopicSessionSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE);  //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            producer.send(message);
            log.info("sent msgId=" + message.getJMSMessageID());
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.commit(); //<!-- COMMITTING SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(1, catcher1.getMessages().size());
            assertEquals(1, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testRollbackTransactedTopicSessionSend() throws Exception {
        log.info("*** testRollbackTransactedTopicSessionSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE);  //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            producer.send(message);
            log.info("sent msgId=" + message.getJMSMessageID());
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.rollback(); //<!-- ROLLING BACK SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < 1 ||
                catcher2.getMessages().size() < 1); i++) {
                log.debug("waiting for rolled back messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
    
    @Test
    public void testTransactedTopicSessionMultiSend() throws Exception {
        log.info("*** testTransactedTopicSessionMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE); //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                log.info("sent msgId=" + message.getJMSMessageID());
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.commit(); //<!-- COMMITTING SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                log.debug("waiting for messages...");
                Thread.sleep(1000);
            }
            assertEquals(msgCount, catcher1.getMessages().size());
            assertEquals(msgCount, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }

    @Test
    public void testRolledbackTransactedTopicSessionMultiSend() throws Exception {
        log.info("*** testRolledbackTransactedTopicSessionMultiSend ***");
        Session session = null;
        MessageProducer producer = null;
        try {
            connection.stop();
            session = connection.createSession(
                    true, Session.AUTO_ACKNOWLEDGE); //<!-- TRUE=transacted
            producer = session.createProducer(destination);
            Message message = session.createMessage();
            
            catcher1.clearMessages();
            catcher2.clearMessages();
            for(int i=0; i<msgCount; i++) {
                producer.send(message);
                log.info("sent msgId=" + message.getJMSMessageID());
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
            session.rollback(); //<!-- ROLLBACK SESSION TRANSACTION
            for(int i=0; i<10 && 
                (catcher1.getMessages().size() < msgCount ||
                catcher2.getMessages().size() < msgCount); i++) {
                log.debug("waiting for rolledback messages...");
                Thread.sleep(1000);
            }
            assertEquals(0, catcher1.getMessages().size());
            assertEquals(0, catcher2.getMessages().size());
        }
        finally {
            if (producer != null) { producer.close(); }
            if (session != null)  { session.close(); }
        }
    }
}
