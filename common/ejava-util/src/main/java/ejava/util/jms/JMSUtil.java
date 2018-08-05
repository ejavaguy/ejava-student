package ejava.util.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMSUtil {
    private static final Logger logger = LoggerFactory.getLogger(JMSUtil.class);
    
    /**
     * Performs a JMS createConnection and will wait supplied number of seconds before 
     * giving up. This gives a starting server time to start during IT tests.
     * @param connFactory
     * @param user
     * @param password
     * @param waitSecs
     * @return connection created. Caller must close.
     * @throws JMSException 
     */
    public static Connection createConnection(ConnectionFactory connFactory, String user, String password, int waitSecs) 
            throws JMSException {
        logger.debug("creating JMS connection waitSecs={}", waitSecs);
        
        Connection connection=null;
        //wait increments should be at least 1sec
        long interval=Math.max(waitSecs*1000/10, 1000);
        for (int elapsed=0; elapsed<(waitSecs*1000); elapsed += interval) {
            if (elapsed + interval < waitSecs*1000) {
                try {
                    connection = user==null ?
                            connFactory.createConnection() :
                            connFactory.createConnection(user, password);
                } catch (Throwable ex) {
                    logger.debug("waiting for connFactory.createConnection({})={}", connFactory.getClass().getSimpleName(), ex.getMessage());
                    try { Thread.sleep(interval); } catch (Exception ex2) {}
                }
            } else {
                connection = user==null ?
                        connFactory.createConnection() :
                        connFactory.createConnection(user, password);
            }
        }
        return connection;
    }
}
