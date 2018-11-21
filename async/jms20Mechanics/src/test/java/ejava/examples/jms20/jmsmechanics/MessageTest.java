package ejava.examples.jms20.jmsmechanics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test case performs a demonstration of using a each message type.
 */
public class MessageTest extends JMSTestBase {
    static final Logger logger = LoggerFactory.getLogger(MessageTest.class);
    protected Destination destination;        
    
    protected JMSContext context;
    protected JMSProducer producer;
    protected JMSConsumer consumer;
    protected JMSConsumer replyConsumer;
    protected Destination replyDestination;
    protected Replier client;

    @Before
    public void setUp() throws Exception {
        destination = (Queue) lookup(queueJNDI);
        assertNotNull("null destination:" + queueJNDI, destination);
        
        emptyQueue(destination);

        //setup replies
        context = createContext();
        consumer = context.createConsumer(destination);
        client = new Replier();
        client.setContext(context);
        consumer.setMessageListener(client);                    
        producer = context.createProducer();            
        replyDestination = context.createTemporaryQueue();
        replyConsumer = context.createConsumer(replyDestination);
        context.start();
    }
    
    @After
    public void tearDown() throws Exception {
        if (client != null) { client.close(); }
        if (context != null) { context.stop(); }
        if (replyConsumer != null) { replyConsumer.close(); }
        if (consumer != null) { consumer.close(); }
        if (context != null)  { context.close(); }
    }
    
    //this class is used to provide an example of a custom class sent within
    //a serializable payload
    private static class MyInteger implements Serializable {
        private static final long serialVersionUID = 6914811570078480852L;
        private int value;
        public MyInteger(int value) { this.value = value; }
        public int getValue() { return value; }
    }
    
    private class Replier implements MessageListener {
        private JMSProducer producer;
        public void setContext(JMSContext context) throws JMSException {
            producer = context.createProducer();
        }
        public void onMessage(Message request) {
            try {
                logger.debug("onMessage received:{}:{}", 
                        request.getJMSMessageID(), request.getClass().getName());
                Destination replyDestination = request.getJMSReplyTo();
                
                Message reply = null;
                if (request instanceof StreamMessage) {
                    reply = getReply((StreamMessage)request);
                }                
                else if (request instanceof MapMessage) {
                    reply = getReply((MapMessage)request);
                }                
                else if (request instanceof TextMessage) {
                    reply = getReply((TextMessage)request);
                }                
                else if (request instanceof BytesMessage) {
                    reply = getReply((BytesMessage)request);
                }                
                else if (request instanceof ObjectMessage) {
                    reply = getReply((ObjectMessage)request);
                }                
                else {
                    reply = getReply(request);
                }
                reply.setJMSCorrelationID(request.getJMSMessageID());
                producer.send(replyDestination, reply);
                
            } catch (Exception ex) {
                logger.error("error handling message", ex);
            }
        }        
        public void close() throws JMSException {
        }
        
        protected Message getReply(StreamMessage request) throws JMSException {
            String operator = request.readString();
            int operand1 = request.readInt();
            int operand2 = request.readInt();
            int result = ("add".equals(operator) ? operand1 + operand2 : -1);
            StreamMessage reply = context.createStreamMessage();
            reply.writeInt(result);
            return reply;
        }

        protected Message getReply(MapMessage request) throws JMSException {
            String operator = request.getString("operator");
            int operand1 = request.getInt("operand1");
            int operand2 = request.getInt("operand2");
            int result = ("add".equals(operator) ? operand1 + operand2 : -1);
            MapMessage reply = context.createMapMessage();
            reply.setInt("result", result);
            return reply;
        }

        protected Message getReply(TextMessage request) throws Exception {
            logger.debug("text request body={}", request.getText());
            Properties props = new Properties();
            props.load(new ByteArrayInputStream(request.getText().getBytes()));
            String operator = props.getProperty("operator");
            int operand1 = Integer.parseInt(props.getProperty("operand1"));
            int operand2 = Integer.parseInt(props.getProperty("operand2"));
            int result = ("add".equals(operator) ? operand1 + operand2 : -1);
            TextMessage reply = context.createTextMessage();
            reply.setText(new Integer(result).toString());
            return reply;
        }
        
        protected Message getReply(ObjectMessage request) throws Exception {
            logger.debug("object request body={}", request.getObject());
            @SuppressWarnings("unchecked")
			Map<String, Object> body = (Map<String, Object>)request.getObject();
            String operator =  (String)body.get("operator");
            int operand1 = ((MyInteger)body.get("operand1")).getValue();
            int operand2 = ((MyInteger)body.get("operand2")).getValue();
            int result = ("add".equals(operator) ? operand1 + operand2 : -1);
            ObjectMessage reply = context.createObjectMessage();
            reply.setObject(new MyInteger(result));
            return reply;
        }

