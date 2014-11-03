package ejava.ejb.examples.encconfig.ejb;

import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class XMLConfiguredEJB extends JNDIReader 
	implements JNDIReaderRemote {
	private static final Logger log = LoggerFactory.getLogger(XMLConfiguredEJB.class);
	private @Resource SessionContext ctx;
	
	private static final int DEFAULT_VALUE=1;
	private int value1=DEFAULT_VALUE;
	private String value2=""+DEFAULT_VALUE;
	private String value3;
	
	private DataSource ds1;   //injected
	private DataSource ds2;   //jndi.lookup
	private DataSource ds3;   //jboss-ejb3.xml
	
	private Queue queue1;
	private Queue queue2;
	private Queue queue3;
	
	private Topic topic1;
	private Topic topic2;
	private Topic topic3;
	
	private ConnectionFactory cf1;
	private ConnectionFactory cf2;
	private ConnectionFactory cf3;
	
	private InjectedEJB ejb1;
	private InjectedEJB ejb2;
	private InjectedEJB ejb3;
	private InjectedEJB ejb4;
	private InjectedEJB ejb5;
	private InjectedEJB ejb6;
	private InjectedEJB ejb7;
	private InjectedEJB ejb8;

	private EntityManager em1;
	private EntityManager em2;
	private EntityManager em3;
	
	private EntityManagerFactory emf1;
	private EntityManagerFactory emf2;
	private EntityManagerFactory emf3;
	
	public void init() {
		super.init();
		log.info("*** XMLConfiguredEJB ***");
		InitialContext jndi=null;
		try {
			jndi=new InitialContext();
			value2 = (String)ctx.lookup("java:comp/env/val/value2");
			value3 = (String)ctx.lookup("java:comp/env/val/value3");
			ds2 = (DataSource)ctx.lookup("java:comp/env/jdbc/ds2");
			ds3 = (DataSource)ctx.lookup("java:comp/env/jdbc/ds3");
			queue2 = (Queue)ctx.lookup("java:comp/env/jms/queue2");
			queue3 = (Queue)ctx.lookup("java:comp/env/jms/queue3");
			topic2 = (Topic)ctx.lookup("java:comp/env/jms/topic2");
			topic3 = (Topic)ctx.lookup("java:comp/env/jms/topic3");
			cf2 = (ConnectionFactory)ctx.lookup("java:comp/env/jms/cf2");
			cf3 = (ConnectionFactory)ctx.lookup("java:comp/env/jms/cf3");
			ejb2 = (InjectedEJB)ctx.lookup("java:comp/env/ejb/ejb2");
			ejb3 = (InjectedEJB)ctx.lookup("java:global/enc-config-example-ejb/InjectedEJB!ejava.ejb.examples.encconfig.ejb.InjectedEJB");
			ejb4 = (InjectedEJB)ctx.lookup("java:app/enc-config-example-ejb/InjectedEJB!ejava.ejb.examples.encconfig.ejb.InjectedEJB");
			ejb5 = (InjectedEJB)ctx.lookup("java:module/InjectedEJB!ejava.ejb.examples.encconfig.ejb.InjectedEJB");
			ejb6 = (InjectedEJB)ctx.lookup("java:global/enc-config-example-ejb/InjectedEJB");
			ejb7 = (InjectedEJB)ctx.lookup("java:app/enc-config-example-ejb/InjectedEJB");
			ejb8 = (InjectedEJB)ctx.lookup("java:module/InjectedEJB");
			em2 = (EntityManager)ctx.lookup("java:comp/env/jpa/em2");
			em3 = (EntityManager)ctx.lookup("java:comp/env/jpa/em3");
			emf2 = (EntityManagerFactory)ctx.lookup("java:comp/env/jpa/emf2");
			emf3 = (EntityManagerFactory)ctx.lookup("java:comp/env/jpa/emf3");
			
			verify(jndi, "val", "value", value1, value2, value3);
			assert(value1==DEFAULT_VALUE) : "value1 value replaced";
			assert(!value2.equals(""+DEFAULT_VALUE)) : "value2 value not replaced";
			verify(jndi, "jdbc", "ds", ds1, ds2, ds3);
			verify(jndi, "jms", "queue", queue1, queue2, queue3);
			verify(jndi, "jms", "topic", topic1, topic2, topic3);
			verify(jndi, "jms", "cf", cf1, cf2, cf3);
			verify(jndi, "ejb", "ejb", ejb1, ejb2, ejb3, ejb4, ejb5, ejb6, ejb7, ejb8);
			verify(jndi, "jpa", "em", em1, em2, em3);
			verify(jndi, "jpa", "emf", emf1, emf2, emf3);
			
		} catch (NamingException ex) {
			throw new EJBException(ex);
		}
		finally {
			if (jndi!=null) {
				try { jndi.close(); }
				catch (NamingException ex) { ex.printStackTrace(); }
			}
		}
	}
}
