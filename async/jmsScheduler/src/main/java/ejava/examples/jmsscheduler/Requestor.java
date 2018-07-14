package ejava.examples.jmsscheduler;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is used to simulate work being tasked to a scheduling queue. Each 
 * request will be tracked for a result.
 *
 * @author jcstaff
 */
public class Requestor implements Runnable, MessageListener {
    private static final Log log = LogFactory.getLog(Requestor.class);
    protected ConnectionFactory connFactory;
    protected Destination requestQueue;
    protected boolean stop = false;
    protected boolean stopped = false;
    protected boolean started = false;
    protected int count=0;
    protected String name;
    protected long sleepTime=10000;
    protected int maxCount=10;
    protected Map<String, Message> requests = new HashMap<String,Message>();
    protected int responseCount=0;
    protected long startTime=0;
    protected String username;
    protected String password;
        
    public Requestor(String name) {
        this.name = name;
    }
    public void setConnFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }
    public void setRequestQueue(Destination requestQueue) {
        this.requestQueue = requestQueue;
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
    protected Connection createConnection(ConnectionFactory connFactory) 
        throws Exception {
        return username==null ? 
        		connFactory.createConnection() :
        		connFactory.createConnection(username, password);
    }
    protected Destination getReplyTo(Session session) throws Exception {
        return session.createTemporaryQueue();
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
            connection = createConnection(connFactory);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(requestQueue);
            Destination replyTo = getReplyTo(session);
            MessageConsumer consumer = session.createConsumer(replyTo);
            consumer.setMessageListener(this);
            connection.start();
            stopped = stop = false;

            log.info("requester " + name + " starting: " +
                    "maxCount=" + maxCount +
                    ", sleepTime" + sleepTime);
            started = true;
            startTime=System.currentTimeMillis();
            while (!stop && (maxCount==0 || count < maxCount)) {
                MapMessage message = session.createMapMessage();
                message.setIntProperty("count", ++count);
                message.setInt("difficulty", count % 10);
                message.setJMSReplyTo(replyTo);
                synchronized (requests) {
                	producer.send(message);
                    requests.put(message.getJMSMessageID(), message);
                }
                if (sleepTime>=1000 || (count % 100==0)) {
                    log.debug("published message(" + count + "):" + 
                            message.getJMSMessageID());
                    log.debug("outstanding requests=" + requests.size());
                }
                Thread.sleep(sleepTime);
            }
            log.info("requester " + name + " stopping, count=" + count);
            while (requests.size() > 0) {
                log.debug("waiting for " + requests.size() +  
                          " outstanding responses");
                log.trace("requests=" + requests);
                Thread.sleep(3000);
            }
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
            log.fatal("error running " + name, ex);
        }
    }    

    /**
     * This method is used to asynchronously receive the responses to 
     * requests sent by the main loop.
     */
    public void onMessage(Message message) {
        try {
            String correlationID = message.getJMSCorrelationID();
            Message request=null;
            synchronized (requests) {
                request = requests.remove(correlationID);    
            }        

            if (request != null) {
                responseCount += 1;
                String worker = message.getStringProperty("worker");

                if (sleepTime>=1000 || (responseCount % 100==0)) {
                    log.debug("recieved response for:" + 
                            request.getIntProperty("count") +
                            ", from " + worker + 
                            ", outstanding=" + requests.size());
                }
            }
            else {
                log.warn("received unexpected response:" + correlationID);
            }
        } catch (Exception ex) {
            log.info("error processing message", ex);
        }
    }

    public static void main(String args[]) {
        boolean noExit=false;
        try {
            System.out.print("Requestor args:");
            for (String s: args) {
                System.out.print(s + " ");
            }
            System.out.println();
            String connFactoryJNDI=null;
            String requestQueueJNDI=null;
            String name="";
            Long sleepTime=null;
            Integer maxCount=null;
            String username=null;
            String password=null;
             for (int i=0; i<args.length; i++) {
                if ("-jndi.name.connFactory".equals(args[i])) {
                    connFactoryJNDI = args[++i];
                }
                else if ("-jndi.name.requestQueue".equals(args[i])) {
                    requestQueueJNDI=args[++i];
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
            else if (requestQueueJNDI==null) {
                throw new Exception("jndi.name.requestQueue not supplied");
            }            
            Requestor requestor = new Requestor(name);
            Context jndi = new InitialContext();
            requestor.setConnFactory(
                    (ConnectionFactory)jndi.lookup(connFactoryJNDI));
            requestor.setRequestQueue((Destination)jndi.lookup(requestQueueJNDI));
            if (maxCount!=null) {
                requestor.setMaxCount(maxCount);
            }
            if (sleepTime!=null) {
                requestor.setSleepTime(sleepTime);
            }
            requestor.setUsername(username);
            requestor.setPassword(password);
            requestor.execute();
        }
        catch (Exception ex) {
            log.fatal("",ex);
            if (noExit) {
            	throw new RuntimeException("requestor error", ex);
            }
            System.exit(-1);            
        }
    }
}