        protected Message getReply(BytesMessage request) throws JMSException {
            logger.debug("body={} bytes", request.getBodyLength());
            byte buffer[] = new byte[10];
            request.readBytes(buffer, 3);
            String operator = new String(buffer);
            int operand1 = request.readByte();
            int operand2 = request.readByte();
            int result = (operator.startsWith("add") ? operand1 + operand2 : -1);
            BytesMessage reply = context.createBytesMessage();
            reply.writeInt(result);
            return reply;
        }
        
        protected Message getReply(Message request) throws JMSException {
            String operator = request.getStringProperty("operator");
            int operand1 = request.getIntProperty("operand1");
            int operand2 = request.getIntProperty("operand2");
            int result = ("add".equals(operator) ? operand1 + operand2 : -1);
            Message reply = context.createMessage();
            reply.setIntProperty("result", result);
            return reply;
        }
    }
    
    @Test
    public void testStreamMessage() throws Exception {
        logger.info("*** testStreamMessage ***");
        
        StreamMessage request = context.createStreamMessage();        
        request.writeString("add");
        request.writeInt(2);
        request.writeInt(3);

        request.setJMSReplyTo(replyDestination);
        producer.send(destination, request);

        StreamMessage response = (StreamMessage)replyConsumer.receive();
        int result = response.readInt();
        assertEquals("wrong answer:" + result, 5, result);
    }
    
    @Test
    public void testMapMessage() throws Exception {
        logger.info("*** testMapMessage ***");
        
        MapMessage request = context.createMapMessage();        
        request.setString("operator", "add");
        request.setInt("operand1", 2);
        request.setInt("operand2", 3);

        request.setJMSReplyTo(replyDestination);
        producer.send(destination, request);

        MapMessage response = (MapMessage)replyConsumer.receive();
        int result = response.getInt("result");
        assertEquals("wrong answer:" + result, 5, result);
    }

    @Test
    public void testTextMessage() throws Exception {
        logger.info("*** testTextMessage ***");
        
        TextMessage request = context.createTextMessage();
        Properties props = new Properties();
        props.put("operator", "add");
        props.put("operand1", new Integer(2).toString());
        props.put("operand2", new Integer(3).toString());
        StringWriter bodyText = new StringWriter();        
        props.list(new PrintWriter(bodyText));
        request.setText(bodyText.toString());

        request.setJMSReplyTo(replyDestination);
        producer.send(destination, request);

        TextMessage response = (TextMessage)replyConsumer.receive();
        String resultStr = response.getText();
        int result = Integer.parseInt(resultStr);
        assertEquals("wrong answer:" + result, 5, result);
    }

    @Test
    public void testObjectMessage() throws Exception {
        logger.info("*** testObjectMessage ***");
        
        ObjectMessage request = context.createObjectMessage();
        Map<String, Serializable> body = new HashMap<String, Serializable>();
        body.put("operator", "add");
        body.put("operand1", new MyInteger(2));  //use a custom class as an
        body.put("operand2", new MyInteger(3));  //example of serializable
        request.setObject((Serializable)body);

        request.setJMSReplyTo(replyDestination);
        producer.send(destination, request);

        ObjectMessage response = (ObjectMessage)replyConsumer.receive();
        int result = ((MyInteger)response.getObject()).getValue();
        assertEquals("wrong answer:" + result, 5, result);
    }

    @Test
    public void testBytesMessage() throws Exception {
        logger.info("*** testBytesMessage ***");
       
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write("add".getBytes());
        bos.write(2);
        bos.write(3);
        
        BytesMessage request = context.createBytesMessage();
        request.writeBytes(bos.toByteArray());

        request.setJMSReplyTo(replyDestination);
        producer.send(destination, request);

        BytesMessage response = (BytesMessage)replyConsumer.receive();
        int result = response.readInt();
        assertEquals("wrong answer:" + result, 5, result);
    }

    @Test
    public void testMessage() throws Exception {
        logger.info("*** testMessage ***");

        Message request = context.createMessage();        
        request.setStringProperty("operator", "add");
        request.setIntProperty("operand1", 2);
        request.setIntProperty("operand2", 3);

        request.setJMSReplyTo(replyDestination);
        producer.send(destination, request);

        Message response = replyConsumer.receive();
        int result = response.getIntProperty("result");
        assertEquals("wrong answer:" + result, 5, result);       
    }

}
