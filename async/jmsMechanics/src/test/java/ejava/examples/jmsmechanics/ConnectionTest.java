package ejava.examples.jmsmechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.ConnectionMetaData;

import org.junit.Test;

public class ConnectionTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(ConnectionTest.class);

    @Test
    public void testConnectionMetadata() throws Exception {
        logger.info("*** testConnectionMetadata ***");
        
        ConnectionMetaData cmd = connection.getMetaData();
        logger.info("connection.metaData={}", cmd);
        logger.info("connection.metaData.JMSMajorVersion={}", 
                cmd.getJMSMajorVersion());
        logger.info("connection.metaData.JMSMinorVersion={}", 
                cmd.getJMSMinorVersion());
        logger.info("connection.metaData.JMSProviderName={}", 
                cmd.getJMSProviderName());
        logger.info("connection.metaData.JMSVersion={}", 
                cmd.getJMSVersion());
        logger.info("connection.metaData.providerMajorVersion={}", 
                cmd.getProviderMajorVersion());
        logger.info("connection.metaData.providerMinorVersion={}", 
                cmd.getProviderMinorVersion());
        logger.info("connection.metaData.providerVersion={}", 
                cmd.getProviderVersion());
    }
    
}
