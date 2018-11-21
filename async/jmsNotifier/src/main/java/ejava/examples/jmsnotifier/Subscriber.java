package ejava.examples.jmsnotifier;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.InvalidDestinationRuntimeException;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is used to listen to messages on a destination. You can control the
 * durability (topics only) and selector used using the properties.
 */
public class Subscriber implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Subscriber.class);
    protected ConnectionFactory connFactory;
    protected Destination destination;
    protected boolean stop = false;
    protected boolean stopped = false;
    protected boolean started = false;
    protected String name;
    protected int limitCount=0;
    protected long sleepTime=0;
    protected int maxCount=0;
    protected boolean durable=false;
    protected String selector=null;
    protected String username;
    protected String password;
        
    public Subscriber(String name) {
        this.name = name;
    }
    public void setConnFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }
    public void setDestination(Destination destination) {
        this.destination = destination;
    }    
    public int getCount() {
        return limitCount;
    }
    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
    public void setDurable(boolean durable) {
        this.durable = durable;
    }
    public void setSelector(String selector) {
        this.selector = selector;
    }
    public void clearMessages() {
        limitCount = 0;
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
    public void setUsername(String username) {
		this.username = username;
	}
    public void setPassword(String password) {
		this.password = password;
	}

    private JMSContext createContext(Integer sessionMode) {
        if (sessionMode!=null) {
            return username==null ?
                    connFactory.createContext(sessionMode) :
                    connFactory.createContext(username, password, sessionMode);            
        } else {
            return username==null ?
                    connFactory.createContext() :
                    connFactory.createContext(username, password);
        }
    }
    
    private JMSConsumer createConsumer(JMSContext context) {
        if (durable == false) {                
            try { context.unsubscribe(name); }
            catch (InvalidDestinationRuntimeException ex) {}
            return context.createConsumer(destination, selector);                
        }
        else {
            return context.createDurableConsumer((Topic)destination, 
                                                     name, selector, false);
        }                    
    }

    public void execute() throws Exception {
        try (JMSContext context=createContext(Session.AUTO_ACKNOWLEDGE)) {
            context.setClientID(name);
            
            try (JMSConsumer consumer=createConsumer(context)) {
                context.start();
                stopped = stop = false;
                logger.info("subscriber {} starting: durable={}, selector={}", 
                        name, durable, selector);
                started = true;
                
                while (!stop && (maxCount==0 || limitCount < maxCount)) {
                    Message message = consumer.receive(3000);
                    if (message != null) {
                        limitCount += 1;
                        Object countProp = message.getObjectProperty("count");
                        StringBuilder text = new StringBuilder();
                        text.append(name + " received message #" + limitCount +
                                ", msgId=" + message.getJMSMessageID() +
                                ", count property=" + countProp);
                        if (message instanceof TextMessage) {
                            text.append(", body=" 
                                    +((TextMessage)message).getText());
                        }
                        logger.debug(text.toString());
                        Thread.yield();
                    }      
                    if (sleepTime > 0) {
                        logger.debug("processing message for {}msecs", sleepTime);
                        Thread.sleep(sleepTime);
                    }
                }
            }

            logger.info("subscriber " + name + " stopping");
            context.stop();
        }
        finally {
            stopped = true;
            started = false;
        }
    }
    
    public void run() {
        try {
            execute();
        }
        catch (Exception ex) {
            logger.error("error running " + name, ex);
        }
    }    

    public static void main(String args[]) {
    	boolean noExit=false;
        try {
            String connFactoryJNDI=null;
            String destinationJNDI=null;
            String name="";
            Long sleepTime=null;
            Integer maxCount=null;
            Boolean durable=null;
            String selector=null;
            String username=null;
            String password=null;
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
                else if ("-name".equals(args[i])) {
                    name=args[++i];
                }
                else if ("-sleep".equals(args[i])) {
                    sleepTime=new Long(args[++i]);
                }
                else if ("-max".equals(args[i])) {
                    maxCount=new Integer(args[++i]);
                }
                else if ("-durable".equals(args[i])) {
                    durable=new Boolean(args[++i]);
                }
                else if ("-selector".equals(args[i])) {
                    selector=args[++i];
                }
                else if ("-username".equals(args[i])) {
                	username=args[++i];
                }
                else if ("-password".equals(args[i])) {
                	password=args[++i];
                }
                else if ("-noExit".equals(args[i])) {
                	noExit=true;
                }
            }
            if (connFactoryJNDI==null) { 
                throw new Exception("jndi.name.connFactory not supplied");
            }
            else if (destinationJNDI==null) {
                throw new Exception("jndi.name.destination not supplied");
            }            
            Subscriber subscriber = new Subscriber(name);
            Context jndi = new InitialContext();
            subscriber.setConnFactory(
                    (ConnectionFactory)jndi.lookup(connFactoryJNDI));
            subscriber.setDestination((Destination)jndi.lookup(destinationJNDI));
            if (maxCount!=null) {
                subscriber.setMaxCount(maxCount);
            }
            if (sleepTime!=null) {
                subscriber.setSleepTime(sleepTime);
            }
            if (durable!=null) {
                subscriber.setDurable(durable);
            }
            if (selector!=null) {
                subscriber.setSelector(selector);
            }
            subscriber.setUsername(username);
            subscriber.setPassword(password);
            subscriber.execute();
        }
        catch (Exception ex) {
            logger.error("",ex);
            System.exit(-1);            
            if (noExit) {
            	throw new RuntimeException("error in subscriber", ex);
            }
            System.exit(-1);
        }
    }
}
