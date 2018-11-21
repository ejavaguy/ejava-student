package ejava.examples.jmsnotifier;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This issues messages to a specified destination. You can use many of the
 * properties to control the content of the message.
 */
public class Publisher implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
    protected ConnectionFactory connFactory;
    protected Destination destination;
    protected boolean stop = false;
    protected boolean stopped = false;
    protected boolean started = false;
    protected int count=0;
    protected String name;
    protected long sleepTime=10000;
    protected int maxCount=10;
    protected String username;
    protected String password;
        
    public Publisher(String name) {
        this.name = name;
    }
    public void setConnFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }
    public void setDestination(Destination destination) {
        this.destination = destination;
    }    
    public int getCount() {
        return count;
    }
    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
    public void clearMessages() {
        count = 0;
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
    
    public void execute() throws Exception {
        try (JMSContext context = createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSProducer producer = context.createProducer();
            stopped = stop = false;

            logger.info("publisher {} starting: maxCount={}, sleepTime {}", 
                    name, maxCount, sleepTime);
            started = true;
            while (!stop && (maxCount==0 || count < maxCount)) {
                TextMessage message = context.createTextMessage();
                message.setIntProperty("count", ++count%4);
                message.setText("count = " + count);
                producer.send(destination, message);
                logger.debug("published message(" + count + "):" + 
                        message.getJMSMessageID());
                Thread.sleep(sleepTime);
            }
            logger.info("publisher {} stopping, limitCount={}", name, count);
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
            System.out.print("Publisher args:");
            for (String s: args) {
                System.out.print(s + " ");
            }
            System.out.println();
            String connFactoryJNDI=null;
            String destinationJNDI=null;
            String name="";
            Long sleepTime=null;
            Integer maxCount=null;
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
                else if ("-sleep".equals(args[i])) {
                    sleepTime=new Long(args[++i]);
                }
                else if ("-max".equals(args[i])) {
                    maxCount=new Integer(args[++i]);
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
            Publisher publisher = new Publisher(name);
            Context jndi = new InitialContext();
            publisher.setConnFactory(
                    (ConnectionFactory)jndi.lookup(connFactoryJNDI));
            publisher.setDestination((Destination)jndi.lookup(destinationJNDI));
            if (maxCount!=null) {
                publisher.setMaxCount(maxCount);
            }
            if (sleepTime!=null) {
                publisher.setSleepTime(sleepTime);
            }
            publisher.setUsername(username);
            publisher.setPassword(password);
            publisher.execute();
        }
        catch (Exception ex) {
            logger.error("",ex);
            if (noExit) {
            	throw new RuntimeException("error in publisher", ex);
            }
            System.exit(-1);
        }
    }


}
