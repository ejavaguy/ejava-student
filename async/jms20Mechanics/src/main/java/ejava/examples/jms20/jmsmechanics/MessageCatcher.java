package ejava.examples.jms20.jmsmechanics;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a support class uses to receive messages by test cases that
 * are sending messages either to a queue or a topic.
 */
public class MessageCatcher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MessageCatcher.class);
    private String name;
    private JMSContext parentContext;
    private Destination destination;
    private int ackMode = Session.AUTO_ACKNOWLEDGE;
    private boolean stop;
    private boolean stopped;
    private boolean started;
    private List<Message> messages = new ArrayList<Message>();
        
    public MessageCatcher(String name) {
        this.name = name;
    }
    public String getName() { return name; }
    public MessageCatcher setContext(JMSContext context) {
        this.parentContext = context;
        return this;
    }    
    public void setDestination(Destination destination) {
        this.destination = destination;
    }    
    public MessageCatcher setAckMode(int ackMode) {
        this.ackMode = ackMode;
        return this;
    }
    public int getCount() {
        return messages.size();
    }
    public void clearMessages() {
        messages.clear();
    }
    public List<Message> getMessages() {
        return messages;
    }
    public void stop() {
        this.stop = true;
    }
    public boolean isStopped() {
        return stopped;
    }
    public boolean isStarted() {
        return started;
    }
    
    public void execute() throws JMSException {
        try (JMSContext context = parentContext.createContext(ackMode)) {
            try (JMSConsumer consumer = context.createConsumer(destination)) {
                context.start();
                stopped = stop = false;
                logger.info("catcher {} starting (ackMode={})", name, ackMode);
                started = true;
                for (int i=0;!stop; i++) {
                    if (i%30==0) { logger.debug("catcher {} looking for message", name); }
                    Message message = consumer.receive(100);
                    if (message != null) {
                        messages.add(message);
                        logger.debug("{} received message #{}, msgId={}", name, messages.size(), message.getJMSMessageID());
                        if (!stop) { Thread.yield(); }
                    }      
                }
            }
            
            logger.info("catcher {} stopping (ackMode={})", name, ackMode);
            if (ackMode == Session.CLIENT_ACKNOWLEDGE && messages.size() > 0) {
                logger.debug("catcher {} acknowledging messages", name);
                messages.get(messages.size()-1).acknowledge();
            }
            context.stop();
        }
        finally {
            stopped = true;
        }
    }
    
    public void run() {
        try {
            execute();
        }
        catch (Exception ex) {
            logger.error("error running " + name, ex);
            throw new RuntimeException("error running " + name, ex);
        }
    }
}
