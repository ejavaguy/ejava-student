package ejava.examples.jmsscheduler;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is used to simulate work being tasked to a scheduling queue. Each 
 * request will be tracked for a result.
 */
public class Requestor implements Runnable, MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(Requestor.class);
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
    protected JMSContext createContext(Integer sessionMode) throws Exception {
        if (sessionMode==null) {
            return username==null ? 
            		connFactory.createContext() :
            		connFactory.createContext(username, password);
        } else {
            return username==null ? 
                    connFactory.createContext(sessionMode) :
                    connFactory.createContext(username, password, sessionMode);            
        }
    }
    public void setUsername(String username) {
		this.username = username;
	}
    public void setPassword(String password) {
		this.password = password;
	}
    public void execute() throws Exception {
        try (JMSContext context=createContext(Session.AUTO_ACKNOWLEDGE)) {
            JMSProducer producer = context.createProducer();
            Destination replyTo = context.createTemporaryQueue();
            
            try (JMSConsumer consumer = context.createConsumer(replyTo)) {
                consumer.setMessageListener(this);
                context.start();
                stopped = stop = false;
                
                logger.info("requester {} starting: maxCount={}, sleepTime {}", name, maxCount, sleepTime);
                started = true;
                startTime=System.currentTimeMillis();
                
                while (!stop && (maxCount==0 || count < maxCount)) {
                    MapMessage message = context.createMapMessage();
                    message.setIntProperty("count", ++count);
                    message.setInt("difficulty", count % 10);
                    message.setJMSReplyTo(replyTo);
                    synchronized (requests) {
                        producer.send(requestQueue, message);
                        requests.put(message.getJMSMessageID(), message);
                    }
                    if (sleepTime>=1000 || (count % 100==0)) {
                        logger.debug("published message(" + count + "):" + 
                                message.getJMSMessageID());
                        logger.debug("outstanding requests=" + requests.size());
                    }
                    Thread.sleep(sleepTime);
                }
                
                logger.info("requester {} stopping, count={}", name, count);
                while (requests.size() > 0) {
                    logger.debug("waiting for {} outstanding responses", requests.size());
                    logger.trace("requests={}", requests);
                    Thread.sleep(3000);
                }
                context.stop();
            }

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
            logger.error("error running {}", name, ex);
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
                    logger.debug("recieved response for:{}, from {}, outstanding={}", 
                            request.getIntProperty("count"), worker, requests.size());
                }
            }
            else {
                logger.warn("received unexpected response:{}" + correlationID);
            }
        } catch (Exception ex) {
            logger.info("error processing message", ex);
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
            logger.error("",ex);
            if (noExit) {
                throw new RuntimeException("requestor error", ex);
            }
            System.exit(-1);            
        }
    }
}
