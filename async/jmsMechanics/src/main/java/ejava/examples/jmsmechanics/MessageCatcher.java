package ejava.examples.jmsmechanics;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a support class uses to receive messages by test cases that
 * are sending messages either to a queue or a topic.
 *
 * @author jcstaff
 */
public class MessageCatcher implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MessageCatcher.class);
    protected ConnectionFactory connFactory;
    protected String user;
    protected String password;
    protected Session sharedSession;
    protected Destination destination;
    protected int ackMode = Session.AUTO_ACKNOWLEDGE;
    protected boolean stop = false;
    protected boolean stopped = false;
    protected boolean started = false;
    protected List<Message> messages = new ArrayList<Message>();
    protected String name;
        
    public MessageCatcher(String name) {
        this.name = name;
    }
    public String getName() { return name; }
    public void setConnFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }
	public void setUser(String user) {
		this.user = user;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    public MessageCatcher setSession(Session session) {
        this.sharedSession = session;
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
    
    protected Connection getConnection() throws JMSException {
    	return user==null ? 
			connFactory.createConnection() : 
			connFactory.createConnection(user, password);
    }
    
    public void execute() throws JMSException {
        Connection connection = null;
        Session session = this.sharedSession;
        MessageConsumer consumer = null;
        try {
            if (session == null) {
                connection = getConnection();
                session = connection.createSession(false, ackMode);
            }
            consumer = session.createConsumer(destination);
            if (this.sharedSession == null) {
                connection.start();
            }
            stopped = stop = false;
            log.info("catcher " + name + " starting (ackMode=" + ackMode + ")");
            started = true;
            for (int i=0;!stop; i++) {
                if (i%30==0) { log.debug("catcher looking for message"); }
                Message message = consumer.receive(100);
                if (message != null) {
                    messages.add(message);
                    log.debug(name + " received message #" + messages.size() +
                            ", msgId=" + message.getJMSMessageID());
                    Thread.yield();
                }      
            }
            log.info("catcher " + name + " stopping (ackMode=" + ackMode + ")");
            if (ackMode == Session.CLIENT_ACKNOWLEDGE && messages.size() > 0) {
                log.debug("catcher " + name + " acknowledging messages");
                messages.get(messages.size()-1).acknowledge();
            }
            if (this.sharedSession == null) {
                connection.stop();
            }
        }
        finally {
            stopped = true;
            //started = false;
            if (consumer != null)   { consumer.close(); }
            if (this.sharedSession == null && session!=null){ session.close();}
            if (connection != null) { connection.close(); }
        }
    }
    
    public void run() {
        try {
            execute();
        }
        catch (Exception ex) {
            log.error("error running " + name, ex);
        }
    }    

    public static void main(String args[]) {
        try {
            String connFactoryJNDI=null;
            String destinationJNDI=null;
            String name="";
            for (int i=0; i<args.length; i++) {
                if ("-jndi.name.connFactory".equals(args[i])) {
                    connFactoryJNDI = args[++i];
                }
                else if ("-jndi.name.destination".equals(args[i])) {
                    destinationJNDI=args[++i];
                }
                else if ("-name".equals(args[i])) {
                    name=args[++i];
                }
            }
            if (connFactoryJNDI==null) { 
                throw new Exception("jndi.name.connFactory not supplied");
            }
            else if (destinationJNDI==null) {
                throw new Exception("jndi.name.destination not supplied");
            }            
            MessageCatcher catcher = new MessageCatcher(name);
            Context jndi = new InitialContext();
            catcher.setConnFactory(
                    (ConnectionFactory)jndi.lookup(connFactoryJNDI));
            catcher.setDestination((Destination)jndi.lookup(destinationJNDI));
            catcher.execute();
        }
        catch (Exception ex) {
            log.error("",ex);
            System.exit(-1);            
        }
    }
}
