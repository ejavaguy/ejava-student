package ejava.examples.jmsscheduler;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class simulates the job of a worker. It will attempt to take a message
 * off the queue, work on it, and issue a reply. The length of time taken on 
 * each message will vary per message based on a difficulty index. The worker
 * will quite when it hits its max value; always failing to repond to the last
 * request processed (on purpose).
 *
 * @author jcstaff
 */
public class Worker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Worker.class);
    protected ConnectionFactory connFactory;
    protected Destination requestQueue;
    protected Destination dlq;
    protected boolean stop = false;
    protected boolean stopped = false;
    protected boolean started = false;
    protected boolean noFail = false;
    protected String name;
    protected int count=0;
    protected int maxCount=0;
    protected long delay[] = {0, 0, 0, 0, 10, 10, 10, 10, 100, 100}; 
    protected String username;
    protected String password;

    public Worker(String name) {
        this.name = name;
    }
    public void setConnFactory(ConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }
    public void setRequestQueue(Destination requestQueue) {
        this.requestQueue = requestQueue;
    }    
    public void setDLQ(Destination dlq) {
        this.dlq = dlq;
    }    
    public int getCount() {
        return count;
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
    public void setNoFail(boolean noFail) {
    	this.noFail = noFail;
    }
    protected Connection createConnection(ConnectionFactory connFactory) 
        throws Exception {
        return username==null ? 
        		connFactory.createConnection() :
        		connFactory.createConnection(username, password);
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
        MessageConsumer consumer = null;
        MessageProducer producer = null;
        MessageProducer dlqProducer = null;
        try {
            connection = createConnection(connFactory);
            //use a transacted session to join request/response in single Tx
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(requestQueue);
            producer = session.createProducer(null);
            dlqProducer = session.createProducer(dlq);
            connection.start();

            stopped = stop = false;
            log.info("worker " + name + " starting");
            started = true;
            while (!stop && (maxCount==0 || count < maxCount)) {
                Message message = consumer.receive(3000);
                if (message != null) {
                    count += 1;                     
                    try {
                        MapMessage request = (MapMessage)message;
                        int difficulty = request.getInt("difficulty");
                        long sleepTime = delay[difficulty];
                        int requestCounter = request.getIntProperty("count");
                        StringBuilder text = new StringBuilder();
                        Destination replyTo = request.getJMSReplyTo();
                        text.append(name + " received message #" + count +
                            ", req=" + requestCounter +
                            ", replyTo=" + replyTo +
                            ", delay=" + sleepTime);
                        log.debug(text.toString());
                        Thread.sleep(sleepTime);
                        if (count < maxCount || maxCount==0 || noFail){//fail on last one
                            Message response = session.createMessage();
                            response.setJMSCorrelationID(
                                    request.getJMSMessageID());
                            response.setStringProperty("worker", name);
                            try {
                                producer.send(replyTo, response);
                            }
                            catch (JMSException ex) {
                                log.error("error sending reply:" + ex);                                
                                dlqProducer.send(request);
                            }
                            finally {
                                log.debug("committing session for: " + request.getJMSMessageID());
                                session.commit();
                            }
                        }
                    }
                    catch (Exception ex) {
                        log.error("error processing request:" + ex);
                        dlqProducer.send(message);
                        log.debug("committing session");
                        session.commit();
                    }
                    Thread.yield();
                }      
            }
            log.info("worker " + name + " stopping");
            connection.stop();
        }
        finally {
            stopped = true;
            started = false;
            if (consumer != null)   { consumer.close(); }
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
            System.out.print("Worker args:");
            for (String s: args) {
                System.out.print(s + " ");
            }

            String connFactoryJNDI=null;
            String requestQueueJNDI=null;
            String dlqJNDI=null;
            String name="";
            Integer maxCount=null;
            boolean noFail=false;
            String username=null;
            String password=null;
            for (int i=0; i<args.length; i++) {
                if ("-jndi.name.connFactory".equals(args[i])) {
                    connFactoryJNDI = args[++i];
                }
                else if ("-jndi.name.requestQueue".equals(args[i])) {
                    requestQueueJNDI=args[++i];
                }
                else if ("-jndi.name.DLQ".equals(args[i])) {
                    dlqJNDI=args[++i];
                }
                else if ("-name".equals(args[i])) {
                    name=args[++i];
                }
                else if ("-max".equals(args[i])) {
                    maxCount=new Integer(args[++i]);
                }
                else if ("-noFail".equals(args[i])) {
                	noFail=Boolean.parseBoolean(args[++i]);
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
            else if (dlqJNDI==null) {
                throw new Exception("jndi.name.DLQ not supplied");
            }            
            Worker worker = new Worker(name);
            Context jndi = new InitialContext();
            worker.setConnFactory(
                    (ConnectionFactory)jndi.lookup(connFactoryJNDI));
            worker.setRequestQueue((Destination)jndi.lookup(requestQueueJNDI));
            worker.setDLQ((Destination)jndi.lookup(dlqJNDI));
            worker.setNoFail(noFail);
            if (maxCount!=null) {
                worker.setMaxCount(maxCount);
            }
            worker.setUsername(username);
            worker.setPassword(password);
            worker.execute();
        }
        catch (Exception ex) {
            log.error("",ex);
            if (noExit) {
            	throw new RuntimeException("worker error", ex);
            }
            System.exit(-1);            
        }
    }


}
