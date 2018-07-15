package ejava.examples.jmsnotifier;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This issues messages to a specified destination. You can use many of the
 * properties to control the content of the message.
 *
 * @author jcstaff
 */
public class Publisher implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Publisher.class);
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
    
    public void execute() throws Exception {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try {
            connection = username==null ?
            		connFactory.createConnection() :
            		connFactory.createConnection(username, password);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            stopped = stop = false;

            log.info("publisher " + name + " starting: " +
                    "maxCount=" + maxCount +
                    ", sleepTime" + sleepTime);
            started = true;
            while (!stop && (maxCount==0 || count < maxCount)) {
                TextMessage message = session.createTextMessage();
                message.setIntProperty("count", ++count);
                message.setText("count = " + count);
                producer.send(message);
                log.debug("published message(" + count + "):" + 
                        message.getJMSMessageID());
                Thread.sleep(sleepTime);
            }
            log.info("publisher " + name + " stopping, count=" + count);
            connection.stop();
        }
        finally {
            stopped = true;
            started = false;
            if (producer != null)   { producer.close(); }
            if (session!=null){ session.close();}
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
            log.error("",ex);
            if (noExit) {
            	throw new RuntimeException("error in publisher", ex);
            }
            System.exit(-1);
        }
    }


}
